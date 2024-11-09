package com.cs203.smucode.services.impl;

import com.cs203.smucode.constants.Status;
import com.cs203.smucode.exceptions.RoundCreationException;
import com.cs203.smucode.exceptions.RoundNotFoundException;
import com.cs203.smucode.exceptions.UserNotFoundException;
import com.cs203.smucode.models.Bracket;
import com.cs203.smucode.models.Round;
import com.cs203.smucode.models.PredictionResult;
import com.cs203.smucode.repositories.RoundServiceRepository;
import com.cs203.smucode.services.BracketService;
import com.cs203.smucode.services.PredictionService;
import com.cs203.smucode.services.RoundService;
import jakarta.transaction.Transactional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.UUID;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Service
public class RoundServiceImpl implements RoundService {

    private static final Logger logger = LoggerFactory.getLogger(RoundServiceImpl.class);

    private final RoundServiceRepository roundServiceRepository;
    private final BracketService bracketService;
    private final PredictionService predictionService;
    private final BracketServiceImpl bracketServiceImpl;


    @Autowired
    public RoundServiceImpl(RoundServiceRepository roundServiceRepository,
                            BracketService bracketService,
                            PredictionService predictionService, BracketServiceImpl bracketServiceImpl) {
        this.roundServiceRepository = roundServiceRepository;
        this.bracketService = bracketService;
        this.predictionService = predictionService;
        this.bracketServiceImpl = bracketServiceImpl;
    }

    @Transactional
    public Round findRoundById(UUID id) {
        return roundServiceRepository.findById(id).orElseThrow(() ->
                new RoundNotFoundException("Round with id " + id + " not found"));
    }

    @Transactional
    public Round findRoundByTournamentIdAndSeqId(UUID tournamentId, int seqId) {
        return roundServiceRepository.findByTournamentIdAndSeqId(tournamentId, seqId).orElseThrow(() ->
                new RoundNotFoundException("Round with tournament id " + tournamentId + " and seq id " + seqId + " not found"));
    }

    @Transactional
    public Round findRoundByTournamentIdAndName(UUID tournamentId, String name) {
        return roundServiceRepository.findByTournamentIdAndName(tournamentId, name).orElseThrow(() ->
                new RoundNotFoundException("Round with tournament id " + tournamentId + " and name " + name + " not found"));
    }

    /**
     * Create round and its associated brackets
     *
     * @param round Round object to be created
     * @return Round object created
     */
    @Transactional
    public Round createRound(Round round) {
        Round savedRound = roundServiceRepository.save(round);
        int bracketCount = getBracketCountFromRoundName(savedRound.getName());

        try {
            createBracketsForRound(savedRound, bracketCount);
        } catch (Exception e) {
            logger.error("Error creating brackets for round: {}", savedRound.getId());
            throw new RoundCreationException("Failed to create round with brackets; " + e.getMessage());
        }

        return savedRound;
    }

    @Transactional
    public Round updateRound(UUID id, Round round) {
        Round roundToUpdate = roundServiceRepository.findById(id)
                .orElseThrow(() -> new RoundNotFoundException("Round with id " + id + " not found"));

        if (round.getName() != null) { roundToUpdate.setName(round.getName()); }
        if (round.getStartDate() != null) { roundToUpdate.setStartDate(round.getStartDate()); }
        if (round.getEndDate() != null) { roundToUpdate.setEndDate(round.getEndDate()); }
        if (round.getStatus() != null) { roundToUpdate.setStatus(round.getStatus()); }

        return roundServiceRepository.save(roundToUpdate);
    }

