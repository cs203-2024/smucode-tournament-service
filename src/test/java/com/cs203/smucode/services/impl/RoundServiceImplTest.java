package com.cs203.smucode.services.impl;

import com.cs203.smucode.exceptions.TournamentNotFoundException;
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
        // Assert
        UUID tournamentId = sampleTournament.getId();
        List<Round> expectedRounds = Collections.singletonList(sampleRound);
        when(roundServiceRepository.findByTournamentId(tournamentId)).thenReturn(Optional.of(expectedRounds));

        // Act
        List<Round> actualRounds = roundService.findAllRoundsByTournamentId(tournamentId);

        // Assert
        assertEquals(expectedRounds, actualRounds);
        verify(roundServiceRepository).findByTournamentId(tournamentId);
    }

    @Test
    void findRoundById_withValidId_shouldReturnRound() {
        // Assert
        UUID id = sampleRound.getId();
        when(roundServiceRepository.findById(id)).thenReturn(Optional.of(sampleRound));

        // Act
        Round actualRound = roundService.findRoundById(id);

        // Assert
        assertEquals(sampleRound, actualRound);
        verify(roundServiceRepository).findById(id);
    }

    @Test
    void findRoundById_withInvalidId_shouldThrowBadRequestException() {
        // Arrange
        UUID id = UUID.randomUUID();
        when(roundServiceRepository.findById(id)).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(RoundNotFoundException.class, () -> roundService.findRoundById(id));
        verify(roundServiceRepository).findById(id);
    }

    @Test
    void findRoundByTournamentIdAndSeqId_shouldReturnRound() {
        // Assert
        UUID tournamentId = sampleTournament.getId();
        int seqId = sampleRound.getSeqId();
        when(roundServiceRepository.findByTournamentIdAndSeqId(tournamentId, seqId)).thenReturn(Optional.ofNullable(sampleRound));

        // Act
        Round actualRound = roundService.findRoundByTournamentIdAndSeqId(tournamentId, seqId);

        // Assert
        assertEquals(sampleRound, actualRound);
        verify(roundServiceRepository).findByTournamentIdAndSeqId(tournamentId, seqId);
    }

    @Test
    void findRoundByTournamentIdAndSeqId_withInvalidArguments_shouldThrowBadRequestException() {
        // Arrange
        UUID tournamentId = UUID.randomUUID();
        int seqId = -1;

        when(roundServiceRepository.findByTournamentIdAndSeqId(tournamentId, seqId)).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(RoundNotFoundException.class, () -> roundService.findRoundByTournamentIdAndSeqId(tournamentId, seqId));
        verify(roundServiceRepository).findByTournamentIdAndSeqId(tournamentId, seqId);
    }

    @Test
    void findRoundByTournamentIdAndName_shouldReturnRound() {
        // Assert
        UUID tournamentId = sampleTournament.getId();
        String name = sampleRound.getName();
        when(roundServiceRepository.findByTournamentIdAndName(tournamentId, name)).thenReturn(Optional.ofNullable(sampleRound));

        // Act
        Round actualRound = roundService.findRoundByTournamentIdAndName(tournamentId, name);

        // Assert
        assertEquals(sampleRound, actualRound);
        verify(roundServiceRepository).findByTournamentIdAndName(tournamentId, name);
    }

    @Test
    void createRound_shouldReturnCreatedRoundAndCreateBrackets() {
        // Assert
        Round roundToCreate = createSampleRound();
        when(roundServiceRepository.save(roundToCreate)).thenReturn(roundToCreate);

        // Act
        Round actualRound = roundService.createRound(roundToCreate);

        // Assert
        assertEquals(roundToCreate, actualRound);
        verify(roundServiceRepository).save(roundToCreate);
//       TODO: MOCKDATA
        verify(bracketService, times(2)).createBracket(any(Bracket.class));
    }

    @Test
    void updateRound_withValidIdAndRound_shouldReturnUpdatedRound() {
        // Assert
        UUID id = sampleRound.getId();
        Round updatedRound = createSampleRound();
        updatedRound.setName("Updated Round Name");
        when(roundServiceRepository.findById(id)).thenReturn(Optional.of(sampleRound));
        when(roundServiceRepository.save(any(Round.class))).thenReturn(updatedRound);

        // Act
        Round actualRound = roundService.updateRound(id, updatedRound);

        // Assert
        assertEquals(updatedRound, actualRound);
        verify(roundServiceRepository).findById(id);
        verify(roundServiceRepository).save(any(Round.class));
    }

    @Test
    void updateRound_withInvalidId_shouldThrowRoundNotFoundException() {
        // Assert
        UUID id = UUID.randomUUID();
        Round updatedRound = createSampleRound();
        when(roundServiceRepository.findById(id)).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(RoundNotFoundException.class, () -> roundService.updateRound(id, updatedRound));
        verify(roundServiceRepository).findById(id);
        verify(roundServiceRepository, never()).save(any(Round.class));
    }

    @Test
    void deleteRoundById_shouldCallRepositoryMethod() {
        // Assert
        UUID id = sampleRound.getId();
        when(roundServiceRepository.existsById(id)).thenReturn(true);

        // Act
        roundService.deleteRoundById(id);

        // Assert
        verify(roundServiceRepository).deleteById(id);
    }

    @Test
    void getBracketCountFromRoundName_shouldReturnCorrectCount() {
        // Assert
        String roundName = "Round of 16";

        // Act
        int bracketCount = roundService.getBracketCountFromRoundName(roundName);

        // Assert
        assertEquals(8, bracketCount);
    }

    @Test
    void getBracketCountFromRoundName_withInvalidName_shouldReturnZero() {
        // Assert
        String roundName = "Final";

        // Act
        int bracketCount = roundService.getBracketCountFromRoundName(roundName);

        // Assert
        assertEquals(0, bracketCount);
    }
}