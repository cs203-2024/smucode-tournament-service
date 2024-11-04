package com.cs203.smucode.services;

import com.cs203.smucode.models.Bracket;

public interface RatingUpdateService {
    /**
     * Updates the ratings of players in a completed bracket and trains the prediction model
     * @param bracket The completed bracket containing player information and match result
     */
    void updateRatings(Bracket bracket);
}