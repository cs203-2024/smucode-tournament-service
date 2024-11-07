package com.cs203.smucode.services.impl;

import com.cs203.smucode.exceptions.TournamentNotFoundException;
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
    void findAllRoundsByTournamentId_shouldReturnListOfRounds() {
        UUID tournamentId = sampleTournament.getId();
        List<Round> expectedRounds = Collections.singletonList(sampleRound);
        when(roundServiceRepository.findByTournamentId(tournamentId)).thenReturn(Optional.of(expectedRounds));

        List<Round> actualRounds = roundService.findAllRoundsByTournamentId(tournamentId);

        assertEquals(expectedRounds, actualRounds);
        verify(roundServiceRepository).findByTournamentId(tournamentId);
    }

    @Test
    void findAllRoundsByTournamentId_whenNoRounds_shouldReturnNull() {
        UUID tournamentId = sampleTournament.getId();
        when(roundServiceRepository.findByTournamentId(tournamentId)).thenReturn(Optional.empty());

        List<Round> actualRounds = roundService.findAllRoundsByTournamentId(tournamentId);

        assertNull(actualRounds);
        verify(roundServiceRepository).findByTournamentId(tournamentId);
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
    void populateNextRound_shouldPopulateRoundWithWinners() {
        UUID currRoundId = UUID.randomUUID();
        UUID nextRoundId = UUID.randomUUID();
        Round nextRound = createSampleRound();
        
        // Create a list with one bracket to simulate the next round
        Bracket nextRoundBracket = createSampleBracket();
        nextRound.setBrackets(Collections.singletonList(nextRoundBracket));

        when(roundServiceRepository.findById(nextRoundId)).thenReturn(Optional.of(nextRound));
        
        // Mock the next round bracket
        when(bracketService.findBracketByRoundIdAndSeqId(nextRoundId, 1)).thenReturn(nextRoundBracket);
        
        // Mock the current round brackets with winners
        Bracket bracket1 = createSampleBracket();
        bracket1.setWinner("player1");
        Bracket bracket2 = createSampleBracket();
        bracket2.setWinner("player2");
        
        when(bracketService.findBracketByRoundIdAndSeqId(currRoundId, 1)).thenReturn(bracket1);
        when(bracketService.findBracketByRoundIdAndSeqId(currRoundId, 2)).thenReturn(bracket2);

        // Mock prediction service
        PredictionResult predictionResult = new PredictionResult();
        predictionResult.setPlayer1WinProbability(0.6);
        predictionResult.setPlayer2WinProbability(0.4);
        when(predictionService.predictMatch("player1", "player2")).thenReturn(predictionResult);

        Round result = roundService.populateNextRound(currRoundId, nextRoundId);

        assertEquals(nextRound, result);
        verify(bracketService).updateBracket(any(UUID.class), any(Bracket.class));
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
        UUID roundId = sampleRound.getId();
        String username = "testUser";
        Bracket bracketWithPlayer = createSampleBracket();
        
        when(bracketService.findBracketByRoundIdAndPlayer(roundId, username)).thenReturn(bracketWithPlayer);
        when(roundServiceRepository.findById(roundId)).thenReturn(Optional.of(sampleRound));

        Round result = roundService.removePlayerFromOngoingRound(roundId, username);

        assertEquals(sampleRound, result);
        verify(bracketService).removePlayerFromBracket(bracketWithPlayer, username);
    }

    @Test
    void deleteRoundById_withValidId_shouldDeleteRound() {
        UUID id = sampleRound.getId();
        when(roundServiceRepository.existsById(id)).thenReturn(true);

        roundService.deleteRoundById(id);

        verify(roundServiceRepository).deleteById(id);
    }

    @Test
    void deleteRoundById_withInvalidId_shouldThrowRoundNotFoundException() {
        UUID id = UUID.randomUUID();
        when(roundServiceRepository.existsById(id)).thenReturn(false);

        assertThrows(RoundNotFoundException.class, () -> roundService.deleteRoundById(id));
        verify(roundServiceRepository, never()).deleteById(id);
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
    void createRound_whenBracketCreationFails_shouldHandleException() {
        // Arrange
        Round roundToCreate = createSampleRound();
        when(roundServiceRepository.save(roundToCreate)).thenReturn(roundToCreate);
        when(bracketService.createBracket(any(Bracket.class)))
                .thenThrow(new RuntimeException("Bracket creation failed"));

        // Act
        Round result = roundService.createRound(roundToCreate);

        // Assert
        assertEquals(roundToCreate, result);
        verify(roundServiceRepository).save(roundToCreate);
    }

    @Test
    void populateNextRound_whenPlayerIsNull_shouldSkipPrediction() {
        // Arrange
        UUID currRoundId = UUID.randomUUID();
        UUID nextRoundId = UUID.randomUUID();
        Round nextRound = createSampleRound();

        Bracket nextRoundBracket = createSampleBracket();
        nextRound.setBrackets(Collections.singletonList(nextRoundBracket));

        Bracket bracket1 = createSampleBracket();
        bracket1.setWinner(null); // Set winner as null
        Bracket bracket2 = createSampleBracket();
        bracket2.setWinner("player2");

        when(roundServiceRepository.findById(nextRoundId)).thenReturn(Optional.of(nextRound));
        when(bracketService.findBracketByRoundIdAndSeqId(nextRoundId, 1)).thenReturn(nextRoundBracket);
        when(bracketService.findBracketByRoundIdAndSeqId(currRoundId, 1)).thenReturn(bracket1);
        when(bracketService.findBracketByRoundIdAndSeqId(currRoundId, 2)).thenReturn(bracket2);

        // Act
        Round result = roundService.populateNextRound(currRoundId, nextRoundId);

        // Assert
        verify(predictionService, never()).predictMatch(anyString(), anyString());
        verify(bracketService).updateBracket(any(UUID.class), any(Bracket.class));
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
    void populateNextRound_whenBothPlayersAreNull_shouldSkipBracketUpdate() {
        // Arrange
        UUID currRoundId = UUID.randomUUID();
        UUID nextRoundId = UUID.randomUUID();
        Round nextRound = createSampleRound();
        nextRound.setBrackets(new ArrayList<>());  // Empty brackets list

        when(roundServiceRepository.findById(nextRoundId)).thenReturn(Optional.of(nextRound));

        // No need to mock bracket service calls since there are no brackets

        // Act
        Round result = roundService.populateNextRound(currRoundId, nextRoundId);

        // Assert
        assertEquals(nextRound, result);
        verify(predictionService, never()).predictMatch(anyString(), anyString());
        verify(bracketService, never()).findBracketByRoundIdAndSeqId(any(UUID.class), anyInt());
        verify(bracketService, never()).updateBracket(any(UUID.class), any(Bracket.class));
    }

}
