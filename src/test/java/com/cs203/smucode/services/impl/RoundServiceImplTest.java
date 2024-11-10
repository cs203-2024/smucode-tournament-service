package com.cs203.smucode.services.impl;

import com.cs203.smucode.exceptions.RoundCreationException;
import com.cs203.smucode.exceptions.TournamentNotFoundException;
import com.cs203.smucode.exceptions.UserNotFoundException;
import com.cs203.smucode.models.PredictionResult;
import com.cs203.smucode.services.PredictionService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.*;

import com.cs203.smucode.constants.Status;
import com.cs203.smucode.exceptions.RoundNotFoundException;
import com.cs203.smucode.models.Round;
import com.cs203.smucode.models.Tournament;
import com.cs203.smucode.models.Bracket;
import com.cs203.smucode.repositories.RoundServiceRepository;
import com.cs203.smucode.services.BracketService;

import java.time.LocalDateTime;
import java.util.*;

class RoundServiceImplTest {

    @Mock
    private RoundServiceRepository roundServiceRepository;

    @Mock
    private BracketService bracketService;

    @Mock
    private PredictionService predictionService;

    @InjectMocks
    private RoundServiceImpl roundService;

    private Round sampleRound;
    private Tournament sampleTournament;
    private Bracket sampleBracket;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        sampleTournament = createSampleTournament();
        sampleRound = createSampleRound();
        sampleBracket = createSampleBracket();
    }

    private Tournament createSampleTournament() {
        Tournament tournament = new Tournament();
        tournament.setId(UUID.randomUUID());
        tournament.setName("Sample Tournament");
        return tournament;
    }

    private Round createSampleRound() {
        Round round = new Round();
        round.setId(UUID.randomUUID());
        round.setSeqId(1);
        round.setName("Round of 16");
        round.setStartDate(LocalDateTime.now());
        round.setEndDate(LocalDateTime.now().plusDays(1));
        round.setStatus(Status.UPCOMING);
        round.setTournament(sampleTournament);
        return round;
    }

    private Bracket createSampleBracket() {
        Bracket bracket = new Bracket();
        bracket.setId(UUID.randomUUID());
        bracket.setTournament(sampleTournament);
        bracket.setRound(sampleRound);
        bracket.setStatus(Status.UPCOMING);
        bracket.setPlayer1Score(0);
        bracket.setPlayer2Score(0);
        return bracket;
    }

    @Test
    void findRoundById_withValidId_shouldReturnRound() {
        UUID id = sampleRound.getId();
        when(roundServiceRepository.findById(id)).thenReturn(Optional.of(sampleRound));

        Round actualRound = roundService.findRoundById(id);

        assertEquals(sampleRound, actualRound);
        verify(roundServiceRepository).findById(id);
    }

    @Test
    void findRoundById_withInvalidId_shouldThrowRoundNotFoundException() {
        UUID id = UUID.randomUUID();
        when(roundServiceRepository.findById(id)).thenReturn(Optional.empty());

        assertThrows(RoundNotFoundException.class, () -> roundService.findRoundById(id));
        verify(roundServiceRepository).findById(id);
    }

    @Test
    void findRoundByTournamentIdAndSeqId_shouldReturnRound() {
        UUID tournamentId = sampleTournament.getId();
        int seqId = sampleRound.getSeqId();
        when(roundServiceRepository.findByTournamentIdAndSeqId(tournamentId, seqId)).thenReturn(Optional.of(sampleRound));

        Round actualRound = roundService.findRoundByTournamentIdAndSeqId(tournamentId, seqId);

        assertEquals(sampleRound, actualRound);
        verify(roundServiceRepository).findByTournamentIdAndSeqId(tournamentId, seqId);
    }

    @Test
    void findRoundByTournamentIdAndSeqId_withInvalidArguments_shouldThrowRoundNotFoundException() {
        UUID tournamentId = UUID.randomUUID();
        int seqId = -1;
        when(roundServiceRepository.findByTournamentIdAndSeqId(tournamentId, seqId)).thenReturn(Optional.empty());

        assertThrows(RoundNotFoundException.class, () -> roundService.findRoundByTournamentIdAndSeqId(tournamentId, seqId));
        verify(roundServiceRepository).findByTournamentIdAndSeqId(tournamentId, seqId);
    }

    @Test
    void findRoundByTournamentIdAndName_shouldReturnRound() {
        UUID tournamentId = sampleTournament.getId();
        String name = sampleRound.getName();
        when(roundServiceRepository.findByTournamentIdAndName(tournamentId, name)).thenReturn(Optional.of(sampleRound));

        Round actualRound = roundService.findRoundByTournamentIdAndName(tournamentId, name);

        assertEquals(sampleRound, actualRound);
        verify(roundServiceRepository).findByTournamentIdAndName(tournamentId, name);
    }

    @Test
    void findRoundByTournamentIdAndName_withInvalidArguments_shouldThrowRoundNotFoundException() {
        UUID tournamentId = UUID.randomUUID();
        String name = "Invalid Round";
        when(roundServiceRepository.findByTournamentIdAndName(tournamentId, name)).thenReturn(Optional.empty());

        assertThrows(RoundNotFoundException.class, () -> roundService.findRoundByTournamentIdAndName(tournamentId, name));
        verify(roundServiceRepository).findByTournamentIdAndName(tournamentId, name);
    }

    @Test
    void createRound_shouldCreateRoundWithBrackets() {
        Round roundToCreate = createSampleRound();
        when(roundServiceRepository.save(roundToCreate)).thenReturn(roundToCreate);

        Round actualRound = roundService.createRound(roundToCreate);

        assertEquals(roundToCreate, actualRound);
        verify(roundServiceRepository).save(roundToCreate);
        // Round of 16 should create 8 brackets
        verify(bracketService, times(8)).createBracket(any(Bracket.class));
    }

    @Test
    void updateRound_withValidIdAndRound_shouldReturnUpdatedRound() {
        UUID id = sampleRound.getId();
        Round updatedRound = createSampleRound();
        updatedRound.setName("Updated Round Name");
        when(roundServiceRepository.findById(id)).thenReturn(Optional.of(sampleRound));
        when(roundServiceRepository.save(any(Round.class))).thenReturn(updatedRound);

        Round actualRound = roundService.updateRound(id, updatedRound);

        assertEquals(updatedRound, actualRound);
        verify(roundServiceRepository).findById(id);
        verify(roundServiceRepository).save(any(Round.class));
    }

    @Test
    void updateRound_withInvalidId_shouldThrowRoundNotFoundException() {
        UUID id = UUID.randomUUID();
        Round updatedRound = createSampleRound();
        when(roundServiceRepository.findById(id)).thenReturn(Optional.empty());

        assertThrows(RoundNotFoundException.class, () -> roundService.updateRound(id, updatedRound));
        verify(roundServiceRepository).findById(id);
        verify(roundServiceRepository, never()).save(any(Round.class));
    }

    @Test
    void populateNextRound_withInvalidNextRoundId_shouldThrowRoundNotFoundException() {
        UUID currRoundId = UUID.randomUUID();
        UUID nextRoundId = UUID.randomUUID();
        when(roundServiceRepository.findById(nextRoundId)).thenReturn(Optional.empty());

        assertThrows(RoundNotFoundException.class, () -> roundService.populateNextRound(currRoundId, nextRoundId));
    }

    @Test
    void removePlayerFromOngoingRound_shouldRemovePlayerAndReturnUpdatedRound() {
        // Arrange
        UUID roundId = UUID.randomUUID();
        String username = "testUser";
        Round round = createSampleRound();
        round.setStatus(Status.ONGOING); // Set status to ONGOING
        Bracket bracket = createSampleBracket();
        bracket.setStatus(Status.ONGOING);

        when(roundServiceRepository.findById(roundId)).thenReturn(Optional.of(round));
        when(bracketService.findBracketByRoundIdAndPlayer(roundId, username)).thenReturn(bracket);
        when(bracketService.removePlayerFromBracket(bracket, username)).thenReturn(bracket);

        // Act
        Round result = roundService.removePlayerFromOngoingRound(roundId, username);

        // Assert
        assertEquals(round, result);
        verify(bracketService).findBracketByRoundIdAndPlayer(roundId, username);
        verify(bracketService).removePlayerFromBracket(bracket, username);
        verify(bracketService).endBracket(bracket.getId());
    }

    @Test
    void getBracketCountFromRoundName_withValidName_shouldReturnCorrectCount() {
        String roundName = "Round of 16";
        int bracketCount = roundService.getBracketCountFromRoundName(roundName);
        assertEquals(8, bracketCount);
    }

    @Test
    void getBracketCountFromRoundName_withInvalidName_shouldReturnZero() {
        String roundName = "Final";
        int bracketCount = roundService.getBracketCountFromRoundName(roundName);
        assertEquals(0, bracketCount);
    }

    @Test
    void createRound_whenRoundNameIsInvalid_shouldNotCreateBrackets() {
        // Arrange
        Round roundToCreate = createSampleRound();
        roundToCreate.setName("Invalid Round Name");
        when(roundServiceRepository.save(roundToCreate)).thenReturn(roundToCreate);

        // Act
        Round result = roundService.createRound(roundToCreate);

        // Assert
        assertEquals(roundToCreate, result);
        verify(roundServiceRepository).save(roundToCreate);
        verify(bracketService, never()).createBracket(any(Bracket.class));
    }

    @Test
    void createRound_whenBracketCreationFails_shouldThrowRoundCreationException() {
        // Arrange
        Round roundToCreate = createSampleRound();
        roundToCreate.setName("Round of 16"); // This will trigger creation of 8 brackets
        when(roundServiceRepository.save(roundToCreate)).thenReturn(roundToCreate);
        when(bracketService.createBracket(any(Bracket.class)))
                .thenThrow(new RuntimeException("Bracket creation failed"));

        // Act & Assert
        RoundCreationException exception = assertThrows(RoundCreationException.class, () ->
                roundService.createRound(roundToCreate));

        assertTrue(exception.getMessage().contains("Failed to create round with brackets"));
        verify(roundServiceRepository).save(roundToCreate);
        verify(bracketService).createBracket(any(Bracket.class)); // Should fail on first bracket
    }

    @Test
    void populateNextRound_whenPlayerIsNull_shouldSkipPrediction() {
        // Arrange
        UUID currRoundId = UUID.randomUUID();
        UUID nextRoundId = UUID.randomUUID();
        Round nextRound = createSampleRound();

        Bracket nextRoundBracket = createSampleBracket();
        nextRound.setBrackets(Collections.singletonList(nextRoundBracket));

        // Create brackets for current round with one null winner
        Bracket bracket1 = createSampleBracket();
        bracket1.setStatus(Status.COMPLETED); // Set status to COMPLETED
        bracket1.setWinner(null);

        Bracket bracket2 = createSampleBracket();
        bracket2.setStatus(Status.COMPLETED); // Set status to COMPLETED
        bracket2.setWinner("player2");

        when(roundServiceRepository.findById(nextRoundId)).thenReturn(Optional.of(nextRound));
        when(bracketService.findBracketByRoundIdAndSeqId(nextRoundId, 1)).thenReturn(nextRoundBracket);
        when(bracketService.findBracketByRoundIdAndSeqId(currRoundId, 1)).thenReturn(bracket1);
        when(bracketService.findBracketByRoundIdAndSeqId(currRoundId, 2)).thenReturn(bracket2);

        // Act & Assert
        assertThrows(RoundNotFoundException.class, () ->
                        roundService.populateNextRound(currRoundId, nextRoundId),
                "Should throw IllegalStateException when a winner is null"
        );
        verify(predictionService, never()).predictMatch(anyString(), anyString());
    }

    @Test
    void updateRound_whenAllFieldsAreNull_shouldNotUpdateFields() {
        // Arrange
        UUID id = sampleRound.getId();
        Round originalRound = createSampleRound();
        Round updatedRound = new Round(); // All fields null

        when(roundServiceRepository.findById(id)).thenReturn(Optional.of(originalRound));
        when(roundServiceRepository.save(any(Round.class))).thenReturn(originalRound);

        // Act
        Round result = roundService.updateRound(id, updatedRound);

        // Assert
        assertEquals(originalRound.getName(), result.getName());
        assertEquals(originalRound.getStartDate(), result.getStartDate());
        assertEquals(originalRound.getEndDate(), result.getEndDate());
        assertEquals(originalRound.getStatus(), result.getStatus());
        verify(roundServiceRepository).save(any(Round.class));
    }

    @Test
    void updateRound_shouldOnlyUpdateNonNullFields() {
        // Arrange
        UUID id = sampleRound.getId();
        Round originalRound = createSampleRound();
        LocalDateTime originalStartDate = originalRound.getStartDate();
        LocalDateTime originalEndDate = originalRound.getEndDate();
        Status originalStatus = originalRound.getStatus();

        Round updatedRound = new Round();
        updatedRound.setName("New Name"); // Only set name

        Round expectedRound = new Round();
        expectedRound.setName("New Name");
        expectedRound.setStartDate(originalStartDate);
        expectedRound.setEndDate(originalEndDate);
        expectedRound.setStatus(originalStatus);

        when(roundServiceRepository.findById(id)).thenReturn(Optional.of(originalRound));
        when(roundServiceRepository.save(any(Round.class))).thenReturn(expectedRound);

        // Act
        Round result = roundService.updateRound(id, updatedRound);

        // Assert
        assertEquals("New Name", result.getName());
        assertEquals(originalStartDate, result.getStartDate());
        assertEquals(originalEndDate, result.getEndDate());
        assertEquals(originalStatus, result.getStatus());
        verify(roundServiceRepository).save(any(Round.class));
    }

    @Test
    void populateNextRound_shouldThrowIllegalStateException_whenCurrentBracketsNotCompleted() {
        // Arrange
        UUID currRoundId = UUID.randomUUID();
        UUID nextRoundId = UUID.randomUUID();
        Round nextRound = createSampleRound();
        nextRound.setBrackets(Collections.singletonList(sampleBracket));

        Bracket currentBracket1 = createSampleBracket();
        currentBracket1.setStatus(Status.ONGOING); // Not completed
        Bracket currentBracket2 = createSampleBracket();
        currentBracket2.setStatus(Status.COMPLETED);

        when(roundServiceRepository.findById(nextRoundId)).thenReturn(Optional.of(nextRound));
        when(bracketService.findBracketByRoundIdAndSeqId(nextRoundId, 1)).thenReturn(sampleBracket);
        when(bracketService.findBracketByRoundIdAndSeqId(currRoundId, 1)).thenReturn(currentBracket1);
        when(bracketService.findBracketByRoundIdAndSeqId(currRoundId, 2)).thenReturn(currentBracket2);

        // Act & Assert
        assertThrows(RoundNotFoundException.class,
                () -> roundService.populateNextRound(currRoundId, nextRoundId));
    }

    @Test
    void populateNextRound_shouldThrowIllegalStateException_whenWinnersNotSet() {
        // Arrange
        UUID currRoundId = UUID.randomUUID();
        UUID nextRoundId = UUID.randomUUID();
        Round nextRound = createSampleRound();
        nextRound.setBrackets(Collections.singletonList(sampleBracket));

        Bracket currentBracket1 = createSampleBracket();
        currentBracket1.setStatus(Status.COMPLETED);
        currentBracket1.setWinner("player1");
        Bracket currentBracket2 = createSampleBracket();
        currentBracket2.setStatus(Status.COMPLETED);
        currentBracket2.setWinner(null); // Missing winner

        when(roundServiceRepository.findById(nextRoundId)).thenReturn(Optional.of(nextRound));
        when(bracketService.findBracketByRoundIdAndSeqId(nextRoundId, 1)).thenReturn(sampleBracket);
        when(bracketService.findBracketByRoundIdAndSeqId(currRoundId, 1)).thenReturn(currentBracket1);
        when(bracketService.findBracketByRoundIdAndSeqId(currRoundId, 2)).thenReturn(currentBracket2);

        // Act & Assert
        assertThrows(RoundNotFoundException.class,
                () -> roundService.populateNextRound(currRoundId, nextRoundId));
    }

    @Test
    void removePlayerFromOngoingRound_shouldThrowIllegalStateException_whenRoundNotOngoing() {
        // Arrange
        UUID roundId = UUID.randomUUID();
        String username = "testUser";
        Round round = createSampleRound();
        round.setStatus(Status.UPCOMING); // Not ongoing

        when(roundServiceRepository.findById(roundId)).thenReturn(Optional.of(round));

        // Act & Assert
        assertThrows(IllegalStateException.class,
                () -> roundService.removePlayerFromOngoingRound(roundId, username));
    }

    @Test
    void removePlayerFromOngoingRound_shouldCompleteRemovedPlayerBracket() {
        // Arrange
        UUID roundId = UUID.randomUUID();
        String username = "testUser";
        Round round = createSampleRound();
        round.setStatus(Status.ONGOING);
        Bracket bracket = createSampleBracket();
        bracket.setStatus(Status.ONGOING);

        when(roundServiceRepository.findById(roundId)).thenReturn(Optional.of(round));
        when(bracketService.findBracketByRoundIdAndPlayer(roundId, username)).thenReturn(bracket);
        when(bracketService.removePlayerFromBracket(bracket, username)).thenReturn(bracket);

        // Act
        Round result = roundService.removePlayerFromOngoingRound(roundId, username);

        // Assert
        assertEquals(round, result);
        verify(bracketService).removePlayerFromBracket(bracket, username);
        verify(bracketService).endBracket(bracket.getId());
    }

    @Test
    void createRound_shouldGenerateCorrectNumberOfBrackets() {
        // Arrange
        Round roundToCreate = createSampleRound();
        when(roundServiceRepository.save(roundToCreate)).thenReturn(roundToCreate);

        // Act
        Round result = roundService.createRound(roundToCreate);

        // Assert
        assertEquals(roundToCreate, result);
        verify(roundServiceRepository).save(roundToCreate);
        // Round of 16 should create 8 brackets
        verify(bracketService, times(8)).createBracket(any(Bracket.class));
    }

    @Test
    void removePlayerFromOngoingRound_shouldThrowUserNotFoundException_whenPlayerNotFound() {
        // Arrange
        UUID roundId = UUID.randomUUID();
        String username = "nonexistentUser";
        Round round = createSampleRound();
        round.setStatus(Status.ONGOING);

        when(roundServiceRepository.findById(roundId)).thenReturn(Optional.of(round));
        when(bracketService.findBracketByRoundIdAndPlayer(roundId, username)).thenReturn(null);

        // Act & Assert
        assertThrows(UserNotFoundException.class,
                () -> roundService.removePlayerFromOngoingRound(roundId, username));
    }

    @Test
    void populateNextRound_shouldPopulateNextRoundSuccessfully() {
        // Arrange
        UUID currRoundId = UUID.randomUUID();
        UUID nextRoundId = UUID.randomUUID();

        // Mock current round
        Round currRound = new Round();
        currRound.setId(currRoundId);
        currRound.setBrackets(new ArrayList<>());
        currRound.setStatus(Status.ONGOING);

        // Mock next round
        Round nextRound = new Round();
        nextRound.setId(nextRoundId);
        nextRound.setBrackets(new ArrayList<>());
        nextRound.setStatus(Status.UPCOMING);

        // Assume we have 4 brackets in current round
        int numberOfBracketsInCurrentRound = 4;
        for (int i = 1; i <= numberOfBracketsInCurrentRound; i++) {
            Bracket currBracket = new Bracket();
            currBracket.setId(UUID.randomUUID());
            currBracket.setSeqId(i);
            currBracket.setStatus(Status.COMPLETED);
            currBracket.setWinner("player" + i);
            currBracket.setRound(currRound);
            currRound.getBrackets().add(currBracket);
        }

        // Assume we have 2 brackets in next round
        int numberOfBracketsInNextRound = 2;
        for (int i = 1; i <= numberOfBracketsInNextRound; i++) {
            Bracket nextBracket = new Bracket();
            nextBracket.setId(UUID.randomUUID());
            nextBracket.setSeqId(i);
            nextBracket.setStatus(Status.UPCOMING);
            nextBracket.setRound(nextRound);
            nextRound.getBrackets().add(nextBracket);
        }

        // Mock repository calls
        when(roundServiceRepository.findById(currRoundId)).thenReturn(Optional.of(currRound));
        when(roundServiceRepository.findById(nextRoundId)).thenReturn(Optional.of(nextRound));
        when(roundServiceRepository.save(any(Round.class))).thenAnswer(invocation -> invocation.getArgument(0));

        // Mock bracketService calls for current brackets
        for (int i = 1; i <= numberOfBracketsInCurrentRound; i++) {
            Bracket currBracket = currRound.getBrackets().get(i - 1);
            when(bracketService.findBracketByRoundIdAndSeqId(currRoundId, i))
                    .thenReturn(currBracket);
        }

        // Mock bracketService calls for next brackets
        for (int i = 1; i <= numberOfBracketsInNextRound; i++) {
            Bracket nextBracket = nextRound.getBrackets().get(i - 1);
            when(bracketService.findBracketByRoundIdAndSeqId(nextRoundId, i))
                    .thenReturn(nextBracket);
        }

        // Mock predictionService calls
        for (int i = 1; i <= numberOfBracketsInNextRound; i++) {
            String player1 = currRound.getBrackets().get((i - 1) * 2).getWinner();
            String player2 = currRound.getBrackets().get((i - 1) * 2 + 1).getWinner();

            PredictionResult predictionResult = new PredictionResult();
            predictionResult.setPlayer1WinProbability(0.6);
            predictionResult.setPlayer2WinProbability(0.4);

            when(predictionService.predictMatch(player1, player2))
                    .thenReturn(predictionResult);
        }

        // Act
        Round result = roundService.populateNextRound(currRoundId, nextRoundId);

        // Assert
        assertEquals(Status.COMPLETED, currRound.getStatus());
        assertEquals(Status.ONGOING, nextRound.getStatus());
        assertEquals(nextRound, result);

        // Verify that brackets in next round are updated correctly
        for (int i = 1; i <= numberOfBracketsInNextRound; i++) {
            Bracket nextBracket = nextRound.getBrackets().get(i - 1);
            String expectedPlayer1 = currRound.getBrackets().get((i - 1) * 2).getWinner();
            String expectedPlayer2 = currRound.getBrackets().get((i - 1) * 2 + 1).getWinner();

            assertEquals(expectedPlayer1, nextBracket.getPlayer1());
            assertEquals(expectedPlayer2, nextBracket.getPlayer2());
            assertEquals(0.6, nextBracket.getPlayer1WinProbability());
            assertEquals(0.4, nextBracket.getPlayer2WinProbability());
            assertEquals(Status.ONGOING, nextBracket.getStatus());

            verify(bracketService).updateBracket(eq(nextBracket.getId()), any(Bracket.class));
        }

        // Verify methods were called
        verify(roundServiceRepository, times(2)).findById(any(UUID.class));
        verify(roundServiceRepository, times(2)).save(any(Round.class));
        verify(bracketService, times(numberOfBracketsInNextRound)).findBracketByRoundIdAndSeqId(eq(nextRoundId), anyInt());
        verify(bracketService, times(numberOfBracketsInCurrentRound)).findBracketByRoundIdAndSeqId(eq(currRoundId), anyInt());
        verify(predictionService, times(numberOfBracketsInNextRound)).predictMatch(anyString(), anyString());
    }
    @Test
    void populateNextRound_withInvalidCurrentRoundId_shouldThrowRoundNotFoundException() {
        // Arrange
        UUID currRoundId = UUID.randomUUID();
        UUID nextRoundId = UUID.randomUUID();

        // Mock next round exists
        Round nextRound = new Round();
        nextRound.setId(nextRoundId);
        when(roundServiceRepository.findById(nextRoundId)).thenReturn(Optional.of(nextRound));

        // Mock current round does not exist
        when(roundServiceRepository.findById(currRoundId)).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(RoundNotFoundException.class, () -> roundService.populateNextRound(currRoundId, nextRoundId));

        verify(roundServiceRepository).findById(currRoundId);
        verify(roundServiceRepository, never()).save(any(Round.class));
    }

    @Test
    void populateNextRound_withIncompleteCurrentBracket_shouldThrowIllegalStateException() {
        // Arrange
        UUID currRoundId = UUID.randomUUID();
        UUID nextRoundId = UUID.randomUUID();

        // Mock current round
        Round currRound = new Round();
        currRound.setId(currRoundId);
        currRound.setBrackets(new ArrayList<>());
        currRound.setStatus(Status.ONGOING);

        // Mock next round
        Round nextRound = new Round();
        nextRound.setId(nextRoundId);
        nextRound.setBrackets(new ArrayList<>());
        nextRound.setStatus(Status.UPCOMING);

        // Assume we have 2 brackets in current round
        int numberOfBracketsInCurrentRound = 2;
        for (int i = 1; i <= numberOfBracketsInCurrentRound; i++) {
            Bracket currBracket = new Bracket();
            currBracket.setId(UUID.randomUUID());
            currBracket.setSeqId(i);
            if (i == 1) {
                currBracket.setStatus(Status.COMPLETED);
                currBracket.setWinner("player" + i);
            } else {
                currBracket.setStatus(Status.ONGOING); // Not completed
            }
            currBracket.setRound(currRound);
            currRound.getBrackets().add(currBracket);
        }

        // Assume we have 1 bracket in next round
        Bracket nextBracket = new Bracket();
        nextBracket.setId(UUID.randomUUID());
        nextBracket.setSeqId(1);
        nextBracket.setStatus(Status.UPCOMING);
        nextBracket.setRound(nextRound);
        nextRound.getBrackets().add(nextBracket);

        // Mock repository calls
        when(roundServiceRepository.findById(currRoundId)).thenReturn(Optional.of(currRound));
        when(roundServiceRepository.findById(nextRoundId)).thenReturn(Optional.of(nextRound));
        when(roundServiceRepository.save(any(Round.class))).thenAnswer(invocation -> invocation.getArgument(0));

        // Mock bracketService calls for current brackets
        for (int i = 1; i <= numberOfBracketsInCurrentRound; i++) {
            Bracket currBracket = currRound.getBrackets().get(i - 1);
            when(bracketService.findBracketByRoundIdAndSeqId(currRoundId, i))
                    .thenReturn(currBracket);
        }

        // Mock bracketService calls for next brackets
        when(bracketService.findBracketByRoundIdAndSeqId(nextRoundId, 1))
                .thenReturn(nextBracket);

        // Act & Assert
        IllegalStateException exception = assertThrows(IllegalStateException.class, () -> roundService.populateNextRound(currRoundId, nextRoundId));

        assertTrue(exception.getMessage().contains("is still ongoing"));
    }

    @Test
    void populateNextRound_withBracketMissingWinner_shouldThrowIllegalStateException() {
        // Arrange
        UUID currRoundId = UUID.randomUUID();
        UUID nextRoundId = UUID.randomUUID();

        // Mock current round
        Round currRound = new Round();
        currRound.setId(currRoundId);
        currRound.setBrackets(new ArrayList<>());
        currRound.setStatus(Status.ONGOING);

        // Mock next round
        Round nextRound = new Round();
        nextRound.setId(nextRoundId);
        nextRound.setBrackets(new ArrayList<>());
        nextRound.setStatus(Status.UPCOMING);

        // Assume we have 2 brackets in current round
        int numberOfBracketsInCurrentRound = 2;
        for (int i = 1; i <= numberOfBracketsInCurrentRound; i++) {
            Bracket currBracket = new Bracket();
            currBracket.setId(UUID.randomUUID());
            currBracket.setSeqId(i);
            currBracket.setStatus(Status.COMPLETED);
            if (i == 1) {
                currBracket.setWinner("player" + i);
            } else {
                currBracket.setWinner(null); // Missing winner
            }
            currBracket.setRound(currRound);
            currRound.getBrackets().add(currBracket);
        }

        // Assume we have 1 bracket in next round
        Bracket nextBracket = new Bracket();
        nextBracket.setId(UUID.randomUUID());
        nextBracket.setSeqId(1);
        nextBracket.setStatus(Status.UPCOMING);
        nextBracket.setRound(nextRound);
        nextRound.getBrackets().add(nextBracket);

        // Mock repository calls
        when(roundServiceRepository.findById(currRoundId)).thenReturn(Optional.of(currRound));
        when(roundServiceRepository.findById(nextRoundId)).thenReturn(Optional.of(nextRound));
        when(roundServiceRepository.save(any(Round.class))).thenAnswer(invocation -> invocation.getArgument(0));

        // Mock bracketService calls for current brackets
        for (int i = 1; i <= numberOfBracketsInCurrentRound; i++) {
            Bracket currBracket = currRound.getBrackets().get(i - 1);
            when(bracketService.findBracketByRoundIdAndSeqId(currRoundId, i))
                    .thenReturn(currBracket);
        }

        // Mock bracketService calls for next brackets
        when(bracketService.findBracketByRoundIdAndSeqId(nextRoundId, 1))
                .thenReturn(nextBracket);

        // Act & Assert
        IllegalStateException exception = assertThrows(IllegalStateException.class, () -> roundService.populateNextRound(currRoundId, nextRoundId));

        assertTrue(exception.getMessage().contains("does not have a winner"));
    }

    @Test
    void populateNextRound_withNoBracketsInNextRound_shouldReturnNextRoundWithUpdatedStatus() {
        // Arrange
        UUID currRoundId = UUID.randomUUID();
        UUID nextRoundId = UUID.randomUUID();

        // Mock current round
        Round currRound = new Round();
        currRound.setId(currRoundId);
        currRound.setBrackets(new ArrayList<>());
        currRound.setStatus(Status.ONGOING);

        // Mock next round with no brackets
        Round nextRound = new Round();
        nextRound.setId(nextRoundId);
        nextRound.setBrackets(new ArrayList<>());
        nextRound.setStatus(Status.UPCOMING);

        // Mock repository calls
        when(roundServiceRepository.findById(currRoundId)).thenReturn(Optional.of(currRound));
        when(roundServiceRepository.findById(nextRoundId)).thenReturn(Optional.of(nextRound));
        when(roundServiceRepository.save(any(Round.class))).thenAnswer(invocation -> invocation.getArgument(0));

        // Act
        Round result = roundService.populateNextRound(currRoundId, nextRoundId);

        // Assert
        assertEquals(Status.COMPLETED, currRound.getStatus());
        assertEquals(Status.ONGOING, nextRound.getStatus());
        assertEquals(nextRound, result);

        // Verify that no brackets were updated
        verify(bracketService, never()).findBracketByRoundIdAndSeqId(any(UUID.class), anyInt());
        verify(bracketService, never()).updateBracket(any(UUID.class), any(Bracket.class));
    }

    @Test
    void populateNextRound_whenPredictionServiceFails_shouldThrowException() {
        // Arrange
        UUID currRoundId = UUID.randomUUID();
        UUID nextRoundId = UUID.randomUUID();

        // Mock current round
        Round currRound = new Round();
        currRound.setId(currRoundId);
        currRound.setBrackets(new ArrayList<>());
        currRound.setStatus(Status.ONGOING);

        // Mock next round
        Round nextRound = new Round();
        nextRound.setId(nextRoundId);
        nextRound.setBrackets(new ArrayList<>());
        nextRound.setStatus(Status.UPCOMING);

        // Assume we have 2 brackets in current round
        int numberOfBracketsInCurrentRound = 2;
        for (int i = 1; i <= numberOfBracketsInCurrentRound; i++) {
            Bracket currBracket = new Bracket();
            currBracket.setId(UUID.randomUUID());
            currBracket.setSeqId(i);
            currBracket.setStatus(Status.COMPLETED);
            currBracket.setWinner("player" + i);
            currBracket.setRound(currRound);
            currRound.getBrackets().add(currBracket);
        }

        // Assume we have 1 bracket in next round
        Bracket nextBracket = new Bracket();
        nextBracket.setId(UUID.randomUUID());
        nextBracket.setSeqId(1);
        nextBracket.setStatus(Status.UPCOMING);
        nextBracket.setRound(nextRound);
        nextRound.getBrackets().add(nextBracket);

        // Mock repository calls
        when(roundServiceRepository.findById(currRoundId)).thenReturn(Optional.of(currRound));
        when(roundServiceRepository.findById(nextRoundId)).thenReturn(Optional.of(nextRound));
        when(roundServiceRepository.save(any(Round.class))).thenAnswer(invocation -> invocation.getArgument(0));

        // Mock bracketService calls for current brackets
        for (int i = 1; i <= numberOfBracketsInCurrentRound; i++) {
            Bracket currBracket = currRound.getBrackets().get(i - 1);
            when(bracketService.findBracketByRoundIdAndSeqId(currRoundId, i))
                    .thenReturn(currBracket);
        }

        // Mock bracketService calls for next brackets
        when(bracketService.findBracketByRoundIdAndSeqId(nextRoundId, 1))
                .thenReturn(nextBracket);

        // Mock predictionService to throw exception
        when(predictionService.predictMatch(anyString(), anyString()))
                .thenThrow(new RuntimeException("Prediction service failure"));

        // Act & Assert
        RuntimeException exception = assertThrows(RuntimeException.class, () -> roundService.populateNextRound(currRoundId, nextRoundId));

        assertEquals("Prediction service failure", exception.getMessage());
    }
}
