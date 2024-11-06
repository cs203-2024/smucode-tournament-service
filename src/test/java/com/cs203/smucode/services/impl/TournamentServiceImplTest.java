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

    @Test
    void endRound_shouldUpdateRoundStatusAndReturnTournament() {
        // Arrange
        UUID roundId = UUID.randomUUID();
        UUID tournamentId = sampleTournament.getId();

        // Create current round
        Round currentRound = new Round();
        currentRound.setId(roundId);
        currentRound.setSeqId(1);
        currentRound.setTournament(sampleTournament);
        currentRound.setBrackets(Collections.singletonList(new Bracket())); // Single bracket for final round

        // Create next round
        Round nextRound = new Round();
        nextRound.setId(UUID.randomUUID());
        nextRound.setSeqId(2);
        nextRound.setName("Round of 8");
        nextRound.setTournament(sampleTournament);

        // Setup mocks
        when(roundService.findRoundById(roundId)).thenReturn(currentRound);
        when(roundService.findRoundByTournamentIdAndSeqId(eq(tournamentId), eq(2))).thenReturn(nextRound);
        when(tournamentServiceRepository.findById(tournamentId)).thenReturn(Optional.of(sampleTournament));
        when(tournamentServiceRepository.save(any(Tournament.class))).thenReturn(sampleTournament);

        // Act
        Tournament result = tournamentService.endRound(roundId);

        // Assert
        verify(roundService).findRoundById(roundId);
        verify(tournamentServiceRepository).findById(tournamentId);
        verify(tournamentServiceRepository).save(any(Tournament.class));
        verify(roundService).updateRound(eq(roundId), any(Round.class));
        assertEquals(Status.COMPLETED, currentRound.getStatus());
    }

    @Test
    void deleteTournamentParticipant_shouldRemoveParticipantAndUpdateTournament() {
        // Arrange
        UUID tournamentId = sampleTournament.getId();
        String participantToRemove = "User1";
        Round currentRound = new Round();
        currentRound.setId(UUID.randomUUID());

        when(tournamentServiceRepository.findById(tournamentId)).thenReturn(Optional.of(sampleTournament));
        when(roundService.findRoundByTournamentIdAndName(eq(tournamentId), anyString())).thenReturn(currentRound);
        when(tournamentServiceRepository.save(any(Tournament.class))).thenReturn(sampleTournament);

        // Act
        Tournament result = tournamentService.deleteTournamentParticipant(tournamentId, participantToRemove);

        // Assert
        assertFalse(result.getParticipants().contains(participantToRemove));
        verify(roundService).removePlayerFromOngoingRound(currentRound.getId(), participantToRemove);
        verify(tournamentServiceRepository).save(any(Tournament.class));
    }

    @Test
    void findAllEligibleTournamentsForUser_shouldReturnEligibleTournaments() {
        // Arrange
        String username = "TestUser";
        LocalDateTime now = LocalDateTime.now();
        List<Tournament> openTournaments = Arrays.asList(sampleTournament);

        when(tournamentServiceRepository.findBySignupEndDateAfterAndStatus(any(LocalDateTime.class), eq(Status.UPCOMING)))
                .thenReturn(Optional.of(openTournaments));

        // Act
        List<Tournament> result = tournamentService.findAllEligibleTournamentsForUser(username);

        // Assert
        assertFalse(result.isEmpty());
        assertEquals(1, result.size());
        verify(tournamentServiceRepository).findBySignupEndDateAfterAndStatus(any(LocalDateTime.class), eq(Status.UPCOMING));
    }

    @Test
    void addTournamentSignup_whenSignupsClosed_shouldThrowIllegalStateException() {
        // Arrange
        UUID tournamentId = sampleTournament.getId();
        String newSignup = "NewUser";
        Tournament closedTournament = createSampleTournament();
        closedTournament.setSignupEndDate(LocalDateTime.now().minusDays(1));

        when(tournamentServiceRepository.findById(tournamentId)).thenReturn(Optional.of(closedTournament));

        // Act & Assert
        assertThrows(IllegalStateException.class, () ->
                tournamentService.addTournamentSignup(tournamentId, newSignup));
        verify(tournamentServiceRepository, never()).save(any(Tournament.class));
    }

    @Test
    void findTournamentsWithSignUpBefore_shouldReturnTournaments() {
        // Arrange
        LocalDateTime dateTime = LocalDateTime.now();
        List<Tournament> expectedTournaments = Collections.singletonList(sampleTournament);

        when(tournamentServiceRepository.findBySignupEndDateBeforeAndStatus(dateTime, Status.UPCOMING))
                .thenReturn(Optional.of(expectedTournaments));

        // Act
        List<Tournament> result = tournamentService.findTournamentsWithSignUpBefore(dateTime);

        // Assert
        assertEquals(expectedTournaments, result);
        verify(tournamentServiceRepository).findBySignupEndDateBeforeAndStatus(dateTime, Status.UPCOMING);
    }

    @Test
    void findTournamentsWithSignUpAfter_shouldReturnTournaments() {
        // Arrange
        LocalDateTime dateTime = LocalDateTime.now();
        List<Tournament> expectedTournaments = Collections.singletonList(sampleTournament);

        when(tournamentServiceRepository.findBySignupEndDateAfterAndStatus(dateTime, Status.UPCOMING))
                .thenReturn(Optional.of(expectedTournaments));

        // Act
        List<Tournament> result = tournamentService.findTournamentsWithSignUpAfter(dateTime);

        // Assert
        assertEquals(expectedTournaments, result);
        verify(tournamentServiceRepository).findBySignupEndDateAfterAndStatus(dateTime, Status.UPCOMING);
    }

    @Test
    void endBracket_shouldEndBracketAndReturnTournament() {
        // Arrange
        UUID bracketId = UUID.randomUUID();
        Bracket bracket = new Bracket();
        Round round = new Round();
        round.setTournament(sampleTournament);
        bracket.setRound(round);

        when(bracketService.endBracket(bracketId)).thenReturn(bracket);

        // Act
        Tournament result = tournamentService.endBracket(bracketId);

        // Assert
        assertEquals(sampleTournament, result);
        verify(bracketService).endBracket(bracketId);
    }

    @Test
    void findAllTournamentsByRegistrant_shouldReturnTournaments() {
        // Arrange
        String registrant = "TestUser";
        List<Tournament> expectedTournaments = Collections.singletonList(sampleTournament);

        when(tournamentServiceRepository.findByRegistrant(registrant))
                .thenReturn(Optional.of(expectedTournaments));

        // Act
        List<Tournament> result = tournamentService.findAllTournamentsByRegistrant(registrant);

        // Assert
        assertEquals(expectedTournaments, result);
        verify(tournamentServiceRepository).findByRegistrant(registrant);
    }

    @Test
    void deleteTournamentById_whenTournamentNotFound_shouldThrowException() {
        // Arrange
        UUID id = UUID.randomUUID();
        when(tournamentServiceRepository.existsById(id)).thenReturn(false);

        // Act & Assert
        assertThrows(TournamentNotFoundException.class, () ->
                tournamentService.deleteTournamentById(id));
        verify(tournamentServiceRepository, never()).deleteById(id);
    }

    @Test
    void endRound_withFinalRound_shouldCompleteTournament() {
        // Arrange
        UUID roundId = UUID.randomUUID();
        UUID tournamentId = sampleTournament.getId();

        // Create final round with single bracket
        Round finalRound = new Round();
        finalRound.setId(roundId);
        finalRound.setSeqId(1);
        finalRound.setTournament(sampleTournament);
        finalRound.setBrackets(Collections.singletonList(new Bracket())); // Single bracket indicates final round

        when(roundService.findRoundById(roundId)).thenReturn(finalRound);
        when(tournamentServiceRepository.findById(tournamentId)).thenReturn(Optional.of(sampleTournament));
        when(tournamentServiceRepository.save(any(Tournament.class))).thenReturn(sampleTournament);

        // Act
        Tournament result = tournamentService.endRound(roundId);

        // Assert
        verify(roundService).findRoundById(roundId);
        verify(tournamentServiceRepository).findById(tournamentId);
        verify(tournamentServiceRepository).save(any(Tournament.class));
        assertEquals(Status.COMPLETED, result.getStatus());
    }

    @Test
    void deleteTournamentParticipant_whenParticipantNotFound_shouldThrowIllegalArgumentException() {
        // Arrange
        UUID tournamentId = sampleTournament.getId();
        String nonExistentParticipant = "NonExistentUser";
        sampleTournament.setParticipants(new HashSet<>(Collections.singletonList("DifferentUser")));

        when(tournamentServiceRepository.findById(tournamentId)).thenReturn(Optional.of(sampleTournament));

        // Act & Assert
        assertThrows(IllegalArgumentException.class, () ->
                tournamentService.deleteTournamentParticipant(tournamentId, nonExistentParticipant));
    }

    @Test
    void deleteTournamentSignup_whenSignupNotFound_shouldThrowIllegalArgumentException() {
        // Arrange
        UUID tournamentId = sampleTournament.getId();
        String nonExistentSignup = "NonExistentUser";
        sampleTournament.setSignups(new HashSet<>(Collections.singletonList("DifferentUser")));

        when(tournamentServiceRepository.findById(tournamentId)).thenReturn(Optional.of(sampleTournament));

        // Act & Assert
        assertThrows(IllegalArgumentException.class, () ->
                tournamentService.deleteTournamentSignup(tournamentId, nonExistentSignup));
    }

    @Test
    void addTournamentSignup_whenTournamentNotFound_shouldThrowTournamentNotFoundException() {
        // Arrange
        UUID tournamentId = UUID.randomUUID();
        String newSignup = "NewUser";

        when(tournamentServiceRepository.findById(tournamentId)).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(TournamentNotFoundException.class, () ->
                tournamentService.addTournamentSignup(tournamentId, newSignup));
    }

    @Test
    void createTournament_whenTournamentIsNull_shouldReturnNull() {
        // Arrange
        Tournament nullTournament = null;

        // Act
        Tournament result = tournamentService.createTournament(nullTournament);

        // Assert
        assertNull(result);
        verify(tournamentServiceRepository, never()).save(any());
        verify(roundService, never()).createRound(any());
    }

    @Test
    void findAllEligibleTournamentsForUser_whenUserAlreadySignedUp_shouldExcludeTournament() {
        // Arrange
        String username = "TestUser";
        Tournament signedUpTournament = createSampleTournament();
        signedUpTournament.getSignups().add(username);
        Tournament eligibleTournament = createSampleTournament();
        eligibleTournament.setId(UUID.randomUUID());
        List<Tournament> openTournaments = Arrays.asList(signedUpTournament, eligibleTournament);

        when(tournamentServiceRepository.findBySignupEndDateAfterAndStatus(any(LocalDateTime.class), eq(Status.UPCOMING)))
                .thenReturn(Optional.of(openTournaments));

        // Act
        List<Tournament> result = tournamentService.findAllEligibleTournamentsForUser(username);

        // Assert
        assertFalse(result.isEmpty());
        assertEquals(1, result.size());
        assertFalse(result.contains(signedUpTournament));
        assertTrue(result.contains(eligibleTournament));
    }

    @Test
    void endRound_withNonFinalRound_shouldProgressToNextRound() {
        // Arrange
        UUID roundId = UUID.randomUUID();
        UUID tournamentId = sampleTournament.getId();

        // Create current round with two brackets
        Round currentRound = new Round();
        currentRound.setId(roundId);
        currentRound.setSeqId(2);
        currentRound.setTournament(sampleTournament);
        List<Bracket> brackets = new ArrayList<>();
        Bracket bracket1 = new Bracket();
        bracket1.setWinner("Player1");
        Bracket bracket2 = new Bracket();
        bracket2.setWinner("Player2");
        brackets.add(bracket1);
        brackets.add(bracket2);
        currentRound.setBrackets(brackets);

        // Create next round
        Round nextRound = new Round();
        nextRound.setId(UUID.randomUUID());
        nextRound.setSeqId(3);
        nextRound.setName("Round of 4");
        nextRound.setTournament(sampleTournament);

        // Setup mocks
        when(roundService.findRoundById(roundId)).thenReturn(currentRound);
        when(roundService.findRoundByTournamentIdAndSeqId(eq(tournamentId), eq(3))).thenReturn(nextRound);
        when(tournamentServiceRepository.findById(tournamentId)).thenReturn(Optional.of(sampleTournament));
        when(tournamentServiceRepository.save(any(Tournament.class))).thenReturn(sampleTournament);

        // Act
        Tournament result = tournamentService.endRound(roundId);

        // Assert
        verify(roundService).findRoundById(roundId);
        verify(roundService).populateNextRound(roundId, nextRound.getId());
        verify(tournamentServiceRepository).save(any(Tournament.class));
        assertEquals("Round of 4", result.getCurrentRound());
    }

    @Test
    void deleteTournamentParticipant_whenTournamentNotFound_shouldThrowTournamentNotFoundException() {
        // Arrange
        UUID tournamentId = UUID.randomUUID();
        String participant = "User1";

        when(tournamentServiceRepository.findById(tournamentId)).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(TournamentNotFoundException.class, () ->
                tournamentService.deleteTournamentParticipant(tournamentId, participant));
        verify(roundService, never()).removePlayerFromOngoingRound(any(), anyString());
    }

    @Test
    void deleteTournamentParticipant_shouldRemoveFromOngoingRoundAndUpdateTournament() {
        // Arrange
        UUID tournamentId = sampleTournament.getId();
        String participantToRemove = "User1";
        Round currentRound = new Round();
        currentRound.setId(UUID.randomUUID());
        sampleTournament.setCurrentRound("Round of 16");

        when(tournamentServiceRepository.findById(tournamentId)).thenReturn(Optional.of(sampleTournament));
        when(roundService.findRoundByTournamentIdAndName(tournamentId, "Round of 16")).thenReturn(currentRound);
        when(tournamentServiceRepository.save(any(Tournament.class))).thenReturn(sampleTournament);

        // Act
        Tournament result = tournamentService.deleteTournamentParticipant(tournamentId, participantToRemove);

        // Assert
        verify(roundService).removePlayerFromOngoingRound(currentRound.getId(), participantToRemove);
        verify(tournamentServiceRepository).save(any(Tournament.class));
        assertFalse(result.getParticipants().contains(participantToRemove));
    }

    @Test
    void deleteTournamentSignup_whenTournamentNotFound_shouldThrowTournamentNotFoundException() {
        // Arrange
        UUID tournamentId = UUID.randomUUID();
        String signup = "User1";

        when(tournamentServiceRepository.findById(tournamentId)).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(TournamentNotFoundException.class, () ->
                tournamentService.deleteTournamentSignup(tournamentId, signup));
    }

    @Test
    void deleteTournamentSignup_whenSignupExists_shouldRemoveAndReturnUpdatedTournament() {
        // Arrange
        UUID tournamentId = sampleTournament.getId();
        String signupToRemove = "User1";
        Set<String> signups = new HashSet<>();
        signups.add(signupToRemove);
        signups.add("User2");
        sampleTournament.setSignups(signups);

        when(tournamentServiceRepository.findById(tournamentId)).thenReturn(Optional.of(sampleTournament));
        when(tournamentServiceRepository.save(any(Tournament.class))).thenReturn(sampleTournament);

        // Act
        Tournament result = tournamentService.deleteTournamentSignup(tournamentId, signupToRemove);

        // Assert
        assertFalse(result.getSignups().contains(signupToRemove));
        verify(tournamentServiceRepository).save(sampleTournament);
        assertEquals(1, result.getSignups().size());
    }
}