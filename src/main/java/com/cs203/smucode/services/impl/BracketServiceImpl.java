package com.cs203.smucode.services.impl;

import com.cs203.smucode.constants.Status;
import com.cs203.smucode.exceptions.BracketNotFoundException;
import com.cs203.smucode.exceptions.UserNotFoundException;
import com.cs203.smucode.models.Bracket;
import com.cs203.smucode.models.Round;
import com.cs203.smucode.models.Tournament;
import com.cs203.smucode.repositories.BracketServiceRepository;
import com.cs203.smucode.repositories.RoundServiceRepository;
import com.cs203.smucode.repositories.TournamentServiceRepository;
import com.cs203.smucode.services.BracketService;
import com.cs203.smucode.services.RatingUpdateService;
import de.gesundkrank.jskills.Player;
import jakarta.transaction.Transactional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
public class BracketServiceImpl implements BracketService {
    private static final Logger logger = LoggerFactory.getLogger(BracketServiceImpl.class);

    private final BracketServiceRepository bracketServiceRepository;
    private final RoundServiceRepository roundServiceRepository;
    private final TournamentServiceRepository tournamentServiceRepository;

    private final RatingUpdateService ratingUpdateService;

    @Autowired
    public BracketServiceImpl(BracketServiceRepository bracketServiceRepository,
                              RoundServiceRepository roundServiceRepository,
                              TournamentServiceRepository tournamentServiceRepository,
                              RatingUpdateService ratingUpdateService) {
        this.bracketServiceRepository = bracketServiceRepository;
        this.roundServiceRepository = roundServiceRepository;
        this.tournamentServiceRepository = tournamentServiceRepository;
        this.ratingUpdateService = ratingUpdateService;
    }

    @Transactional
    public List<Bracket> findAllBracketsByRoundId(UUID roundId) {
        return bracketServiceRepository.findByRoundId(roundId).orElse(null);
    }

    @Transactional
    public Bracket findBracketById(UUID id) {
        return bracketServiceRepository.findById(id).orElseThrow(() ->
                new BracketNotFoundException("Bracket with id " + id + " not found"));
    }

    @Transactional
    public Bracket findBracketByRoundIdAndSeqId(UUID id, int seqId) {
        return bracketServiceRepository.findByRoundIdAndSeqId(id, seqId).orElseThrow(() ->
                new BracketNotFoundException("Bracket with round id " + id + " and seq id " + seqId + " not found"));
    }

    @Transactional
    public Bracket findBracketByRoundIdAndPlayer(UUID roundId, String player) {
        return bracketServiceRepository.findByRoundIdAndPlayer1OrPlayer2(roundId, player).orElseThrow(() ->
                new BracketNotFoundException(
                        "Bracket with round id " + roundId + " and player " + player + " not found"
                ));
    }

    @Transactional
    public Bracket createBracket(Bracket bracket) {
        return bracketServiceRepository.save(bracket);
    }

    /**
     * Updates the specified bracket and handles associated status changes
     * in the parent round and tournament as necessary.
     *
     * @param id the UUID of the bracket to update
     * @param bracket the Bracket object containing updated information
     * @return the updated Bracket object
     * @throws BracketNotFoundException if the bracket with the specified ID is not found
     */
    @Transactional
    public Bracket updateBracket(UUID id, Bracket bracket) {
        Bracket bracketToUpdate = bracketServiceRepository.findById(id)
                .orElseThrow(() -> new BracketNotFoundException("Bracket with id " + id + " not found"));

        // Update status of parent round if needed
        Round parentRound = bracketToUpdate.getRound();
        if (parentRound.getStatus() == Status.UPCOMING) {
            parentRound.setStatus(Status.ONGOING);
            roundServiceRepository.save(parentRound); // Save updated parent round status
        }

        // Update tournament current round and status if needed
        Tournament tournament = parentRound.getTournament();
        tournament.setCurrentRound(parentRound.getName());
        if (tournament.getStatus() == Status.UPCOMING) {
            tournament.setStatus(Status.ONGOING);
        }
        tournamentServiceRepository.save(tournament); // Save updated tournament status

        // Update bracket details
        if (bracketToUpdate.getStatus() == Status.UPCOMING) {
            bracketToUpdate.setStatus(Status.ONGOING);
        }

        bracketToUpdate.setPlayer1(bracket.getPlayer1());
        bracketToUpdate.setPlayer2(bracket.getPlayer2());
        bracketToUpdate.setPlayer1Score(bracket.getPlayer1Score());
        bracketToUpdate.setPlayer2Score(bracket.getPlayer2Score());
        bracketToUpdate.setPlayer1WinProbability(bracket.getPlayer1WinProbability());
        bracketToUpdate.setPlayer2WinProbability(bracket.getPlayer2WinProbability());

        return bracketServiceRepository.save(bracketToUpdate); // Save and return updated bracket
    }

