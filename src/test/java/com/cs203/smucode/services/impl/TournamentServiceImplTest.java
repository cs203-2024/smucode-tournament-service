package com.cs203.smucode.services.impl;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.*;

import com.cs203.smucode.constants.Status;
import com.cs203.smucode.exceptions.TournamentNotFoundException;
import com.cs203.smucode.models.*;
import com.cs203.smucode.repositories.TournamentServiceRepository;
import com.cs203.smucode.services.BracketService;
import com.cs203.smucode.services.RoundService;

import java.time.LocalDateTime;
import java.util.*;

class TournamentServiceImplTest {

    @Mock
    private TournamentServiceRepository tournamentServiceRepository;

    @Mock
    private RoundService roundService;

    @Mock
    private BracketService bracketService;

    @InjectMocks
    private TournamentServiceImpl tournamentService;

    private Tournament sampleTournament;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        sampleTournament = createSampleTournament();
    }

    private Tournament createSampleTournament() {
        Tournament tournament = new Tournament();
        tournament.setId(UUID.randomUUID());
        tournament.setName("Sample Tournament");
        tournament.setDescription("A sample tournament for testing");
        tournament.setStartDate(LocalDateTime.now().plusDays(7));
        tournament.setEndDate(LocalDateTime.now().plusDays(14));
        tournament.setFormat("single-elimination");
        tournament.setCapacity(16);
        tournament.setOrganiser("TestOrganiser");
        tournament.setTimeWeight(30);
        tournament.setMemWeight(30);
        tournament.setTestCaseWeight(40);
        tournament.setStatus(Status.UPCOMING);
        tournament.setSignupStartDate(LocalDateTime.now());
        tournament.setSignupEndDate(LocalDateTime.now().plusDays(5));
        tournament.setSignups(new HashSet<>(Arrays.asList("User1", "User2")));
        tournament.setParticipants(new HashSet<>(Arrays.asList("User1", "User2")));
        tournament.setCurrentRound("Round of 16");
        return tournament;
    }

    @Test
    void findAllTournaments_shouldReturnListOfTournaments() {
        // Arrange
        List<Tournament> expectedTournaments = Collections.singletonList(sampleTournament);
        when(tournamentServiceRepository.findAll()).thenReturn(expectedTournaments);

        // Act
        List<Tournament> actualTournaments = tournamentService.findAllTournaments();

        // Assert
        assertEquals(expectedTournaments, actualTournaments);
        verify(tournamentServiceRepository).findAll();
    }

    @Test
    void findTournamentById_withValidId_shouldReturnTournament() {
        // Arrange
        UUID id = sampleTournament.getId();
        when(tournamentServiceRepository.findById(id)).thenReturn(Optional.of(sampleTournament));

        // Act
        Tournament actualTournament = tournamentService.findTournamentById(id);

        // Assert
        assertEquals(sampleTournament, actualTournament);
        verify(tournamentServiceRepository).findById(id);
    }

    @Test
    void findTournamentById_withInvalidId_shouldThrowTournamentNotFoundException() {
        // Arrange
        UUID id = UUID.randomUUID();
        when(tournamentServiceRepository.findById(id)).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(TournamentNotFoundException.class, () -> tournamentService.findTournamentById(id));
        verify(tournamentServiceRepository).findById(id);
    }

    @Test
    void findAllTournamentsByOrganiser_shouldReturnListOfTournaments() {
        // Arrange
        String organiser = "TestOrganiser";
        List<Tournament> expectedTournaments = Collections.singletonList(sampleTournament);
        when(tournamentServiceRepository.findByOrganiser(organiser)).thenReturn(Optional.of(expectedTournaments));

        // Act
        List<Tournament> actualTournaments = tournamentService.findAllTournamentsByOrganiser(organiser);

        // Assert
        assertEquals(expectedTournaments, actualTournaments);
        verify(tournamentServiceRepository).findByOrganiser(organiser);
    }

    @Test
    void findAllTournamentsByStatus_shouldReturnListOfTournaments() {
        // Arrange
        Status status = Status.UPCOMING;
        List<Tournament> expectedTournaments = Collections.singletonList(sampleTournament);
        when(tournamentServiceRepository.findByStatus(status)).thenReturn(Optional.of(expectedTournaments));

        // Act
        List<Tournament> actualTournaments = tournamentService.findAllTournamentsByStatus(status);

        // Assert
        assertEquals(expectedTournaments, actualTournaments);
        verify(tournamentServiceRepository).findByStatus(status);
    }

    @Test
    void findAllTournamentsByParticipant_shouldReturnListOfTournaments() {
        // Arrange
        String participant = "User1";
        List<Tournament> expectedTournaments = Collections.singletonList(sampleTournament);
        when(tournamentServiceRepository.findByParticipant(participant)).thenReturn(Optional.of(expectedTournaments));

        // Act
        List<Tournament> actualTournaments = tournamentService.findAllTournamentsByParticipant(participant);

        // Assert
        assertEquals(expectedTournaments, actualTournaments);
        verify(tournamentServiceRepository).findByParticipant(participant);
    }

    @Test
    void createTournament_shouldCreateTournamentAndRounds() {
        // Arrange
        Tournament tournamentToCreate = createSampleTournament();
        tournamentToCreate.setId(sampleTournament.getId());
        tournamentToCreate.setStartDate(sampleTournament.getStartDate());
        tournamentToCreate.setEndDate(sampleTournament.getEndDate());
        tournamentToCreate.setSignupStartDate(sampleTournament.getSignupStartDate());
        tournamentToCreate.setSignupEndDate(sampleTournament.getSignupEndDate());
        when(tournamentServiceRepository.save(tournamentToCreate)).thenReturn(sampleTournament);
        when(roundService.createRound(any(Round.class))).thenReturn(new Round());

        // Act
        Tournament createdTournament = tournamentService.createTournament(tournamentToCreate);

        // Assert
        assertEquals(sampleTournament, createdTournament);
        verify(tournamentServiceRepository).save(tournamentToCreate);
        verify(roundService, times(4)).createRound(any(Round.class)); // log2(16) = 4 rounds
    }

    @Test
    void updateTournament_withValidIdAndTournament_shouldReturnUpdatedTournament() {
        // Arrange
        UUID id = sampleTournament.getId();
        Tournament updatedTournament = createSampleTournament();
        updatedTournament.setName("Updated Tournament Name");
        when(tournamentServiceRepository.findById(id)).thenReturn(Optional.of(sampleTournament));
        when(tournamentServiceRepository.save(any(Tournament.class))).thenReturn(updatedTournament);

        // Act
        Tournament actualTournament = tournamentService.updateTournament(id, updatedTournament);

        // Assert
        assertEquals(updatedTournament, actualTournament);
        verify(tournamentServiceRepository).findById(id);
        verify(tournamentServiceRepository).save(any(Tournament.class));
    }

    @Test
    void updateTournament_withInvalidId_shouldThrowTournamentNotFoundException() {
        // Arrange
        UUID id = UUID.randomUUID();
        Tournament updatedTournament = createSampleTournament();
        when(tournamentServiceRepository.findById(id)).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(TournamentNotFoundException.class, () -> tournamentService.updateTournament(id, updatedTournament));
        verify(tournamentServiceRepository).findById(id);
        verify(tournamentServiceRepository, never()).save(any(Tournament.class));
    }

    @Test
    void addTournamentSignup_shouldAddSignupAndReturnUpdatedTournament() {
        // Arrange
        UUID id = sampleTournament.getId();
        String newSignup = "NewUser";
        Tournament updatedTournament = createSampleTournament();
        updatedTournament.getSignups().add(newSignup);
        when(tournamentServiceRepository.findById(id)).thenReturn(Optional.of(sampleTournament));
        when(tournamentServiceRepository.save(any(Tournament.class))).thenReturn(updatedTournament);

        // Act
        Tournament actualTournament = tournamentService.addTournamentSignup(id, newSignup);

        // Assert
        assertTrue(actualTournament.getSignups().contains(newSignup));
        verify(tournamentServiceRepository).findById(id);
        verify(tournamentServiceRepository).save(any(Tournament.class));
    }

    @Test
    void deleteTournamentSignup_shouldRemoveSignupAndReturnUpdatedTournament() {
        // Arrange
        UUID id = sampleTournament.getId();
        String signupToRemove = "User1";
        Tournament updatedTournament = createSampleTournament();
        updatedTournament.getSignups().remove(signupToRemove);
        when(tournamentServiceRepository.findById(id)).thenReturn(Optional.of(sampleTournament));
        when(tournamentServiceRepository.save(any(Tournament.class))).thenReturn(updatedTournament);

        // Act
        Tournament actualTournament = tournamentService.deleteTournamentSignup(id, signupToRemove);

        // Assert
        assertFalse(actualTournament.getSignups().contains(signupToRemove));
        verify(tournamentServiceRepository).findById(id);
        verify(tournamentServiceRepository).save(any(Tournament.class));
    }

