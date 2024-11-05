package com.cs203.smucode.services.impl;

import com.cs203.smucode.constants.Status;
import com.cs203.smucode.exceptions.TournamentNotFoundException;
import com.cs203.smucode.models.Bracket;
import com.cs203.smucode.models.Round;
import com.cs203.smucode.models.Tournament;
import com.cs203.smucode.repositories.TournamentServiceRepository;
import com.cs203.smucode.services.BracketService;
import com.cs203.smucode.services.RoundService;
import com.cs203.smucode.services.TournamentService;
import jakarta.transaction.Transactional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.*;

@Service
public class TournamentServiceImpl implements TournamentService {
    private static final Logger logger = LoggerFactory.getLogger(TournamentServiceImpl.class);
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

    @Transactional
    public Tournament findTournamentById(UUID id) {
        return tournamentServiceRepository.findById(id).orElseThrow(() ->
                new TournamentNotFoundException("Tournament with id " + id + " not found"));
    }

    @Transactional
    public List<Tournament> findAllTournamentsByOrganiser(String organiser) {
        return tournamentServiceRepository.findByOrganiser(organiser).orElse(null);
    }

    @Transactional
    public List<Tournament> findAllTournamentsByStatus(Status status) {
        return tournamentServiceRepository.findByStatus(status).orElse(null);
    }

    @Transactional
    public List<Tournament> findAllTournamentsByRegistrant(String registrant) {
        return tournamentServiceRepository.findByRegistrant(registrant).orElse(null);
    }

    @Transactional
    public List<Tournament> findAllTournamentsByParticipant(String participant) {
        return tournamentServiceRepository.findByParticipant(participant).orElse(null);
    }

    @Transactional
    public List<Tournament> findTournamentsWithSignUpBefore(LocalDateTime dateTime) {
        return tournamentServiceRepository.findBySignupEndDateBeforeAndStatus(dateTime, Status.UPCOMING).orElse(null);
    }

    @Transactional
    public List<Tournament> findTournamentsWithSignUpAfter(LocalDateTime dateTime) {
        return tournamentServiceRepository.findBySignupEndDateAfterAndStatus(dateTime, Status.UPCOMING).orElse(null);
    }

    @Transactional
    public List<Tournament> findAllEligibleTournamentsForUser(String username) {

        // show tournaments which signups have not closed and have not been signed up by user
        List<Tournament> openTournaments = findTournamentsWithSignUpAfter(LocalDateTime.now());
        logger.info("open tournaments: {}", openTournaments);
        List<Tournament> eligibleTournaments = new ArrayList<>();
        for (Tournament tournament : openTournaments) {
            if (!tournament.getSignups().contains(username)) {
                eligibleTournaments.add(tournament);
            }
        }

        logger.info("eligible tournaments: {}", eligibleTournaments);
        return eligibleTournaments;
    }

    @Transactional
    public Tournament createTournament(Tournament tournament) {

        // TODO: data insert validation
        if (tournament == null) { return null; }

        tournamentServiceRepository.save(tournament);

        createRounds(tournament); // generate rounds

        return tournament;
    }

    @Transactional
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
        tournamentToUpdate.setBand(tournament.getBand());
        tournamentToUpdate.setSignups(tournament.getSignups());
        tournamentToUpdate.setParticipants(tournament.getParticipants());
        tournamentToUpdate.setCurrentRound(tournament.getCurrentRound());

        return tournamentServiceRepository.save(tournamentToUpdate);
    }

    @Transactional
    public Tournament addTournamentSignup(UUID id, String signup) {
        Optional<Tournament> tournamentOptional = tournamentServiceRepository.findById(id);

        if (tournamentOptional.isEmpty()) {
            throw new TournamentNotFoundException("Tournament with id " + id + " not found");
        }

        Tournament tournament = tournamentOptional.get();

        if (LocalDateTime.now().isAfter(tournament.getSignupEndDate())) {
            throw new IllegalStateException("Tournament with id " + id + " has already closed signups");
        }

        Set<String> signups = tournament.getSignups();
        signups.add(signup);
        tournament.setSignups(signups);

        return tournamentServiceRepository.save(tournament);
    }

    @Transactional
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

    @Transactional
    public Tournament deleteTournamentParticipant(UUID id, String participant) {
        Optional<Tournament> tournamentOptional = tournamentServiceRepository.findById(id);

        if (tournamentOptional.isEmpty()) {
            throw new TournamentNotFoundException("Tournament with id " + id + " not found");
        }

        Tournament tournament = tournamentOptional.get();
        Set<String> existingParticipants = tournament.getParticipants();

        if (!existingParticipants.contains(participant)) {
            throw new IllegalArgumentException("Tournament with id " + id + " does not have participant " + participant);
        }

        logger.info("Removing player from tournament: {}", tournament.getId());
        existingParticipants.remove(participant);
        // Remove from ongoing bracket
        UUID currRound = roundService.
                findRoundByTournamentIdAndName(tournament.getId(), tournament.getCurrentRound())
                .getId();
        roundService.removePlayerFromOngoingRound(currRound, participant);
        tournament.setParticipants(existingParticipants);

        return tournamentServiceRepository.save(tournament);

    }

    @Transactional
    public Tournament endBracket(UUID bracketId) {
        Bracket bracket = bracketService.endBracket(bracketId);
        return bracket.getRound().getTournament();
    }

    @Transactional
    public Tournament endRound(UUID roundId) {

        Round currRound = roundService.findRoundById(roundId);

        UUID currRoundId = currRound.getId();
        int currRoundSeqId = currRound.getSeqId();
        Tournament parentTournament = currRound.getTournament();
        UUID parentTournamentId = currRound.getTournament().getId();

        // Update current round status
        currRound.setStatus(Status.COMPLETED);
        roundService.updateRound(currRoundId, currRound);

        // If final round
        if (currRound.getBrackets().size() == 1) {
            // TODO: tournament complete logic
            parentTournament.setStatus(Status.COMPLETED); // Set tournament status to completed
            updateTournament(parentTournamentId, parentTournament);
            return parentTournament;
        }

        // Get next round
        Round nextRound = roundService.findRoundByTournamentIdAndSeqId(parentTournamentId, currRoundSeqId+1);
        UUID nextRoundId = nextRound.getId();

        // Move winners to respective brackets
        roundService.populateNextRound(roundId, nextRoundId);

        // Update tournament "currRound" field
        parentTournament.setCurrentRound(nextRound.getName());
        updateTournament(parentTournamentId, parentTournament);

        return parentTournament;

    }

    @Transactional
    public void deleteTournamentById(UUID id) {
        if (!tournamentServiceRepository.existsById(id)) {
            throw new TournamentNotFoundException("Tournament with id " + id + " not found");
        }
        tournamentServiceRepository.deleteById(id); }

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