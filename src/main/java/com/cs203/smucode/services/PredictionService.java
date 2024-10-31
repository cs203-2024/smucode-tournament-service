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
}