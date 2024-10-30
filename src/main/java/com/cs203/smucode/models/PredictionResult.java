package com.cs203.smucode.models;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class PredictionResult {
    private String player1Username;
    private String player2Username;
    private double player1WinProbability;
    private double player2WinProbability;
}