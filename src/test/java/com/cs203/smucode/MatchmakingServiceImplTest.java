package com.cs203.smucode;

import com.cs203.smucode.dto.UserDTO;
import com.cs203.smucode.models.Tournament;
import com.cs203.smucode.models.Bracket;
import com.cs203.smucode.services.BracketService;
import com.cs203.smucode.services.TournamentService;
import com.cs203.smucode.services.impl.MatchmakingServiceImpl;
import com.cs203.smucode.services.impl.UserServiceClient;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

public class MatchmakingServiceImplTest {

    @Mock
    private UserServiceClient userServiceClient;

    @Mock
    private BracketService bracketService;

    @Mock
    private TournamentService tournamentService;

    @Mock
    private Tournament tournament;

    @Mock
    private UserDTO user1, user2, user3, user4;

    @InjectMocks
    private MatchmakingServiceImpl matchmakingService;

    @BeforeEach
    public void setUp() {
        // Initialize mocks
        MockitoAnnotations.openMocks(this);

        // Set mock tournament properties
        when(tournament.getId()).thenReturn("1");
        when(tournament.getCapacity()).thenReturn(4);
        when(tournament.getStatus()).thenReturn("pending");

        // Set mock user properties
        when(user1.getSkillIndex()).thenReturn(1200.0);
        when(user1.getUsername()).thenReturn("player1");

        when(user2.getSkillIndex()).thenReturn(1300.0);
        when(user2.getUsername()).thenReturn("player2");

        when(user3.getSkillIndex()).thenReturn(1100.0);
        when(user3.getUsername()).thenReturn("player3");

        when(user4.getSkillIndex()).thenReturn(1400.0);
        when(user4.getUsername()).thenReturn("player4");
    }

    @Test
    public void testRunMatchmaking_notEnoughPlayers() {
        // Mock the behavior of getTournamentSignups to return an empty list
        when(userServiceClient.getTournamentSignups(any())).thenReturn(Collections.emptyList());

        // Act
        matchmakingService.runMatchmaking(tournament);

        // Assert
        // Verify that the tournament is canceled due to not enough players
        verify(tournamentService).updateTournament(eq("1"), any(Tournament.class));
        verify(tournament).setStatus("cancelled");
        verify(bracketService, never()).createBracket(any());
    }

    @Test
    public void testRunMatchmaking_enoughPlayers() {
        // Mock the return of getTournamentSignups to return enough players
        List<UserDTO> signups = Arrays.asList(user1, user2, user3, user4);
        when(userServiceClient.getTournamentSignups(any())).thenReturn(signups);

        // Act
        matchmakingService.runMatchmaking(tournament);

        // Assert
        // Verify that the tournament status is updated to "ongoing"
        verify(tournamentService).updateTournament(eq("1"), any(Tournament.class));
        verify(tournament).setStatus("ongoing");

        // Verify that brackets are created
        verify(bracketService, times(2)).createBracket(any(Bracket.class));
    }

    @Test
    public void testPairPlayersWithoutShuffle() {
        // Arrange: Set up the skill index and usernames for the players
        when(user1.getSkillIndex()).thenReturn(1200.0);  // Third highest skill
        when(user2.getSkillIndex()).thenReturn(1300.0);  // Second highest skill
        when(user3.getSkillIndex()).thenReturn(1100.0);  // Lowest skill
        when(user4.getSkillIndex()).thenReturn(1400.0);  // Highest skill

        when(user1.getUsername()).thenReturn("player1");
        when(user2.getUsername()).thenReturn("player2");
        when(user3.getUsername()).thenReturn("player3");
        when(user4.getUsername()).thenReturn("player4");

        // Act: Run the pairing logic without shuffle
        List<UserDTO> players = Arrays.asList(user1, user2, user3, user4);
        List<Bracket> brackets = matchmakingService.pairPlayers(players, tournament, false);

        // Assert: Validate that the correct number of brackets is created
        assertEquals(2, brackets.size(), "There should be 2 brackets for 4 players");

        // Assert correct pairings based on fixed seed order
        assertEquals("player4", brackets.get(0).getPlayer1().getUsername());  // Highest skill player
        assertEquals("player3", brackets.get(0).getPlayer2().getUsername());  // Lowest skill player

        assertEquals("player2", brackets.get(1).getPlayer1().getUsername());  // Second highest
        assertEquals("player1", brackets.get(1).getPlayer2().getUsername());  // Second lowest
    }


    @Test
    public void testPairPlayersWithShuffle() {
        // Arrange: Set up the skill index and usernames for the players
        when(user1.getSkillIndex()).thenReturn(1200.0);
        when(user2.getSkillIndex()).thenReturn(1300.0);
        when(user3.getSkillIndex()).thenReturn(1100.0);
        when(user4.getSkillIndex()).thenReturn(1400.0);

        when(user1.getUsername()).thenReturn("player1");
        when(user2.getUsername()).thenReturn("player2");
        when(user3.getUsername()).thenReturn("player3");
        when(user4.getUsername()).thenReturn("player4");

        // Act: Run the pairing logic with shuffle enabled (variable seeds are shuffled)
        List<UserDTO> players = Arrays.asList(user1, user2, user3, user4);
        List<Bracket> brackets = matchmakingService.pairPlayers(players, tournament, true);

        // Assert: Validate that the correct number of brackets is created
        assertEquals(2, brackets.size(), "There should be 2 brackets for 4 players");

        // Assert correct pairings - can't guarantee exact order because of shuffle
        List<String> topPlayers = Arrays.asList("player4", "player2");
        List<String> bottomPlayers = Arrays.asList("player1", "player3");

        // Ensure that top-seeded players are in the Player1 position
        assertEquals(true, topPlayers.contains(brackets.get(0).getPlayer1().getUsername()));
        assertEquals(true, topPlayers.contains(brackets.get(1).getPlayer1().getUsername()));

        // Ensure that bottom-seeded players are in the Player2 position
        assertEquals(true, bottomPlayers.contains(brackets.get(0).getPlayer2().getUsername()));
        assertEquals(true, bottomPlayers.contains(brackets.get(1).getPlayer2().getUsername()));
    }
}