    /**
     * Populates the next round with players who won in the current round.
     * Each bracket in the next round is filled by pairing winners from the current round's brackets.
     *
     * @param currRoundId the UUID of the current round (which has just ended)
     * @param nextRoundId the UUID of the next round (which is about to start)
     * @return the Round object for the next round with updated brackets
     * @throws RoundNotFoundException if the next round with the specified ID is not found
     * @throws IllegalStateException if any current round bracket is incomplete or missing a winner
     */
    @Transactional
    public Round populateNextRound(UUID currRoundId, UUID nextRoundId) {
        Round nextRound = roundServiceRepository.findById(nextRoundId)
                .orElseThrow(() -> new RoundNotFoundException("Round with id " + nextRoundId + " not found"));

        for (int i = 1; i <= nextRound.getBrackets().size(); i++) {
            Bracket bracketToUpdate = bracketService.findBracketByRoundIdAndSeqId(nextRoundId, i);

            // Fetch current round brackets for player determination
            Bracket currBracket1 = bracketService.findBracketByRoundIdAndSeqId(currRoundId, i*2 - 1);
            Bracket currBracket2 = bracketService.findBracketByRoundIdAndSeqId(currRoundId, i*2);

            if (currBracket1.getStatus() != Status.COMPLETED || currBracket2.getStatus() != Status.COMPLETED) {
                throw new IllegalStateException(
                        "Current bracket " + currBracket1.getId() + " or " + currBracket2.getId() + " is still ongoing"
                );
            }

            // Get winners from current round brackets
            String player1 = currBracket1.getWinner();
            String player2 = currBracket2.getWinner();

            if (player1 == null || player2 == null) {
                throw new IllegalStateException(
                        "Bracket " + currBracket1.getId() + " or " + currBracket2.getId() + " does not have a winner"
                );
            }

            // Set up new bracket with players and predictions
            PredictionResult prediction = predictionService.predictMatch(player1, player2);
            bracketToUpdate.setPlayer1(player1);
            bracketToUpdate.setPlayer2(player2);
            bracketToUpdate.setPlayer1WinProbability(prediction.getPlayer1WinProbability());
            bracketToUpdate.setPlayer2WinProbability(prediction.getPlayer2WinProbability());
            bracketToUpdate.setStatus(Status.ONGOING);

            bracketService.updateBracket(bracketToUpdate.getId(), bracketToUpdate);
        }

        return nextRound;
    }

    /**
     * Remove player from (ongoing) round - premature leaving of tournament.
     * Result in round bye for round containing leaving player.
     *
     * @param roundId Round id of ongoing round
     * @param username Username of player to be removed
     * @return Round object of ongoing round
     */
    @Transactional
    public Round removePlayerFromOngoingRound(UUID roundId, String username) {
        logger.info("Attempting to remove player '{}' from round: {}", username, roundId);

        // Fetch the round and check if it is ongoing
        Round round = findRoundById(roundId);
        if (round.getStatus() != Status.ONGOING) {
            throw new IllegalStateException("Cannot remove player: Round " + roundId + " is not ongoing");
        }

        // Locate the bracket containing the player
        Bracket bracketWithPlayer = bracketService.findBracketByRoundIdAndPlayer(roundId, username);
        if (bracketWithPlayer == null) {
            throw new UserNotFoundException("Player " + username + " not found in any bracket of round " + roundId);
        }

        // Remove the player from the bracket
        bracketService.removePlayerFromBracket(bracketWithPlayer, username);
        logger.info("Successfully removed player '{}' from round {}", username, roundId);

        // End the bracket with the leaving player
        bracketService.endBracket(bracketWithPlayer.getId());

        return round;
    }

//    helper functions
  
    /**
     * Method to create brackets for round
     *
     * @param round to create brackets for
     * @param bracketCount to be created
     */
    void createBracketsForRound(Round round, int bracketCount) {
        for (int i = 0; i < bracketCount; i++) {
            Bracket bracket = new Bracket();
            bracket.setTournament(round.getTournament());
            bracket.setRound(round);
            bracket.setStatus(Status.UPCOMING);
            // TODO: move this to be handled at DB level
            bracket.setPlayer1Score(0);
            bracket.setPlayer2Score(0);
            bracketService.createBracket(bracket);
        }
    }

    /**
     * Method gets number of brackets in round from round name ("Round of N")
     *
     * @param roundName of round object
     * @return number of brackets in round
     */
     int getBracketCountFromRoundName(String roundName) {
        Pattern pattern = Pattern.compile("\\d+"); // regex pattern to match 1 or more digits
        Matcher matcher = pattern.matcher(roundName); // searches regex pattern against roundName

        if (matcher.find()) {
            return Integer.parseInt(matcher.group()) / 2; // retrieves first match
        }
        return 0;
    }
}