    /**
     * Updates score for the specified bracket
     *
     * @param id the UUID of the bracket to update
     * @param bracket the Bracket object containing updated score information
     * @return the updated Bracket object
     * @throws BracketNotFoundException if the bracket with the specified ID is not found
     */
    @Transactional
    public Bracket updateBracketScore(UUID id, Bracket bracket) {
        Bracket bracketToUpdate = bracketServiceRepository.findById(id)
                .orElseThrow(() -> new BracketNotFoundException("Bracket with id " + id + " not found"));

        // Update bracket details
        bracketToUpdate.setPlayer1Score(bracket.getPlayer1Score());
        bracketToUpdate.setPlayer2Score(bracket.getPlayer2Score());

        return bracketServiceRepository.save(bracketToUpdate); // Save and return updated bracket
    }

    /**
     * Ends the specified bracket by determining and setting the winner based on player scores
     * or bye conditions. Updates ratings if both players were present and the bracket is completed.
     *
     * @param id the UUID of the bracket to end
     * @return the updated Bracket object with the winner and completed status
     * @throws BracketNotFoundException if the bracket with the specified ID is not found
     */
    @Transactional
    public Bracket endBracket(UUID id) {
        Bracket bracket = bracketServiceRepository.findById(id)
                .orElseThrow(() -> new BracketNotFoundException("Bracket with id " + id + " not found"));

        setBracketWinner(bracket);

        // Update ratings only if both players participated
        //TODO: refactor this to handle bye cases; issue now is that for updating to happen, we need both players ("relative updating")
        if (bracket.getPlayer1() != null && bracket.getPlayer2() != null) {
            ratingUpdateService.updateRatings(bracket);
        }

        // Close bracket
        bracket.setStatus(Status.COMPLETED);

        return bracketServiceRepository.save(bracket);
    }

    /**
     * Removes the specified player from the bracket. Clears all data associated with the player,
     * including score and win probability, based on their position in the bracket (player1 or player2).
     *
     * @param bracket the Bracket object from which to remove the player
     * @param player the username of the player to remove
     * @return the updated Bracket object after the player has been removed
     * @throws UserNotFoundException if the specified player is not found in the bracket
     */
    public Bracket removePlayerFromBracket(Bracket bracket, String player) {
        logger.info("Attempting to remove player '{}' from bracket: {}", player, bracket.getId());

        if (player.equals(bracket.getPlayer1())) {
            clearPlayer(bracket, 1);
        } else if (player.equals(bracket.getPlayer2())) {
            clearPlayer(bracket, 2);
        } else {
        throw new UserNotFoundException("User " + player + " not found in bracket " + bracket.getId());
    }
        return bracketServiceRepository.save(bracket);

    }

//    helper functions
    /**
     * Determines and sets the winner of the given bracket based on player presence and scores.
     *
     * @param bracket the Bracket object for which the winner will be determined and set
     */
    private void setBracketWinner(Bracket bracket) {
        if (bracket.getPlayer1() == null) {
            // Player 1 is absent, set Player 2 as the winner
            bracket.setWinner(bracket.getPlayer2());
        } else if (bracket.getPlayer2() == null) {
            // Player 2 is absent, set Player 1 as the winner
            bracket.setWinner(bracket.getPlayer1());
        } else {
            // Both players present, set winner based on scores
            String winner = bracket.getPlayer1Score() > bracket.getPlayer2Score()
                    ? bracket.getPlayer1()
                    : bracket.getPlayer2();
            bracket.setWinner(winner);
        }
    }

    /**
     * Clears the specified player’s data from the bracket, setting the player to null,
     * resetting the score to 0, and updating win probabilities based on absence.
     *
     * @param bracket the Bracket object from which to clear player data
     * @param playerNumber the player number to clear (1 for player1, 2 for player2)
     * @throws IllegalArgumentException if an invalid player number is provided
     */
    private void clearPlayer(Bracket bracket, int playerNumber) {
        if (playerNumber == 1) {
            bracket.setPlayer1(null);
            bracket.setPlayer1Score(0);
            bracket.setPlayer1WinProbability(0.0);
            bracket.setPlayer2WinProbability(1.0);
        } else if (playerNumber == 2) {
            bracket.setPlayer2(null);
            bracket.setPlayer2Score(0);
            bracket.setPlayer2WinProbability(0.0);
            bracket.setPlayer1WinProbability(1.0);
        } else {
            throw new IllegalArgumentException("Invalid player number: " + playerNumber);
        }
    }

}
