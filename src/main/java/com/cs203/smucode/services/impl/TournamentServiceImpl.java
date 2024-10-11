package com.cs203.smucode.services.impl;

import com.cs203.smucode.constants.Status;
import com.cs203.smucode.exceptions.TournamentNotFoundException;
import com.cs203.smucode.models.Bracket;
import com.cs203.smucode.models.PlayerInfo;
import com.cs203.smucode.models.Round;
import com.cs203.smucode.models.Tournament;
import com.cs203.smucode.repositories.TournamentServiceRepository;
import com.cs203.smucode.services.BracketService;
import com.cs203.smucode.services.RoundService;
import com.cs203.smucode.services.TournamentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class TournamentServiceImpl implements TournamentService {
    private final TournamentServiceRepository tournamentServiceRepository;
    private final RoundService roundService;
    private final BracketService bracketService;

    @Autowired
    public TournamentServiceImpl(TournamentServiceRepository tournamentServiceRepository,
                                 RoundService roundService,
                                 BracketService bracketService) {
        this.tournamentServiceRepository = tournamentServiceRepository;
        this.roundService = roundService;
        this.bracketService = bracketService;
    }

    public List<Tournament> findAllTournaments() {
        return tournamentServiceRepository.findAll();
    }

    public Tournament findTournamentById(UUID id) {
        Optional<Tournament> tournament = tournamentServiceRepository.findById(id);
        if (tournament.isEmpty()) {
            throw new TournamentNotFoundException("Tournament with id " + id + " not found");
        }
        return tournament.get();
    }

    public List<Tournament> findAllTournamentsByOrganiser(String organiser) {
        return tournamentServiceRepository.findByOrganiser(organiser);
    }

    public List<Tournament> findAllTournamentsByStatus(Status status) {
        return tournamentServiceRepository.findByStatus(status);
    }

    public List<Tournament> findAllTournamentsByParticipant(String participant) {
        return tournamentServiceRepository.findByParticipants(participant).stream().toList();
    }

    public Tournament createTournament(Tournament tournament) {

        // TODO: data insert validation
        if (tournament == null) { return null; }

        tournamentServiceRepository.save(tournament);

        createRounds(tournament); // generate rounds

        return tournament;
    }

    public Tournament updateTournament(UUID id, Tournament tournament) {
        return tournamentServiceRepository.findById(id).map(existingTournament -> {
            existingTournament.setName(tournament.getName());
            existingTournament.setDescription(tournament.getDescription());
            existingTournament.setStartDate(tournament.getStartDate());
            existingTournament.setEndDate(tournament.getEndDate());
            existingTournament.setFormat(tournament.getFormat());
            existingTournament.setCapacity(tournament.getCapacity());
            existingTournament.setIcon(tournament.getIcon());
            existingTournament.setOrganiser(tournament.getOrganiser());
            existingTournament.setTimeWeight(tournament.getTimeWeight());
            existingTournament.setMemWeight(tournament.getMemWeight());
            existingTournament.setTestCaseWeight(tournament.getTestCaseWeight());
            existingTournament.setStatus(tournament.getStatus());
            existingTournament.setSignupStartDate(tournament.getSignupStartDate());
            existingTournament.setSignupEndDate(tournament.getSignupEndDate());
//            existingTournament.setSignupStatus(tournament.getSignupStatus());
            existingTournament.setBand(tournament.getBand());
            existingTournament.setSignups(tournament.getSignups());
            existingTournament.setCurrentRound(tournament.getCurrentRound());

//            Set<String> signups = existingTournament.getSignups();
//            signups.addAll(tournament.getSignups());
//            existingTournament.setSignups(signups);

            return tournamentServiceRepository.save(existingTournament);
        }).orElseThrow(() -> new TournamentNotFoundException("Tournament not found with id: " + id));
    }

    public Tournament addTournamentSignup(UUID id, String signup) {
        return tournamentServiceRepository.findById(id).map(existingTournament -> {
            Set<String> existingSignups = existingTournament.getSignups();
            existingSignups.add(signup);
            existingTournament.setSignups(existingSignups);

            return tournamentServiceRepository.save(existingTournament);
        }).orElseThrow(() -> new TournamentNotFoundException("Tournament not found with id: " + id));
    }

    public Tournament deleteTournamentSignup(UUID id, String signup) {
        return tournamentServiceRepository.findById(id).map(existingTournament -> {
            Set<String> existingSignups = existingTournament.getSignups();
            existingSignups.remove(signup);
//            TODO: handle exception of user not being in signups
            existingTournament.setSignups(existingSignups);

            return tournamentServiceRepository.save(existingTournament);
        }).orElseThrow(() -> new TournamentNotFoundException("Tournament not found with id: " + id));
    }

//    progress tournament (when round ends)
    public Tournament updateTournamentProgress(UUID id) {
        Optional<Tournament> tournamentOptional = tournamentServiceRepository.findById(id);

        if (tournamentOptional.isEmpty()) {
            throw new TournamentNotFoundException("Tournament with id " + id + " not found");
        }

//        get current round
        Tournament tournament = tournamentOptional.get();
        String currRoundName = tournament.getCurrentRound();
        Round currRound = roundService.findRoundByTournamentIdAndName(id, currRoundName);
        UUID currRoundId = currRound.getId();
        int currRoundSeqId = currRound.getSeqId();
//        update current round status
        currRound.setStatus(Status.COMPLETED);
        roundService.updateRound(currRoundId, currRound);

//        final round
        if (currRoundName.equals("Round of 2")) {
//            TODO: tournament complete logic
            return tournament;
        }

//        get next round
        Round nextRound = roundService.findRoundByTournamentIdAndSeqId(id, currRoundSeqId+1);
        UUID nextRoundId = nextRound.getId();

//        move winners to respective brackets
        for (int i = 1; i <= nextRound.getBrackets().size(); i++) {
            Bracket newBracket = bracketService.findBracketByRoundIdAndSeqId(nextRoundId, i);

            // TODO: round progression validation (whether previous round has finished - null winner)
            String player1 = bracketService.findBracketByRoundIdAndSeqId(currRoundId, i*2 - 1).getWinner();
            String player2 = bracketService.findBracketByRoundIdAndSeqId(currRoundId, i*2).getWinner();

            newBracket.setPlayers(new ArrayList<>(
                    List.of(
                            new PlayerInfo(player1, 0),
                            new PlayerInfo(player2, 0)
                    )
            ));

            bracketService.updateBracket(newBracket.getId(), newBracket);

        }

        tournament.setCurrentRound(nextRound.getName());
        tournamentServiceRepository.save(tournament);

        return tournament;

    }

    public void deleteTournamentById(UUID id) { tournamentServiceRepository.deleteById(id); }

    public List<Tournament> findTournamentsBySignUpDeadline(LocalDateTime dateTime, String status) {
        return tournamentServiceRepository.findBySignupEndDateBeforeAndStatus(dateTime, status);
    }

//    helper classes
    List<Round> createRounds(Tournament tournament) {
        // generate list of rounds
        List<Integer> roundSizes = new ArrayList<>();
        int capacity = tournament.getCapacity();
        while (capacity > 1) {
            roundSizes.add(capacity);
            capacity /= 2;
        }

        List<Round> createdRounds = new ArrayList<>();
        for (int roundSize : roundSizes) {
            Round round = new Round();
            round.setTournament(tournament);
            round.setName("Round of " + roundSize);

            Round createdRound = roundService.createRound(round);
            createdRounds.add(createdRound);

        }
        return createdRounds;

    }
}