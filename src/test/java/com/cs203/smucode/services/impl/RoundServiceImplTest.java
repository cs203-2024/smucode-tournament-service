package com.cs203.smucode.services.impl;

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
import com.cs203.smucode.services.impl.RoundServiceImpl;

import java.time.LocalDateTime;
import java.util.*;

class RoundServiceImplTest {

    @Mock
    private RoundServiceRepository roundServiceRepository;

    @Mock
    private BracketService bracketService;

    @InjectMocks
    private RoundServiceImpl roundService;

    private Round sampleRound;
    private Tournament sampleTournament;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        sampleTournament = createSampleTournament();
        sampleRound = createSampleRound();
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

    @Test
    void findAllRoundsByTournamentId_shouldReturnListOfRounds() {
        // Given
        UUID tournamentId = sampleTournament.getId();
        List<Round> expectedRounds = Arrays.asList(sampleRound);
        when(roundServiceRepository.findByTournamentId(tournamentId)).thenReturn(expectedRounds);

        // When
        List<Round> actualRounds = roundService.findAllRoundsByTournamentId(tournamentId);

        // Then
        assertEquals(expectedRounds, actualRounds);
        verify(roundServiceRepository).findByTournamentId(tournamentId);
    }

    @Test
    void findRoundById_withValidId_shouldReturnRound() {
        // Given
        UUID id = sampleRound.getId();
        when(roundServiceRepository.findById(id)).thenReturn(Optional.of(sampleRound));

        // When
        Round actualRound = roundService.findRoundById(id);

        // Then
        assertEquals(sampleRound, actualRound);
        verify(roundServiceRepository).findById(id);
    }

    @Test
    void findRoundByTournamentIdAndSeqId_shouldReturnRound() {
        // Given
        UUID tournamentId = sampleTournament.getId();
        int seqId = sampleRound.getSeqId();
        when(roundServiceRepository.findByTournamentIdAndSeqId(tournamentId, seqId)).thenReturn(sampleRound);

        // When
        Round actualRound = roundService.findRoundByTournamentIdAndSeqId(tournamentId, seqId);

        // Then
        assertEquals(sampleRound, actualRound);
        verify(roundServiceRepository).findByTournamentIdAndSeqId(tournamentId, seqId);
    }

    @Test
    void findRoundByTournamentIdAndName_shouldReturnRound() {
        // Given
        UUID tournamentId = sampleTournament.getId();
        String name = sampleRound.getName();
        when(roundServiceRepository.findByTournamentIdAndName(tournamentId, name)).thenReturn(sampleRound);

        // When
        Round actualRound = roundService.findRoundByTournamentIdAndName(tournamentId, name);

        // Then
        assertEquals(sampleRound, actualRound);
        verify(roundServiceRepository).findByTournamentIdAndName(tournamentId, name);
    }

    @Test
    void createRound_shouldReturnCreatedRoundAndCreateBrackets() {
        // Given
        Round roundToCreate = createSampleRound();
        when(roundServiceRepository.save(roundToCreate)).thenReturn(roundToCreate);

        // When
        Round actualRound = roundService.createRound(roundToCreate);

        // Then
        assertEquals(roundToCreate, actualRound);
        verify(roundServiceRepository).save(roundToCreate);
        verify(bracketService, times(8)).createBracket(any(Bracket.class));
    }

    @Test
    void updateRound_withValidIdAndRound_shouldReturnUpdatedRound() {
        // Given
        UUID id = sampleRound.getId();
        Round updatedRound = createSampleRound();
        updatedRound.setName("Updated Round Name");
        when(roundServiceRepository.findById(id)).thenReturn(Optional.of(sampleRound));
        when(roundServiceRepository.save(any(Round.class))).thenReturn(updatedRound);

        // When
        Round actualRound = roundService.updateRound(id, updatedRound);

        // Then
        assertEquals(updatedRound, actualRound);
        verify(roundServiceRepository).findById(id);
        verify(roundServiceRepository).save(any(Round.class));
    }

    @Test
    void updateRound_withInvalidId_shouldThrowRoundNotFoundException() {
        // Given
        UUID id = UUID.randomUUID();
        Round updatedRound = createSampleRound();
        when(roundServiceRepository.findById(id)).thenReturn(Optional.empty());

        // When & Then
        assertThrows(RoundNotFoundException.class, () -> roundService.updateRound(id, updatedRound));
        verify(roundServiceRepository).findById(id);
        verify(roundServiceRepository, never()).save(any(Round.class));
    }

    @Test
    void deleteRoundById_shouldCallRepositoryMethod() {
        // Given
        UUID id = sampleRound.getId();

        // When
        roundService.deleteRoundById(id);

        // Then
        verify(roundServiceRepository).deleteById(id);
    }

    @Test
    void getBracketCountFromRoundName_shouldReturnCorrectCount() {
        // Given
        String roundName = "Round of 16";

        // When
        int bracketCount = roundService.getBracketCountFromRoundName(roundName);

        // Then
        assertEquals(8, bracketCount);
    }

    @Test
    void getBracketCountFromRoundName_withInvalidName_shouldReturnZero() {
        // Given
        String roundName = "Final";

        // When
        int bracketCount = roundService.getBracketCountFromRoundName(roundName);

        // Then
        assertEquals(0, bracketCount);
    }
}