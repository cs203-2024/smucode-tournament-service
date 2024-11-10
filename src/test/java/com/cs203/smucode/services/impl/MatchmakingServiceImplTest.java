package com.cs203.smucode.services.impl;

import com.cs203.smucode.constants.Status;
import com.cs203.smucode.consumers.UserServiceConsumer;
import com.cs203.smucode.dtos.users.UserDTO;
import com.cs203.smucode.models.Bracket;
import com.cs203.smucode.models.PredictionResult;
import com.cs203.smucode.models.Round;
import com.cs203.smucode.models.Tournament;
import com.cs203.smucode.handlers.UserServiceHandler;
import com.cs203.smucode.services.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.mockito.Mockito.lenient;

@ExtendWith(MockitoExtension.class) // Use MockitoExtension for JUnit 5
class MatchmakingServiceImplTest {

    @Mock
    private UserServiceConsumer userClient;

    @Mock
    private UserServiceHandler userService;

//    @Mock
//    private EventFactory eventFactory;

    @Mock
    private BracketService bracketService;

    @Mock
    private RoundService roundService;

    @Mock
    private TournamentService tournamentService;

    @Mock
    private PredictionService predictionService;

    @InjectMocks
    private MatchmakingServiceImpl matchmakingService;

    private Tournament tournament;
    private UserDTO user1, user2, user3, user4;

    @BeforeEach
     void setUp() {
        // Set up a tournament with rounds and brackets pre-created
        tournament = new Tournament();
        tournament.setId(UUID.randomUUID());
        tournament.setCapacity(4);
        tournament.setStatus(Status.UPCOMING);
        tournament.setSignups(new HashSet<>(Arrays.asList("player1", "player2", "player3", "player4")));

        // Create and mock rounds
        Round round1 = new Round();
        round1.setId(UUID.randomUUID());
        round1.setName("Round of 4");
        round1.setTournament(tournament);
        round1.setSeqId(1);
        round1.setStatus(Status.UPCOMING);

        Round round2 = new Round();
        round2.setId(UUID.randomUUID());
        round2.setName("Round of 2");
        round2.setTournament(tournament);
        round2.setSeqId(2);
        round2.setStatus(Status.UPCOMING);

        // Create and mock brackets
        int numberOfBrackets = 2;
        List<Bracket> round1Brackets = new ArrayList<>();
        for (int i = 0; i < numberOfBrackets; i++) {
            Bracket newBracket = new Bracket();
            newBracket.setId(UUID.randomUUID());
            newBracket.setSeqId(i+1);
            round1Brackets.add(newBracket);
        }
        round1.setBrackets(round1Brackets);

        numberOfBrackets /= 2;
        List<Bracket> round2Brackets = new ArrayList<>();
        for (int i = 0; i < numberOfBrackets; i++) {
            Bracket newBracket = new Bracket();
            newBracket.setId(UUID.randomUUID());
            newBracket.setSeqId(i+1);
            round2Brackets.add(newBracket);
        }
        round2.setBrackets(round2Brackets);

        List<Round> rounds = Arrays.asList(round1, round2);
        tournament.setRounds(rounds);

        // Mark the stubbing of the round creation as lenient
        lenient().when(roundService.createRound(any())).thenReturn(round1);

        // Set up user details using direct instantiation of UserDTO
        user1 = new UserDTO("player1", "password1", "player1@example.com", "/images/player1.png", "ROLE_USER", 25.0, 8.33, 1200.0);
        user2 = new UserDTO("player2", "password2", "player2@example.com", "/images/player2.png", "ROLE_USER", 25.0, 8.33, 1300.0);
        user3 = new UserDTO("player3", "password3", "player3@example.com", "/images/player3.png", "ROLE_USER", 25.0, 8.33, 1100.0);
        user4 = new UserDTO("player4", "password4", "player4@example.com", "/images/player4.png", "ROLE_USER", 25.0, 8.33, 1400.0);

        // Mark the stubbing of the user service as lenient
        lenient().when(userService.getUsers(anyList())).thenReturn(Arrays.asList(user1, user2, user3, user4));
        lenient().when(predictionService.predictMatch(anyString(), anyString()))
                .thenReturn(new PredictionResult("", "", 0.5, 0.5));
    }

