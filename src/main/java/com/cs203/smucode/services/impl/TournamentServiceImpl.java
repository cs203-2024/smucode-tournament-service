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
        return tournamentServiceRepository.findById(id).orElseThrow(() ->
                new TournamentNotFoundException("Tournament with id " + id + " not found"));
    }

    public List<Tournament> findAllTournamentsByOrganiser(String organiser) {
        return tournamentServiceRepository.findByOrganiser(organiser).orElse(null);
    }

    public List<Tournament> findAllTournamentsByStatus(Status status) {
        return tournamentServiceRepository.findByStatus(status).orElse(null);
    }

    public List<Tournament> findAllTournamentsByParticipant(String participant) {
        return tournamentServiceRepository.findByParticipant(participant).orElse(null);
    }

    public Tournament createTournament(Tournament tournament) {

        // TODO: data insert validation
        if (tournament == null) { return null; }

        tournamentServiceRepository.save(tournament);

        createRounds(tournament); // generate rounds

        return tournament;
    }

    public Tournament updateTournament(UUID id, Tournament tournament) {
        Optional<Tournament> tournamentOptional = tournamentServiceRepository.findById(id);

        if (tournamentOptional.isEmpty()) {
            throw new TournamentNotFoundException("Tournament with id " + id + " not found");
        }

        Tournament tournamentToUpdate = tournamentOptional.get();

        tournamentToUpdate.setName(tournament.getName());
        tournamentToUpdate.setDescription(tournament.getDescription());
        tournamentToUpdate.setStartDate(tournament.getStartDate());
        tournamentToUpdate.setEndDate(tournament.getEndDate());
        tournamentToUpdate.setFormat(tournament.getFormat());
        tournamentToUpdate.setCapacity(tournament.getCapacity());
        tournamentToUpdate.setIcon(tournament.getIcon());
        tournamentToUpdate.setOrganiser(tournament.getOrganiser());
        tournamentToUpdate.setTimeWeight(tournament.getTimeWeight());
        tournamentToUpdate.setMemWeight(tournament.getMemWeight());
        tournamentToUpdate.setTestCaseWeight(tournament.getTestCaseWeight());
        tournamentToUpdate.setStatus(tournament.getStatus());
        tournamentToUpdate.setSignupStartDate(tournament.getSignupStartDate());
        tournamentToUpdate.setSignupEndDate(tournament.getSignupEndDate());
//            tournamentToUpdate.setSignupStatus(tournament.getSignupStatus());
        tournamentToUpdate.setBand(tournament.getBand());
        tournamentToUpdate.setSignups(tournament.getSignups());
        tournamentToUpdate.setCurrentRound(tournament.getCurrentRound());

//            Set<String> signups = tournamentToUpdate.getSignups();
//            signups.addAll(tournament.getSignups());
//            tournamentToUpdate.setSignups(signups);

        return tournamentServiceRepository.save(tournamentToUpdate);
    }

    public Tournament addTournamentSignup(UUID id, String signup) {
        Optional<Tournament> tournamentOptional = tournamentServiceRepository.findById(id);

        if (tournamentOptional.isEmpty()) {
            throw new TournamentNotFoundException("Tournament with id " + id + " not found");
        }

        Tournament tournament = tournamentOptional.get();
        Set<String> signups = tournament.getSignups();
        signups.add(signup);
        tournament.setSignups(signups);

        return tournamentServiceRepository.save(tournament);
    }

    public Tournament deleteTournamentSignup(UUID id, String signup) {
        Optional<Tournament> tournamentOptional = tournamentServiceRepository.findById(id);

        if (tournamentOptional.isEmpty()) {
            throw new TournamentNotFoundException("Tournament with id " + id + " not found");
        }

        Tournament tournament = tournamentOptional.get();
        Set<String> existingSignups = tournament.getSignups();

        if (!existingSignups.contains(signup)) {
            throw new IllegalArgumentException("Tournament with id " + id + " does not have signup " + signup);
        }

        existingSignups.remove(signup);
        tournament.setSignups(existingSignups);

        return tournamentServiceRepository.save(tournament);
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

//            newBracket.setPlayers(new ArrayList<>(
//                    List.of(
//                            new PlayerInfo(player1, 0),
//                            new PlayerInfo(player2, 0)
//                    )
//            ));
            newBracket.setPlayer1(player1);
            newBracket.setPlayer2(player2);

            bracketService.updateBracket(newBracket.getId(), newBracket);

        }

        tournament.setCurrentRound(nextRound.getName());
        tournamentServiceRepository.save(tournament);

        return tournament;

    }

    public void deleteTournamentById(UUID id) {
        if (!tournamentServiceRepository.existsById(id)) {
            throw new TournamentNotFoundException("Tournament with id " + id + " not found");
        }
        tournamentServiceRepository.deleteById(id); }

    public List<Tournament> findTournamentsBySignUpDeadline(LocalDateTime dateTime, Status status) {
        return tournamentServiceRepository.findBySignupEndDateBeforeAndStatus(dateTime, status).orElse(null);
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
//            TODO: move default value of status to db?
            round.setStatus(Status.UPCOMING);
            round.setName("Round of " + roundSize);

            Round createdRound = roundService.createRound(round);
            createdRounds.add(createdRound);

        }
        return createdRounds;

    }
}