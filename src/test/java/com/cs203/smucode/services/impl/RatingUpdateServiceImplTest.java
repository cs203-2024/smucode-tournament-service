package com.cs203.smucode.services.impl;

import com.cs203.smucode.consumers.UserServiceConsumer;
import com.cs203.smucode.dtos.users.UserDTO;
import com.cs203.smucode.dtos.users.UserRatingDTO;
import com.cs203.smucode.models.Bracket;
import com.cs203.smucode.services.PredictionService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class RatingUpdateServiceImplTest {

    @Mock
    private UserServiceConsumer userServiceConsumer;

    @Mock
    private PredictionService predictionService;

    @Captor
    private ArgumentCaptor<UserRatingDTO> ratingDTOCaptor;

    private RatingUpdateServiceImpl ratingUpdateService;
    private Bracket testBracket;

    @BeforeEach
    void setUp() {
        ratingUpdateService = new RatingUpdateServiceImpl(userServiceConsumer, predictionService);

        // Create test bracket
        testBracket = new Bracket();
        testBracket.setId(UUID.randomUUID());
        testBracket.setPlayer1("player1");
        testBracket.setPlayer2("player2");
        testBracket.setPlayer1Score(3);
        testBracket.setPlayer2Score(1);
        testBracket.setWinner("player1");
    }

    @Test
    void updateRatings_WhenStrongerPlayerWins() {
        // Arrange
        UserDTO winner = createUserDTO("player1", 30.0); // Higher skill
        UserDTO loser = createUserDTO("player2", 20.0);  // Lower skill

        when(userServiceConsumer.getUserById("player1")).thenReturn(winner);
        when(userServiceConsumer.getUserById("player2")).thenReturn(loser);

        // Act
        ratingUpdateService.updateRatings(testBracket);

        // Assert
        verify(userServiceConsumer, times(2)).updateRating(ratingDTOCaptor.capture());
        verify(predictionService).trainModelWithResult("player1", "player2", true);

        List<UserRatingDTO> ratingUpdates = ratingDTOCaptor.getAllValues();

        // Check winner rating update
        UserRatingDTO winnerUpdate = ratingUpdates.get(0);
        assertEquals("player1", winnerUpdate.username());
        assertTrue(winnerUpdate.mu() > winner.mu(), "Winner's rating should increase");
        assertTrue(winnerUpdate.sigma() < winner.sigma(), "Winner's uncertainty should decrease");

        // Check loser rating update
        UserRatingDTO loserUpdate = ratingUpdates.get(1);
        assertEquals("player2", loserUpdate.username());
        assertTrue(loserUpdate.mu() < loser.mu(), "Loser's rating should decrease");
        assertTrue(loserUpdate.sigma() < loser.sigma(), "Loser's uncertainty should decrease");

        // Verify small rating change for expected result
        assertTrue((winnerUpdate.mu() - winner.mu()) < 2.5,
                "Rating change should be small when favorite wins");
    }

    @Test
    void updateRatings_WhenWeakerPlayerWins() {
        // Arrange
        UserDTO winner = createUserDTO("player1", 20.0); // Lower skill
        UserDTO loser = createUserDTO("player2", 30.0);  // Higher skill

        when(userServiceConsumer.getUserById("player1")).thenReturn(winner);
        when(userServiceConsumer.getUserById("player2")).thenReturn(loser);

        // Act
        ratingUpdateService.updateRatings(testBracket);

        // Assert
        verify(userServiceConsumer, times(2)).updateRating(ratingDTOCaptor.capture());
        verify(predictionService).trainModelWithResult("player1", "player2", true);

        List<UserRatingDTO> ratingUpdates = ratingDTOCaptor.getAllValues();

        // Verify larger rating changes for upset
        UserRatingDTO winnerUpdate = ratingUpdates.get(0);
        UserRatingDTO loserUpdate = ratingUpdates.get(1);

        double ratingGain = winnerUpdate.mu() - winner.mu();
        double ratingLoss = loser.mu() - loserUpdate.mu();

        assertTrue(ratingGain > 5.0, "Underdog winner should gain more rating");
        assertTrue(ratingLoss > 5.0, "Favored loser should lose more rating");
    }

    @Test
    void updateRatings_WhenEqualSkillPlayers() {
        // Arrange
        UserDTO winner = createUserDTO("player1", 25.0);
        UserDTO loser = createUserDTO("player2", 25.0);

        when(userServiceConsumer.getUserById("player1")).thenReturn(winner);
        when(userServiceConsumer.getUserById("player2")).thenReturn(loser);

        // Act
        ratingUpdateService.updateRatings(testBracket);

        // Assert
        verify(userServiceConsumer, times(2)).updateRating(ratingDTOCaptor.capture());
        verify(predictionService).trainModelWithResult("player1", "player2", true);

        List<UserRatingDTO> ratingUpdates = ratingDTOCaptor.getAllValues();
        UserRatingDTO winnerUpdate = ratingUpdates.get(0);
        UserRatingDTO loserUpdate = ratingUpdates.get(1);

        assertEquals(Math.abs(winnerUpdate.mu() - winner.mu()),
                Math.abs(loser.mu() - loserUpdate.mu()),
                0.01,
                "Rating changes should be symmetric for equal skill players");
    }

    @Test
    void updateRatings_WhenUserServiceFails() {
        // Arrange
        when(userServiceConsumer.getUserById("player1"))
                .thenThrow(new RuntimeException("Service unavailable"));

        // Act & Assert
        Exception exception = assertThrows(RuntimeException.class, () ->
                ratingUpdateService.updateRatings(testBracket));

        assertEquals("Failed to update player ratings", exception.getMessage());

        verify(userServiceConsumer, never()).updateRating(any());
        verify(predictionService, never()).trainModelWithResult(any(), any(), anyBoolean());
    }

    @Test
    void updateRatings_WhenBracketHasNoWinner() {
        // Arrange
        testBracket.setWinner(null);

        // Act & Assert
        Exception exception = assertThrows(RuntimeException.class, () ->
                ratingUpdateService.updateRatings(testBracket));

        verify(userServiceConsumer, never()).updateRating(any());
        verify(predictionService, never()).trainModelWithResult(any(), any(), anyBoolean());
    }

    private UserDTO createUserDTO(String username, double mu) {
        return new UserDTO(
                username,
                "password",
                username + "@test.com",
                "/image.jpg",
                "ROLE_USER",
                mu,
                25.0/3.0,  // Default initial sigma
                0.0
        );
    }
}