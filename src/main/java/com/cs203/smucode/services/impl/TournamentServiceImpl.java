package com.cs203.smucode.services.impl;

import com.cs203.smucode.constants.Status;
import com.cs203.smucode.exceptions.TournamentNotFoundException;
import com.cs203.smucode.exceptions.UserNotFoundException;
import com.cs203.smucode.models.Round;
import com.cs203.smucode.models.Tournament;
import com.cs203.smucode.repositories.TournamentServiceRepository;
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
//    private EventFactory eventFactory;

    @Autowired
    public TournamentServiceImpl(TournamentServiceRepository tournamentServiceRepository,
                                 RoundService roundService) {
        this.tournamentServiceRepository = tournamentServiceRepository;
        this.roundService = roundService;
    }

//    @Autowired
//    public void setEventFactory(EventFactory eventFactory) {
//        this.eventFactory = eventFactory;
//    }

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

    /**
     * Get eligible tournaments for user (explore page).
     * Eligible defined as tournaments that user has not signed up for and still have their signups open.
     *
     * @param username of intended user
     * @return List of tournament objects eligible for user
     */
    @Transactional
    public List<Tournament> findAllEligibleTournamentsForUser(String username) {
        // Retrieve open tournaments (where signups have not closed)
        List<Tournament> openTournaments = findTournamentsWithSignUpAfter(LocalDateTime.now());

        // Filter out tournaments where the user has already signed up
        return openTournaments.stream()
                .filter(tournament -> !tournament.getSignups().contains(username))
                .toList();
    }

    /**
     * Create tournament and generate its associated rounds.
     *
     * @param tournament Tournament object
     * @return the created tournament object
     */
    @Transactional
    public Tournament createTournament(Tournament tournament) {
        Tournament createdTournament = tournamentServiceRepository.save(tournament);
        createRounds(tournament); // generate rounds

        return createdTournament;
    }

    @Transactional
    public Tournament updateTournament(UUID id, Tournament tournament) {
        Tournament tournamentToUpdate = tournamentServiceRepository.findById(id)
                .orElseThrow(() -> new TournamentNotFoundException("Tournament with id " + id + " not found"));

        // Update fields
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

    /**
     * Adds a new registrant to the tournament signup list.
     *
     * @param id the UUID of the tournament the registrant is signing up for
     * @param signup the username of the registrant
     * @return the updated Tournament object
     * @throws TournamentNotFoundException if the tournament with the specified ID is not found
     * @throws IllegalStateException if the tournament's signup period has already ended
     */
    @Transactional
    public Tournament addTournamentSignup(UUID id, String signup) {
        Tournament tournament = tournamentServiceRepository.findById(id)
                .orElseThrow(() -> new TournamentNotFoundException("Tournament with id " + id + " not found"));

        if (LocalDateTime.now().isAfter(tournament.getSignupEndDate())) {
            throw new IllegalStateException("Tournament with id " + id + " has already closed signups");
        }

        Set<String> signups = tournament.getSignups();
        signups.add(signup);
        tournament.setSignups(signups);

        return tournamentServiceRepository.save(tournament);
    }

    /**
     * Withdraws an existing registrant from the tournament signup list.
     *
     * @param id the UUID of the tournament from which the registrant is withdrawing
     * @param signup the username of the registrant to withdraw
     * @return the updated Tournament object
     * @throws TournamentNotFoundException if the tournament with the specified ID is not found
     * @throws UserNotFoundException if the specified registrant is not signed up for the tournament
     */
    @Transactional
    public Tournament deleteTournamentSignup(UUID id, String signup) {
        Tournament tournament = tournamentServiceRepository.findById(id)
                .orElseThrow(() -> new TournamentNotFoundException("Tournament with id " + id + " not found"));

        Set<String> existingSignups = tournament.getSignups();

        if (!existingSignups.contains(signup)) {
            throw new UserNotFoundException("Tournament with id " + id + " does not have signup " + signup);
        }

        existingSignups.remove(signup);
        tournament.setSignups(existingSignups);

        return tournamentServiceRepository.save(tournament);
    }

    /**
     * Removes a participant from the tournament's participant list and updates the ongoing round if applicable.
     *
     * @param id the UUID of the tournament from which the participant will be removed
     * @param participant the username of the participant to remove
     * @return the updated Tournament object
     * @throws TournamentNotFoundException if the tournament with the specified ID is not found
     * @throws UserNotFoundException if the specified participant is not in the tournament
     */
    @Transactional
    public Tournament deleteTournamentParticipant(UUID id, String participant) {
        Tournament tournament = tournamentServiceRepository.findById(id)
                .orElseThrow(() -> new TournamentNotFoundException("Tournament with id " + id + " not found"));

        Set<String> existingParticipants = tournament.getParticipants();

        if (!existingParticipants.contains(participant)) {
            throw new UserNotFoundException("Tournament with id " + id + " does not have participant " + participant);
        }

        logger.info("Removing player '{}' from tournament: {}", participant, tournament.getId());
        existingParticipants.remove(participant);
        tournament.setParticipants(existingParticipants);

        // Remove participant from the current round's bracket if applicable
        UUID currRound = roundService.findRoundByTournamentIdAndName(
                tournament.getId(),
                tournament.getCurrentRound()
        ).getId();
        roundService.removePlayerFromOngoingRound(currRound, participant);

        return tournamentServiceRepository.save(tournament);
    }

    /**
     * Completes the current round in the tournament and progresses to the next round, if applicable.
     *
     * @param roundId the UUID of the round to end
     * @return the updated Tournament object after round progression
     */
    @Transactional
    public Tournament progressTournamentToNextRound(UUID roundId) {
        Round currRound = roundService.findRoundById(roundId);
        Tournament parentTournament = currRound.getTournament();

        // Complete the current round
        currRound.setStatus(Status.COMPLETED);
        roundService.updateRound(currRound.getId(), currRound);

//        // Publish ROUND_END notification
//        eventFactory.createRoundEndEvent(
//                parentTournament.getId(),
//                parentTournament.getName(),
//                String.format("Round %s has ended!", roundId)
//        );

        // Check if it's the final round
        if (currRound.getBrackets().size() == 1) {
            // TODO: tournament complete logic
            parentTournament.setStatus(Status.COMPLETED); // Mark the tournament as completed
            updateTournament(parentTournament.getId(), parentTournament);

//            // Publish TOURNAMENT_END notification
//            eventFactory.createTournamentEndEvent(
//                    parentTournament.getId(),
//                    parentTournament.getName(),
//                    String.format("Tournament %s has ended!", parentTournament.getId())
//            );
            return parentTournament;
        }

        // Transition to next round
        Round nextRound = roundService.findRoundByTournamentIdAndSeqId(parentTournament.getId(), currRound.getSeqId()+1);
        roundService.populateNextRound(roundId, nextRound.getId());

        // Update tournament "currRound" field
        parentTournament.setCurrentRound(nextRound.getName());
        updateTournament(parentTournament.getId(), parentTournament);

        return parentTournament;

    }

    @Transactional
    public void deleteTournamentById(UUID id) {
        if (!tournamentServiceRepository.existsById(id)) {
            throw new TournamentNotFoundException("Tournament with id " + id + " not found");
        }
        tournamentServiceRepository.deleteById(id); }

//    helper classes
    /**
     * Creates a list of rounds for a given tournament based on the tournament's capacity.
     * Each round reduces the number of participants by half until only one bracket remains.
     *
     * @param tournament the tournament for which to create rounds
     * @return a list of created rounds
     * @throws IllegalArgumentException if the tournament capacity is less than 2
     */
    List<Round> createRounds(Tournament tournament) {
        int capacity = tournament.getCapacity();

        // Validate tournament capacity
        if (capacity < 2) {
            throw new IllegalArgumentException("Tournament capacity must be at least 2 to create rounds.");
        }

        // Generate the list of round sizes based on tournament capacity
        List<Integer> roundSizes = new ArrayList<>();
        while (capacity > 1) {
            roundSizes.add(capacity);
            capacity /= 2;
        }

        // Create and persist rounds for the tournament
        List<Round> createdRounds = new ArrayList<>();
        for (int roundSize : roundSizes) {
            Round round = new Round();
            round.setTournament(tournament);
            // TODO: move default value of status to db
            round.setStatus(Status.UPCOMING);
            round.setName("Round of " + roundSize);

            Round createdRound = roundService.createRound(round);
            createdRounds.add(createdRound);

        }
        return createdRounds;
    }
}