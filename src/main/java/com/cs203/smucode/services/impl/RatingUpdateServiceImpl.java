package com.cs203.smucode.services.impl;

import com.cs203.smucode.consumers.UserServiceConsumer;
import com.cs203.smucode.dto.UserDTO;
import com.cs203.smucode.dto.UserRatingDTO;
import com.cs203.smucode.exceptions.RatingUpdateFailedException;
import com.cs203.smucode.models.Bracket;
import com.cs203.smucode.services.PredictionService;
import com.cs203.smucode.services.RatingUpdateService;
import de.gesundkrank.jskills.*;
import de.gesundkrank.jskills.trueskill.TwoPlayerTrueSkillCalculator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.Map;

/**
 * Implementation of the Rating Update Service using the TrueSkill algorithm.
 * This service handles updating player ratings after matches and training the prediction model
 * with new match results.
 *
 * TrueSkill parameters:
 * - μ (mu): Player's average skill level
 * - σ (sigma): Uncertainty in the player's skill estimate
 * - β (beta): Additional uncertainty during gameplay
 * - τ (tau): Small additive uncertainty to prevent sigma from getting too small
 * - drawProbability: Likelihood of a draw (not applicable in our case but required by TrueSkill)
 */
@Service
public class RatingUpdateServiceImpl implements RatingUpdateService {
    private static final Logger logger = LoggerFactory.getLogger(RatingUpdateServiceImpl.class);

    // TrueSkill constants as recommended by the "original" paper
    private static final double DEFAULT_INITIAL_MEAN = 25.0;  // Initial μ for new players
    private static final double DEFAULT_INITIAL_STANDARD_DEVIATION = 25.0 / 3.0;  // Initial σ (large to show uncertainty)
    private static final double BETA = 25.0 / 6.0;  // Skill chain variance (~4.166)
    private static final double DRAW_PROBABILITY = 0.1;  // Required by TrueSkill but not used in our system

    // GameInfo encapsulates the TrueSkill parameters
    private static final GameInfo GAME_INFO = new GameInfo(
            DEFAULT_INITIAL_MEAN,
            DEFAULT_INITIAL_STANDARD_DEVIATION,
            BETA,
            DRAW_PROBABILITY,
            0.003  // Small τ value to maintain some uncertainty
    );

    private final UserServiceConsumer userServiceConsumer;
    private final PredictionService predictionService;
    private final TwoPlayerTrueSkillCalculator calculator;

    @Autowired
    public RatingUpdateServiceImpl(UserServiceConsumer userServiceConsumer,
                                   PredictionService predictionService) {
        this.userServiceConsumer = userServiceConsumer;
        this.predictionService = predictionService;
        this.calculator = new TwoPlayerTrueSkillCalculator();
    }

    /**
     * Updates player ratings after a match using the TrueSkill algorithm.
     * Also trains the prediction model with the match result.
     *
     * The process:
     * 1. Retrieve player information and current ratings
     * 2. Create TrueSkill Players and Teams
     * 3. Calculate new ratings using TrueSkill
     * 4. Update players' ratings in the user service
     * 5. Train the prediction model with the result
     *
     * @param bracket The completed bracket containing match result information
     * @throws RatingUpdateFailedException if rating update fails
     */
    @Override
    public void updateRatings(Bracket bracket) {
        try {
            // Retrieve player information from UserService
            UserDTO winner = userServiceConsumer.getUserById(bracket.getWinner());
            UserDTO loser = userServiceConsumer.getUserById(
                    bracket.getPlayer1().equals(bracket.getWinner()) ? bracket.getPlayer2() : bracket.getPlayer1()
            );

            // Create Player objects
            Player<String> winnerPlayer = new Player<>(winner.username());
            Player<String> loserPlayer = new Player<>(loser.username());

            // Update User Win/Loss
            userServiceConsumer.updateUserWin(winner.username());
            userServiceConsumer.updateUserLoss(loser.username());

            // Create Rating objects with current rating values
            Rating winnerRating = new Rating(winner.mu(), winner.sigma());
            Rating loserRating = new Rating(loser.mu(), loser.sigma());

            // Create "Teams"; (1 player per team for 1v1 matches)
            Team winnerTeam = new Team(winnerPlayer, winnerRating);
            Team loserTeam = new Team(loserPlayer, loserRating);

            // Calculate new ratings (ranks: 1 for winner, 2 for loser)
            Map<IPlayer, Rating> newRatings = calculator.calculateNewRatings(
                    GAME_INFO,
                    Arrays.asList(winnerTeam, loserTeam),
                    1, 2
            );

            // Extract the new computed ratings from the result map
            Rating newWinnerRating = newRatings.get(winnerPlayer);
            Rating newLoserRating = newRatings.get(loserPlayer);

            // Update ratings in user service
            updateUserRating(winner.username(), newWinnerRating);
            updateUserRating(loser.username(), newLoserRating);

            // Train the prediction model with the match result
            trainModel(bracket);

            // Log the rating changes
            logger.info("Successfully updated ratings for match: Winner {} (new μ={}, σ={}), Loser {} (new μ={}, σ={})",
                    winner.username(), newWinnerRating.getMean(), newWinnerRating.getStandardDeviation(),
                    loser.username(), newLoserRating.getMean(), newLoserRating.getStandardDeviation());

        } catch (Exception e) {
            logger.error("Failed to update ratings for bracket {}: {}",
                    bracket.getId(), e.getMessage());
            throw new RatingUpdateFailedException("Failed to update player ratings");
        }
    }

    /**
     * Updates a player's rating in the user service
     * @param username The player's username
     * @param newRating The player's new TrueSkill rating
     */
    private void updateUserRating(String username, Rating newRating) {
        UserRatingDTO ratingDTO = new UserRatingDTO(
                username,
                newRating.getMean(),
                newRating.getStandardDeviation()
        );
        userServiceConsumer.updateRating(ratingDTO);
    }

    /**
     * Trains the prediction model with a match result
     * @param bracket The completed bracket containing match result information
     */
    private void trainModel(Bracket bracket) {
        predictionService.trainModelWithResult(
                bracket.getPlayer1(),
                bracket.getPlayer2(),
                bracket.getWinner().equals(bracket.getPlayer1())
        );
    }
}