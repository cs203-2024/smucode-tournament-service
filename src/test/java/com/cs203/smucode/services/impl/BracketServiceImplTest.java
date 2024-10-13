package com.cs203.smucode.services.impl;

import com.cs203.smucode.constants.Status;
import com.cs203.smucode.exceptions.BracketNotFoundException;
import com.cs203.smucode.models.Bracket;
import com.cs203.smucode.models.PlayerInfo;
import com.cs203.smucode.models.Round;
import com.cs203.smucode.models.Tournament;
import com.cs203.smucode.repositories.BracketServiceRepository;
import com.cs203.smucode.repositories.RoundServiceRepository;
import com.cs203.smucode.repositories.TournamentServiceRepository;
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
        when(bracketServiceRepository.findByRoundId(roundId)).thenReturn(expectedBrackets);

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
        when(bracketServiceRepository.findByRoundIdAndSeqId(roundId, seqId)).thenReturn(expectedBracket);

        Bracket result = bracketService.findBracketByRoundIdAndSeqId(roundId, seqId);

        assertEquals(expectedBracket, result);
        verify(bracketServiceRepository).findByRoundIdAndSeqId(roundId, seqId);
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
    void updateBracket() {
        UUID bracketId = UUID.randomUUID();
        Bracket existingBracket = new Bracket();
        existingBracket.setId(bracketId);
        Round parentRound = new Round();
        parentRound.setStatus(Status.UPCOMING);
        parentRound.setName("Round 1");
        Tournament tournament = new Tournament();
        parentRound.setTournament(tournament);
        existingBracket.setRound(parentRound);

        Bracket updatedBracket = new Bracket();
        updatedBracket.setId(bracketId);
        updatedBracket.setStatus(Status.ONGOING);
        updatedBracket.setWinner("Player1");
//        updatedBracket.setPlayers(Arrays.asList(
//                new PlayerInfo("player1", 10),
//                new PlayerInfo("player2", 5)
//        ));
        updatedBracket.setPlayer1("player1");
        updatedBracket.setPlayer1Score(10);
        updatedBracket.setPlayer2("player2");
        updatedBracket.setPlayer2Score(5);

        when(bracketServiceRepository.findById(bracketId)).thenReturn(Optional.of(existingBracket));
        when(bracketServiceRepository.save(any(Bracket.class))).thenReturn(updatedBracket);

        Bracket result = bracketService.updateBracket(bracketId, updatedBracket);

        assertEquals(updatedBracket.getStatus(), result.getStatus());
        assertEquals(updatedBracket.getWinner(), result.getWinner());
//        assertEquals(updatedBracket.getPlayers(), result.getPlayers());
        assertEquals(updatedBracket.getPlayer1(), result.getPlayer1());
        assertEquals(updatedBracket.getPlayer2(), result.getPlayer2());
        assertEquals(updatedBracket.getPlayer1Score(), result.getPlayer1Score());
        assertEquals(updatedBracket.getPlayer2Score(), result.getPlayer2Score());
        assertEquals(Status.ONGOING, parentRound.getStatus());
        assertEquals("Round 1", tournament.getCurrentRound());
        verify(roundServiceRepository).save(parentRound);
        verify(tournamentServiceRepository).save(tournament);
        verify(bracketServiceRepository).save(any(Bracket.class));
    }

    @Test
    void updateBracket_BracketNotFound() {
        UUID bracketId = UUID.randomUUID();
        Bracket updatedBracket = new Bracket();
        when(bracketServiceRepository.findById(bracketId)).thenReturn(Optional.empty());

        assertThrows(BracketNotFoundException.class, () -> bracketService.updateBracket(bracketId, updatedBracket));
    }

    @Test
    void deleteBracketById() {
        UUID bracketId = UUID.randomUUID();

        bracketService.deleteBracketById(bracketId);

        verify(bracketServiceRepository).deleteById(bracketId);
    }
}