//    @Test
//    void updateTournamentProgress_shouldUpdateRoundsAndReturnUpdatedTournament() {
//        // Arrange
//        UUID id = sampleTournament.getId();
//        Round currentRound = new Round();
//        currentRound.setId(UUID.randomUUID());
//        currentRound.setName("Round of 16");
//        currentRound.setSeqId(1);
//
//        Round nextRound = new Round();
//        nextRound.setId(UUID.randomUUID());
//        nextRound.setName("Round of 8");
//        nextRound.setSeqId(2);
//        nextRound.setBrackets(Arrays.asList(new Bracket(), new Bracket(), new Bracket(), new Bracket()));
//
//        when(tournamentServiceRepository.findById(id)).thenReturn(Optional.of(sampleTournament));
//        when(roundService.findRoundByTournamentIdAndName(id, "Round of 16")).thenReturn(currentRound);
//        when(roundService.findRoundByTournamentIdAndSeqId(id, 2)).thenReturn(nextRound);
//        when(bracketService.findBracketByRoundIdAndSeqId(any(UUID.class), anyInt())).thenReturn(new Bracket());
//        when(tournamentServiceRepository.save(any(Tournament.class))).thenReturn(sampleTournament);
//
//        // Act
//        Tournament actualTournament = tournamentService.updateTournamentProgress(id);
//
//        // Assert
//        assertEquals("Round of 8", actualTournament.getCurrentRound());
//        verify(tournamentServiceRepository).findById(id);
//        verify(roundService).findRoundByTournamentIdAndName(id, "Round of 16");
//        verify(roundService).findRoundByTournamentIdAndSeqId(id, 2);
//        verify(bracketService, times(12)).findBracketByRoundIdAndSeqId(any(UUID.class), anyInt());
//        verify(bracketService, times(4)).updateBracket(any(UUID.class), any(Bracket.class));
//        verify(tournamentServiceRepository).save(any(Tournament.class));
//    }

    @Test
    void deleteTournamentById_shouldCallRepositoryMethod() {
        // Arrange
        UUID id = sampleTournament.getId();
        when(tournamentServiceRepository.existsById(id)).thenReturn(true);

        // Act
        tournamentService.deleteTournamentById(id);

        // Assert
        verify(tournamentServiceRepository).deleteById(id);
    }
}