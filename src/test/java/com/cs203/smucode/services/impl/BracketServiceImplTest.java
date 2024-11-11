package com.cs203.smucode.services.impl;

import com.cs203.smucode.constants.Status;
import com.cs203.smucode.exceptions.BracketNotFoundException;
import com.cs203.smucode.exceptions.UserNotFoundException;
import com.cs203.smucode.models.Bracket;
import com.cs203.smucode.models.PlayerInfo;
import com.cs203.smucode.models.Round;
import com.cs203.smucode.models.Tournament;
import com.cs203.smucode.repositories.BracketServiceRepository;
import com.cs203.smucode.repositories.RoundServiceRepository;
import com.cs203.smucode.repositories.TournamentServiceRepository;
import com.cs203.smucode.services.EventService;
import com.cs203.smucode.services.RatingUpdateService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class BracketServiceImplTest {

    @Mock
    private BracketServiceRepository bracketServiceRepository;

    @Mock
    private RoundServiceRepository roundServiceRepository;

    @Mock
    private TournamentServiceRepository tournamentServiceRepository;

    @Mock
    private RatingUpdateService ratingUpdateService;

    @Mock
    private EventService eventService;

    @InjectMocks
    private BracketServiceImpl bracketService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void findAllBracketsByRoundId() {
        UUID roundId = UUID.randomUUID();
        List<Bracket> expectedBrackets = Arrays.asList(new Bracket(), new Bracket());
        when(bracketServiceRepository.findByRoundId(roundId)).thenReturn(Optional.of(expectedBrackets));

        List<Bracket> result = bracketService.findAllBracketsByRoundId(roundId);

        assertEquals(expectedBrackets, result);
        verify(bracketServiceRepository).findByRoundId(roundId);
    }

    @Test
    void findBracketById() {
        UUID bracketId = UUID.randomUUID();
        Bracket expectedBracket = new Bracket();
        when(bracketServiceRepository.findById(bracketId)).thenReturn(Optional.of(expectedBracket));

        Bracket result = bracketService.findBracketById(bracketId);

        assertEquals(expectedBracket, result);
        verify(bracketServiceRepository).findById(bracketId);
    }

    @Test
    void findBracketByRoundIdAndSeqId() {
        UUID roundId = UUID.randomUUID();
        int seqId = 1;
        Bracket expectedBracket = new Bracket();
        when(bracketServiceRepository.findByRoundIdAndSeqId(roundId, seqId)).thenReturn(Optional.of(expectedBracket));

        Bracket result = bracketService.findBracketByRoundIdAndSeqId(roundId, seqId);

        assertEquals(expectedBracket, result);
        verify(bracketServiceRepository).findByRoundIdAndSeqId(roundId, seqId);
    }

    @Test
    void findBracketByRoundIdAndPlayer() {
        UUID roundId = UUID.randomUUID();
        String player = "player1";
        Bracket expectedBracket = new Bracket();
        when(bracketServiceRepository.findByRoundIdAndPlayer1OrPlayer2(roundId, player))
            .thenReturn(Optional.of(expectedBracket));

        Bracket result = bracketService.findBracketByRoundIdAndPlayer(roundId, player);

        assertEquals(expectedBracket, result);
        verify(bracketServiceRepository).findByRoundIdAndPlayer1OrPlayer2(roundId, player);
    }

    @Test
    void findBracketByRoundIdAndPlayer_NotFound() {
        UUID roundId = UUID.randomUUID();
        String player = "nonexistentPlayer";
        when(bracketServiceRepository.findByRoundIdAndPlayer1OrPlayer2(roundId, player))
            .thenReturn(Optional.empty());

        assertThrows(BracketNotFoundException.class,
            () -> bracketService.findBracketByRoundIdAndPlayer(roundId, player));
    }

    @Test
    void createBracket() {
        Bracket bracket = new Bracket();
        when(bracketServiceRepository.save(bracket)).thenReturn(bracket);

        Bracket result = bracketService.createBracket(bracket);

        assertEquals(bracket, result);
        verify(bracketServiceRepository).save(bracket);
    }
    @Test
    void endBracket_BothPlayersPresent() {
        UUID bracketId = UUID.randomUUID();
        Bracket bracket = new Bracket();
        bracket.setPlayer1("player1");
        bracket.setPlayer2("player2");
        bracket.setPlayer1Score(10);
        bracket.setPlayer2Score(5);
        Round round = new Round();
        bracket.setRound(round);
        Tournament tournament = new Tournament();
        bracket.setTournament(tournament);

        when(bracketServiceRepository.findById(bracketId)).thenReturn(Optional.of(bracket));
        when(bracketServiceRepository.save(any(Bracket.class))).thenReturn(bracket);

        Bracket result = bracketService.endBracket(bracketId);

        assertEquals("player1", result.getWinner());
        assertEquals(Status.COMPLETED, result.getStatus());
        verify(ratingUpdateService).updateRatings(bracket);
        verify(bracketServiceRepository).save(bracket);
    }

    @Test
    void endBracket_Player1Absent() {
        UUID bracketId = UUID.randomUUID();
        Bracket bracket = new Bracket();
        bracket.setPlayer2("player2");
        Round round = new Round();
        bracket.setRound(round);
        Tournament tournament = new Tournament();
        bracket.setTournament(tournament);

        when(bracketServiceRepository.findById(bracketId)).thenReturn(Optional.of(bracket));
        when(bracketServiceRepository.save(any(Bracket.class))).thenReturn(bracket);

        Bracket result = bracketService.endBracket(bracketId);

        assertEquals("player2", result.getWinner());
        assertEquals(Status.COMPLETED, result.getStatus());
        verify(bracketServiceRepository).save(bracket);
    }

    @Test
    void endBracket_Player2Absent() {
        UUID bracketId = UUID.randomUUID();
        Bracket bracket = new Bracket();
        bracket.setPlayer1("player1");
        Round round = new Round();
        bracket.setRound(round);
        Tournament tournament = new Tournament();
        bracket.setTournament(tournament);

        when(bracketServiceRepository.findById(bracketId)).thenReturn(Optional.of(bracket));
        when(bracketServiceRepository.save(any(Bracket.class))).thenReturn(bracket);

        Bracket result = bracketService.endBracket(bracketId);

        assertEquals("player1", result.getWinner());
        assertEquals(Status.COMPLETED, result.getStatus());
        verify(bracketServiceRepository).save(bracket);
    }

    @Test
    void endBracket_BracketNotFound() {
        UUID bracketId = UUID.randomUUID();
        when(bracketServiceRepository.findById(bracketId)).thenReturn(Optional.empty());

        assertThrows(BracketNotFoundException.class, () -> bracketService.endBracket(bracketId));
    }

    @Test
    void removePlayerFromBracket_Player1() {
        Bracket bracket = new Bracket();
        bracket.setPlayer1("player1");
        bracket.setPlayer1Score(10);
        bracket.setPlayer1WinProbability(60.0);
        bracket.setPlayer2WinProbability(40.0);

        when(bracketServiceRepository.save(any(Bracket.class))).thenReturn(bracket);

        Bracket result = bracketService.removePlayerFromBracket(bracket, "player1");

        assertNull(result.getPlayer1());
        assertEquals(0, result.getPlayer1Score());
        assertEquals(0.0, result.getPlayer1WinProbability());
        assertEquals(1.0, result.getPlayer2WinProbability());
        verify(bracketServiceRepository).save(bracket);
    }

    @Test
    void removePlayerFromBracket_Player2() {
        Bracket bracket = new Bracket();
        bracket.setPlayer1("player1");
        bracket.setPlayer2("player2");
        bracket.setPlayer2Score(10);
        bracket.setPlayer1WinProbability(40.0);
        bracket.setPlayer2WinProbability(60.0);

        when(bracketServiceRepository.save(any(Bracket.class))).thenReturn(bracket);

        bracketService.removePlayerFromBracket(bracket, "player2");

        assertNull(bracket.getPlayer2());
        assertEquals(0, bracket.getPlayer2Score());
        assertEquals(1.0, bracket.getPlayer1WinProbability());
        assertEquals(0.0, bracket.getPlayer2WinProbability());
        verify(bracketServiceRepository).save(bracket);
    }

    @Test
    void removePlayerFromBracket_PlayerNotFound() {
        Bracket bracket = new Bracket();
        bracket.setPlayer1("player1");
        bracket.setPlayer2("player2");

        assertThrows(UserNotFoundException.class,
            () -> bracketService.removePlayerFromBracket(bracket, "player3"));
    }

    @Test
    void findBracketByRoundIdAndSeqId_NotFound() {
        // Arrange
        UUID roundId = UUID.randomUUID();
        int seqId = 1;
        when(bracketServiceRepository.findByRoundIdAndSeqId(roundId, seqId))
                .thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(BracketNotFoundException.class,
                () -> bracketService.findBracketByRoundIdAndSeqId(roundId, seqId),
                "Should throw BracketNotFoundException when bracket not found");
    }

    @Test
    void findBracketById_NotFound() {
        // Arrange
        UUID bracketId = UUID.randomUUID();
        when(bracketServiceRepository.findById(bracketId))
                .thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(BracketNotFoundException.class,
                () -> bracketService.findBracketById(bracketId),
                "Should throw BracketNotFoundException when bracket not found");
    }

    @Test
    void endBracket_WithEqualScores_Player1Wins() {
        // Arrange
        UUID bracketId = UUID.randomUUID();
        Bracket bracket = new Bracket();
        bracket.setPlayer1("player1");
        bracket.setPlayer2("player2");
        bracket.setPlayer1Score(10);
        bracket.setPlayer2Score(10);
        Round round = new Round();
        bracket.setRound(round);
        Tournament tournament = new Tournament();
        bracket.setTournament(tournament);

        when(bracketServiceRepository.findById(bracketId)).thenReturn(Optional.of(bracket));
        when(bracketServiceRepository.save(any(Bracket.class))).thenReturn(bracket);

        // Act
        Bracket result = bracketService.endBracket(bracketId);

        // Assert
        assertEquals("player2", result.getWinner());
        assertEquals(Status.COMPLETED, result.getStatus());
        verify(ratingUpdateService).updateRatings(bracket);
        verify(bracketServiceRepository).save(bracket);
    }
}