    @Test
     void testRunMatchmaking_withPreCreatedRounds() {

        // Arrange
        when(roundService.findRoundByTournamentIdAndSeqId(any(UUID.class), eq(1)))
                .thenReturn(tournament.getRounds().get(0));
        when(bracketService.findBracketByRoundIdAndSeqId(any(UUID.class), eq(1)))
                .thenReturn(tournament.getRounds().get(0).getBrackets().get(0));
        when(bracketService.findBracketByRoundIdAndSeqId(any(UUID.class), eq(2)))
                .thenReturn(tournament.getRounds().get(0).getBrackets().get(1));

        // Act: Run the matchmaking process
        matchmakingService.runMatchmaking(tournament);

        // Assert
        verify(bracketService, times(2)).updateBracket(any(UUID.class), any(Bracket.class)); // Verify that the brackets were created <><><>
        assertEquals(Status.ONGOING, tournament.getStatus()); // Assert that the tournament status was updated
    }

    @Test
     void testPairPlayers() {
        // Arrange: List of players to pair
        List<UserDTO> players = Arrays.asList(user1, user2, user3, user4);

        // Act: Run the pairing logic with no shuffling
        List<Bracket> brackets = matchmakingService.pairPlayers(players, false);

        // Assert: Validate that the correct number of brackets is created
        assertEquals(2, brackets.size(), "There should be 2 brackets for 4 players");

        // Check the players paired in the brackets
        assertEquals("player4", brackets.get(0).getPlayer1());  // Highest skill player
        assertEquals("player3", brackets.get(0).getPlayer2());  // Lowest skill player

        assertEquals("player2", brackets.get(1).getPlayer1());
        assertEquals("player1", brackets.get(1).getPlayer2());
    }

    @Test
    void selectParticipants_withWorstSelectionType_shouldSelectBottomPlayers() {
        // Arrange
        List<UserDTO> signups = Arrays.asList(
                new UserDTO("player1", "pwd", "p1@test.com", "/img.png", "ROLE_USER", 25.0, 8.33, 1400.0),
                new UserDTO("player2", "pwd", "p2@test.com", "/img.png", "ROLE_USER", 25.0, 8.33, 1300.0),
                new UserDTO("player3", "pwd", "p3@test.com", "/img.png", "ROLE_USER", 25.0, 8.33, 1200.0),
                new UserDTO("player4", "pwd", "p4@test.com", "/img.png", "ROLE_USER", 25.0, 8.33, 1100.0)
        );

        // Act
        List<UserDTO> selectedPlayers = matchmakingService.selectParticipants(signups, 2, "worst");

        // Assert
        assertEquals(2, selectedPlayers.size());
        assertEquals("player4", selectedPlayers.get(0).username());
        assertEquals("player3", selectedPlayers.get(1).username());
    }

    @Test
    void selectParticipants_withInvalidSelectionType_shouldThrowIllegalArgumentException() {
        // Arrange
        List<UserDTO> signups = Arrays.asList(
                new UserDTO("player1", "pwd", "p1@test.com", "/img.png", "ROLE_USER", 25.0, 8.33, 1200.0),
                new UserDTO("player2", "pwd", "p2@test.com", "/img.png", "ROLE_USER", 25.0, 8.33, 1100.0)
        );

        // Act & Assert
        assertThrows(IllegalArgumentException.class, () ->
                matchmakingService.selectParticipants(signups, 2, "invalid"));
    }

    @Test
    void updateBrackets_whenRoundsNotPreCreated_shouldThrowIllegalStateException() {
        // Arrange
        Tournament newTournament = new Tournament();
        newTournament.setId(UUID.randomUUID());
        newTournament.setRounds(new ArrayList<>());
        List<Bracket> bracketPairs = Arrays.asList(new Bracket(), new Bracket());

        // Act & Assert
        assertThrows(IllegalStateException.class, () ->
                matchmakingService.updateBrackets(newTournament, bracketPairs));
    }

