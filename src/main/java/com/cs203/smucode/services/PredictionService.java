package com.cs203.smucode.services;

import com.cs203.smucode.models.PredictionResult;

public interface PredictionService {
    /**
     * Predicts the outcome of a match between two players
     * @param player1Username Username of the first player
     * @param player2Username Username of the second player
     * @return PredictionResult containing win probabilities for both players
     */
    PredictionResult predictMatch(String player1Username, String player2Username);

    /**
     * Trains the model with a new match result
     * @param player1Username First player's username
     * @param player2Username Second player's username
     * @param player1Won Whether player1 won the match
     */
    void trainModelWithResult(String player1Username, String player2Username, boolean player1Won);
}