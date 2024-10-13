//package com.cs203.smucode;
//
//import com.cs203.smucode.constants.Status;
//import com.cs203.smucode.dto.UserDTO;
//import com.cs203.smucode.models.Tournament;
//import com.cs203.smucode.models.Bracket;
//import com.cs203.smucode.services.BracketService;
//import com.cs203.smucode.services.TournamentService;
//import com.cs203.smucode.services.impl.MatchmakingServiceImpl;
//import com.cs203.smucode.services.impl.UserServiceClient;
//import org.junit.jupiter.api.BeforeEach;
//import org.junit.jupiter.api.Test;
//import org.mockito.InjectMocks;
//import org.mockito.Mock;
//import org.mockito.MockitoAnnotations;
//
//import java.util.Arrays;
//import java.util.Collections;
//import java.util.List;
//import java.util.UUID;
//
//import static org.junit.jupiter.api.Assertions.*;
//import static org.mockito.ArgumentMatchers.any;
//import static org.mockito.Mockito.*;
//
//public class MatchmakingServiceImplTest {
//
//    @Mock
//    private UserServiceClient userServiceClient;
//
//    @Mock
//    private BracketService bracketService;
//
//    @Mock
//    private TournamentService tournamentService;
//
//    @Mock
//    private Tournament tournament;
//
//    @Mock
//    private UserDTO user1, user2, user3, user4;
//
//    @InjectMocks
//    private MatchmakingServiceImpl matchmakingService;
//
//    @BeforeEach
//    public void setUp() {
//        // Initialize mocks
//        MockitoAnnotations.openMocks(this);
//
//        // Set mock tournament properties
//        UUID tournamentId = UUID.randomUUID();
////        when(tournament.getId()).thenReturn("1");
//        when(tournament.getId()).thenReturn(tournamentId);
//        when(tournament.getCapacity()).thenReturn(4);
////        when(tournament.getStatus()).thenReturn("pending");
//        when(tournament.getStatus()).thenReturn(Status.UPCOMING);
//
//        // Set mock user properties
//        when(user1.skillIndex()).thenReturn(1200.0);
//        when(user1.username()).thenReturn("player1");
//
//        when(user2.skillIndex()).thenReturn(1300.0);
//        when(user2.username()).thenReturn("player2");
//
//        when(user3.skillIndex()).thenReturn(1100.0);
//        when(user3.username()).thenReturn("player3");
//
//        when(user4.skillIndex()).thenReturn(1400.0);
//        when(user4.username()).thenReturn("player4");
//    }
//
//    @Test
//    public void testRunMatchmaking_notEnoughPlayers() {
//        // Mock the behavior of getTournamentSignups to return an empty list
//        when(userServiceClient.getTournamentSignups(any())).thenReturn(Collections.emptyList());
//
//        // Act
//        matchmakingService.runMatchmaking(tournament);
//
//        // Assert
//        // Verify that the tournament is canceled due to not enough players
////        verify(tournamentService).updateTournament(eq("1"), any(Tournament.class));
//        verify(tournamentService).updateTournament(eq(tournament.getId()), any(Tournament.class));
////        verify(tournament).setStatus("cancelled");
//        verify(tournament).setStatus(Status.UPCOMING);
//        verify(bracketService, never()).createBracket(any());
//    }
//
//    @Test
//    public void testRunMatchmaking_tooManyPlayers_bestSelection() {
//        // Arrange: Set up the skill index and usernames for more than the allowed number of players
//        UserDTO user5 = mock(UserDTO.class);
//        UserDTO user6 = mock(UserDTO.class);
//
//        when(user1.skillIndex()).thenReturn(1200.0);  // Third highest skill
//        when(user2.skillIndex()).thenReturn(1300.0);  // Second highest skill
//        when(user3.skillIndex()).thenReturn(1100.0);  // Fifth highest skill
//        when(user4.skillIndex()).thenReturn(1400.0);  // Highest skill
//        when(user5.skillIndex()).thenReturn(900.0);   // Lowest skill
//        when(user6.skillIndex()).thenReturn(1150.0);  // Fourth highest skill
//
//        when(user1.username()).thenReturn("player1");
//        when(user2.username()).thenReturn("player2");
//        when(user3.username()).thenReturn("player3");
//        when(user4.username()).thenReturn("player4");
//        when(user5.username()).thenReturn("player5");
//        when(user6.username()).thenReturn("player6");
//
//        // Mock the return of getTournamentSignups to return more players than the capacity
//        List<UserDTO> signups = Arrays.asList(user1, user2, user3, user4, user5, user6);
//        when(userServiceClient.getTournamentSignups(any())).thenReturn(signups);
//
//        // Mock tournament capacity to only allow 4 players
//        when(tournament.getCapacity()).thenReturn(4);
//
//        // Act: Run matchmaking with the "best" selection type
//        List<UserDTO> selectedPlayers = matchmakingService.selectParticipants(signups, 4, "best");
//
//        // Assert: Verify that the top 4 players by skill are selected
//        assertEquals(4, selectedPlayers.size());
//        assertEquals("player4", selectedPlayers.get(0).username());  // Highest skill
//        assertEquals("player2", selectedPlayers.get(1).username());  // Second highest skill
//        assertEquals("player1", selectedPlayers.get(2).username());  // Third highest skill
//        assertEquals("player6", selectedPlayers.get(3).username());  // Fourth highest skill
//    }
//
//    @Test
//    public void testRunMatchmaking_tooManyPlayers_neutralSelection() {
//        // Arrange: Set up the skill index and usernames for more than the allowed number of players
//        UserDTO user5 = mock(UserDTO.class);
//        UserDTO user6 = mock(UserDTO.class);
//
//        when(user1.skillIndex()).thenReturn(1200.0);  // Third highest skill
//        when(user2.skillIndex()).thenReturn(1300.0);  // Second highest skill
//        when(user3.skillIndex()).thenReturn(1100.0);  // Fifth highest skill
//        when(user4.skillIndex()).thenReturn(1400.0);  // Highest skill
//        when(user5.skillIndex()).thenReturn(900.0);   // Lowest skill
//        when(user6.skillIndex()).thenReturn(1000.0);  // Fourth highest skill
//
//        when(user1.username()).thenReturn("player1");
//        when(user2.username()).thenReturn("player2");
//        when(user3.username()).thenReturn("player3");
//        when(user4.username()).thenReturn("player4");
//        when(user5.username()).thenReturn("player5");
//        when(user6.username()).thenReturn("player6");
//
//        // Mock the return of getTournamentSignups to return more players than the capacity
//        List<UserDTO> signups = Arrays.asList(user1, user2, user3, user4, user5, user6);
//        when(userServiceClient.getTournamentSignups(any())).thenReturn(signups);
//
//        // Mock tournament capacity to only allow 4 players
//        when(tournament.getCapacity()).thenReturn(4);
//
//        // Act: Run matchmaking with the "neutral" selection type
//        List<UserDTO> selectedPlayers = matchmakingService.selectParticipants(signups, 4, "neutral");
//
//        // Assert: Verify that the correct 4 players are selected (ignoring order for now)
//        assertEquals(4, selectedPlayers.size());
//
//        // Create a set of expected usernames
//        List<String> expectedUsernames = Arrays.asList("player2", "player1", "player6", "player3");
//
//        // Ensure that the selected players match the expected ones
//        List<String> actualUsernames = selectedPlayers.stream().map(UserDTO::username).toList();
//        assertTrue(actualUsernames.containsAll(expectedUsernames), "Selected players should be the middle ones.");
//    }
//
//
//    @Test
//    public void testRunMatchmaking_enoughPlayers() {
//        // Mock the return of getTournamentSignups to return enough players
//        List<UserDTO> signups = Arrays.asList(user1, user2, user3, user4);
//        when(userServiceClient.getTournamentSignups(any())).thenReturn(signups);
//
//        // Act
//        matchmakingService.runMatchmaking(tournament);
//
//        // Assert
//        // Verify that the tournament status is updated to "ongoing"
////        verify(tournamentService).updateTournament(eq("1"), any(Tournament.class));
//        verify(tournamentService).updateTournament(eq(tournament.getId()), any(Tournament.class));
////        verify(tournament).setStatus("ongoing");
//        verify(tournament).setStatus(Status.ONGOING);
//
//        // Verify that brackets are created
//        verify(bracketService, times(2)).createBracket(any(Bracket.class));
//    }
//
//    @Test
//    public void testPairPlayersWithoutShuffle() {
//        // Arrange: Set up the skill index and usernames for the players
//        when(user1.skillIndex()).thenReturn(1200.0);  // Third highest skill
//        when(user2.skillIndex()).thenReturn(1300.0);  // Second highest skill
//        when(user3.skillIndex()).thenReturn(1100.0);  // Lowest skill
//        when(user4.skillIndex()).thenReturn(1400.0);  // Highest skill
//
//        when(user1.username()).thenReturn("player1");
//        when(user2.username()).thenReturn("player2");
//        when(user3.username()).thenReturn("player3");
//        when(user4.username()).thenReturn("player4");
//
//        // Act: Run the pairing logic without shuffle
//        List<UserDTO> players = Arrays.asList(user1, user2, user3, user4);
//        List<Bracket> brackets = matchmakingService.pairPlayers(players, tournament, false);
//
//        // Assert: Validate that the correct number of brackets is created
//        assertEquals(2, brackets.size(), "There should be 2 brackets for 4 players");
//
//        // Assert correct pairings based on fixed seed order
////        assertEquals("player4", brackets.get(0).getPlayer1().username());  // Highest skill player
////        assertEquals("player3", brackets.get(0).getPlayer2().username());  // Lowest skill player
//        assertEquals("player4", brackets.get(0).getPlayers().getFirst().getPlayerId());  // Highest skill player
//        assertEquals("player3", brackets.get(0).getPlayers().getLast().getPlayerId());  // Lowest skill player
//
////        assertEquals("player2", brackets.get(1).getPlayer1().username());  // Second highest
////        assertEquals("player1", brackets.get(1).getPlayer2().username());  // Second lowest
//        assertEquals("player2", brackets.get(1).getPlayers().getFirst().getPlayerId());  // Highest skill player
//        assertEquals("player1", brackets.get(1).getPlayers().getLast().getPlayerId());  // Lowest skill player
//    }
//
//
//    @Test
//    public void testPairPlayersWithShuffle() {
//        // Arrange: Set up the skill index and usernames for the players
//        when(user1.skillIndex()).thenReturn(1200.0);
//        when(user2.skillIndex()).thenReturn(1300.0);
//        when(user3.skillIndex()).thenReturn(1100.0);
//        when(user4.skillIndex()).thenReturn(1400.0);
//
//        when(user1.username()).thenReturn("player1");
//        when(user2.username()).thenReturn("player2");
//        when(user3.username()).thenReturn("player3");
//        when(user4.username()).thenReturn("player4");
//
//        // Act: Run the pairing logic with shuffle enabled (variable seeds are shuffled)
//        List<UserDTO> players = Arrays.asList(user1, user2, user3, user4);
//        List<Bracket> brackets = matchmakingService.pairPlayers(players, tournament, true);
//
//        // Assert: Validate that the correct number of brackets is created
//        assertEquals(2, brackets.size(), "There should be 2 brackets for 4 players");
//
//        // Assert correct pairings - can't guarantee exact order because of shuffle
//        List<String> topPlayers = Arrays.asList("player4", "player2");
//        List<String> bottomPlayers = Arrays.asList("player1", "player3");
//
//        // Ensure that top-seeded players are in the Player1 position
////        assertEquals(true, topPlayers.contains(brackets.get(0).getPlayer1().username()));
////        assertEquals(true, topPlayers.contains(brackets.get(1).getPlayer1().username()));
//        assertEquals(true, topPlayers.contains(brackets.get(0).getPlayers().getFirst().getPlayerId()));
//        assertEquals(true, topPlayers.contains(brackets.get(1).getPlayers().getFirst().getPlayerId()));
//
//        // Ensure that bottom-seeded players are in the Player2 position
////        assertEquals(true, bottomPlayers.contains(brackets.get(0).getPlayer2().username()));
////        assertEquals(true, bottomPlayers.contains(brackets.get(1).getPlayer2().username()));
//        assertEquals(true, topPlayers.contains(brackets.get(0).getPlayers().getLast().getPlayerId()));
//        assertEquals(true, topPlayers.contains(brackets.get(1).getPlayers().getLast().getPlayerId()));
//    }
//}
