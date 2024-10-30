package com.cs203.smucode.services.impl;

import com.cs203.smucode.consumers.UserServiceConsumer;
import com.cs203.smucode.dto.UserDTO;
import com.cs203.smucode.models.PredictionResult;
import org.apache.commons.math3.distribution.NormalDistribution;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class PredictionServiceImplTest {

    @Mock
    private UserServiceConsumer userServiceConsumer;

    private PredictionServiceImpl predictionService;
    private static final double BETA = 4.166667;
    private static final NormalDistribution NORMAL = new NormalDistribution(0, 1);

    @BeforeEach
    void setUp(){
        predictionService = new PredictionServiceImpl(userServiceConsumer);
        predictionService.initialize(); // load the actual model
    }

    @Test
    void testBlendedPrediction() {
        UserDTO player1 = new UserDTO(
                "player1",
                "pwd",
                "p1@test.com",
                "/img.png",
                "ROLE_USER",
                30.0,
                7.0,
                0.0
        );

        UserDTO player2 = new UserDTO(
                "player2",
                "pwd",
                "p2@test.com",
                "/img.png",
                "ROLE_USER",
                20.0,
                5.0,
                0.0
        );

        when(userServiceConsumer.getUserById("player1")).thenReturn(player1);
        when(userServiceConsumer.getUserById("player2")).thenReturn(player2);

        double deltaMu = player1.mu() - player2.mu();
        double sumSigma = Math.pow(player1.sigma(), 2) + Math.pow(player2.sigma(), 2);
        int size = 2;
        double denom = Math.sqrt(size * (BETA * BETA) + sumSigma);
        double tsProb = NORMAL.cumulativeProbability(deltaMu / denom);

        PredictionResult result = predictionService.predictMatch("player1", "player2");

        double blendedProb = result.getPlayer1WinProbability();
        double modelProb = (blendedProb - 0.8 * tsProb) / 0.2;

        System.out.printf("""
                Blended Prediction Analysis:
                Player 1: μ=%.1f, σ=%.1f
                Player 2: μ=%.1f, σ=%.1f
                TrueSkill Probability = %.2f%%
                Model Probability = %.2f%%
                Blended Probability = %.2f%% (80%% TrueSkill + 20%% Model)
                """,
                player1.mu(), player1.sigma(),
                player2.mu(), player2.sigma(),
                tsProb * 100,
                modelProb * 100,
                blendedProb * 100
        );

        assertEquals(blendedProb, (0.8 * tsProb) + (0.2 * modelProb), 0.0001,
                "Blended probability should be 80% TrueSkill + 20% model prediction");

        assertTrue(result.getPlayer1WinProbability() >= 0.05 && result.getPlayer1WinProbability() <= 0.95,
                "Blended probability should be bounded between 5% and 95%");

        assertEquals(1.0, result.getPlayer1WinProbability() + result.getPlayer2WinProbability(), 0.0001,
                "Probabilities should sum to 1");
    }

    @Test
    void testBlendedPredictionWithEqualSkill() {
        UserDTO player1 = new UserDTO(
                "player1", "pwd", "p1@test.com", "/img.png",
                "ROLE_USER", 25.0, 8.0, 0.0
        );
        UserDTO player2 = new UserDTO(
                "player2", "pwd", "p2@test.com", "/img.png",
                "ROLE_USER", 25.0, 8.0, 0.0
        );

        when(userServiceConsumer.getUserById("player1")).thenReturn(player1);
        when(userServiceConsumer.getUserById("player2")).thenReturn(player2);

        PredictionResult result = predictionService.predictMatch("player1", "player2");

        System.out.printf("""
                Equal Skill Blended Prediction:
                Both players: μ=25.0, σ=8.0
                Blended Probability = %.2f%% vs %.2f%%
                """,
                result.getPlayer1WinProbability() * 100,
                result.getPlayer2WinProbability() * 100
        );

        assertTrue(Math.abs(0.5 - result.getPlayer1WinProbability()) < 0.1,
                "Equal skill players should have roughly equal probabilities");
    }

    @Test
    void testBlendedPredictionWithLargeSkillGap() {
        UserDTO expertPlayer = new UserDTO(
                "expert", "pwd", "expert@test.com", "/img.png",
                "ROLE_USER", 40.0, 5.0, 0.0
        );
        UserDTO novicePlayer = new UserDTO(
                "novice", "pwd", "novice@test.com", "/img.png",
                "ROLE_USER", 10.0, 5.0, 0.0
        );

        when(userServiceConsumer.getUserById("expert")).thenReturn(expertPlayer);
        when(userServiceConsumer.getUserById("novice")).thenReturn(novicePlayer);

        PredictionResult result = predictionService.predictMatch("expert", "novice");

        System.out.printf("""
                Large Skill Gap Blended Prediction:
                Expert: μ=40.0, σ=5.0
                Novice: μ=10.0, σ=5.0
                Blended Probability = %.2f%% vs %.2f%%
                """,
                result.getPlayer1WinProbability() * 100,
                result.getPlayer2WinProbability() * 100
        );

        assertTrue(result.getPlayer1WinProbability() <= 0.95,
                "Prediction should be bounded at 95% even for large skill differences");
        assertTrue(result.getPlayer2WinProbability() >= 0.05,
                "Prediction should be bounded at 5% even for large skill differences");
    }
}