    @Test
    void updateBrackets_whenBracketsNotPreCreated_shouldThrowIllegalStateException() {
        // Arrange
        Tournament newTournament = new Tournament();
        newTournament.setId(UUID.randomUUID());
        Round round = new Round();
        round.setBrackets(new ArrayList<>());
        newTournament.setRounds(Arrays.asList(round));
        List<Bracket> bracketPairs = Arrays.asList(new Bracket(), new Bracket());

        when(roundService.findRoundByTournamentIdAndSeqId(any(UUID.class), eq(1)))
                .thenReturn(round);

        // Act & Assert
        assertThrows(IllegalStateException.class, () ->
                matchmakingService.updateBrackets(newTournament, bracketPairs));
    }

    @Test
    void updateBrackets_whenBracketsNull_shouldThrowIllegalStateException() {
        // Arrange
        Tournament newTournament = new Tournament();
        newTournament.setId(UUID.randomUUID());

        Round round = new Round();
        round.setId(UUID.randomUUID());
        round.setSeqId(1);
        round.setBrackets(null); // Explicitly set brackets to null
        newTournament.setRounds(Arrays.asList(round));

        List<Bracket> bracketPairs = Arrays.asList(new Bracket(), new Bracket());

        when(roundService.findRoundByTournamentIdAndSeqId(newTournament.getId(), 1)).thenReturn(round);

        // Act & Assert
        IllegalStateException exception = assertThrows(IllegalStateException.class, () ->
                matchmakingService.updateBrackets(newTournament, bracketPairs));
        assertEquals("Brackets have not been pre-created in the tournament.", exception.getMessage());
    }

    @Test
    void runMatchmaking_whenTournamentAlreadyStarted_shouldThrowIllegalStateException() {
        // Arrange
        Tournament newTournament = new Tournament();
        newTournament.setStatus(Status.ONGOING);

        // Act & Assert
        assertThrows(IllegalStateException.class, () ->
                matchmakingService.runMatchmaking(newTournament));
    }

    @Test
    void selectParticipants_withNeutralSelectionType_shouldSelectMiddlePlayers() {
        // Arrange
        List<UserDTO> signups = Arrays.asList(
                new UserDTO("player1", "pwd", "p1@test.com", "/img.png", "ROLE_USER", 25.0, 8.33, 1400.0),
                new UserDTO("player2", "pwd", "p2@test.com", "/img.png", "ROLE_USER", 25.0, 8.33, 1300.0),
                new UserDTO("player3", "pwd", "p3@test.com", "/img.png", "ROLE_USER", 25.0, 8.33, 1200.0),
                new UserDTO("player4", "pwd", "p4@test.com", "/img.png", "ROLE_USER", 25.0, 8.33, 1100.0)
        );

        // Act
        List<UserDTO> selectedPlayers = matchmakingService.selectParticipants(signups, 2, "neutral");

        // Assert
        assertEquals(2, selectedPlayers.size());
        // Since we're selecting from the middle, we should get players 2 and 3 (indices 1 and 2)
        assertTrue(selectedPlayers.stream().anyMatch(p -> p.username().equals("player2")));
        assertTrue(selectedPlayers.stream().anyMatch(p -> p.username().equals("player3")));
    }

    @Test
    void pairPlayers_withShuffleEnabled_shouldProduceRandomPairings() {
        // Arrange
        List<UserDTO> players = Arrays.asList(
                new UserDTO("player1", "pwd", "p1@test.com", "/img.png", "ROLE_USER", 25.0, 8.33, 1400.0),
                new UserDTO("player2", "pwd", "p2@test.com", "/img.png", "ROLE_USER", 25.0, 8.33, 1300.0),
                new UserDTO("player3", "pwd", "p3@test.com", "/img.png", "ROLE_USER", 25.0, 8.33, 1200.0),
                new UserDTO("player4", "pwd", "p4@test.com", "/img.png", "ROLE_USER", 25.0, 8.33, 1100.0)
        );

        when(predictionService.predictMatch(anyString(), anyString()))
                .thenReturn(new PredictionResult("", "", 0.5, 0.5));

        // Act
        List<Bracket> brackets = matchmakingService.pairPlayers(players, true);

        // Assert
        assertEquals(2, brackets.size());
        verify(predictionService, times(2)).predictMatch(anyString(), anyString());

        // Verify that players are assigned to brackets
        Set<String> allPlayers = new HashSet<>();
        for (Bracket bracket : brackets) {
            allPlayers.add(bracket.getPlayer1());
            allPlayers.add(bracket.getPlayer2());
        }
        assertEquals(4, allPlayers.size());
    }
}

