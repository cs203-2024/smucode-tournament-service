package com.cs203.smucode.controllers;

import com.cs203.smucode.constants.Status;
import com.cs203.smucode.dto.BracketDTO;
import com.cs203.smucode.dto.BracketUserDTO;
import com.cs203.smucode.dto.UpdateBracketScoreDTO;
import com.cs203.smucode.exceptions.BracketNotFoundException;
import com.cs203.smucode.mappers.BracketMapper;
import com.cs203.smucode.models.Bracket;
import com.cs203.smucode.models.PlayerInfo;
import com.cs203.smucode.models.Round;
import com.cs203.smucode.models.Tournament;
import com.cs203.smucode.services.BracketService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import java.util.UUID;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(BracketRestController.class)
@DisplayName("BracketController Integration Tests")
class BracketRestControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private BracketService bracketService;

    @MockBean
    private BracketMapper bracketMapper;

    private TestData testData;

    @BeforeEach
    void setUp() {
        testData = new TestData();
        setupMocks();
    }

    private void setupMocks() {
        when(bracketService.findBracketById(testData.bracketId))
                .thenReturn(testData.bracket);
        when(bracketMapper.bracketToBracketDTO(any(Bracket.class)))
                .thenReturn(testData.bracketDTO);
        when(bracketMapper.updateBracketScoreDTOToBracket(any()))
                .thenReturn(testData.bracket);
    }

    @Nested
    @DisplayName("GET /tournaments/brackets/{bracketId}")
    class GetBracketTests {

        @Test
        @WithMockUser
        @DisplayName("Should successfully retrieve bracket")
        void getBracket_Success() throws Exception {
            mockMvc.perform(get("/tournaments/brackets/{bracketId}", testData.bracketId))
                    .andExpect(status().isOk())
                    .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                    .andExpect(jsonPath("$.id").value(testData.bracketId.toString()))
                    .andExpect(jsonPath("$.player1.username").value(testData.PLAYER_1_USERNAME))
                    .andExpect(jsonPath("$.player2.username").value(testData.PLAYER_2_USERNAME));

            verify(bracketService).findBracketById(testData.bracketId);
        }

        @Test
        @WithMockUser
        @DisplayName("Should return 404 when bracket not found")
        void getBracket_NotFound() throws Exception {
            when(bracketService.findBracketById(testData.bracketId))
                    .thenThrow(new BracketNotFoundException("Bracket not found"));

            mockMvc.perform(get("/tournaments/brackets/{bracketId}", testData.bracketId))
                    .andExpect(status().isNotFound())
                    .andExpect(jsonPath("$.message").value("Bracket not found"));
        }
    }

    @Nested
    @DisplayName("PUT /tournaments/brackets/{bracketId}")
    class UpdateBracketScoreTests {

        @Test
        @WithMockUser
        @DisplayName("Should successfully update bracket score")
        void updateBracketScore_Success() throws Exception {
            when(bracketService.updateBracketScore(eq(testData.bracketId), any(Bracket.class)))
                    .thenReturn(testData.bracket);

            mockMvc.perform(put("/tournaments/brackets/{bracketId}", testData.bracketId)
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(testData.updateScoreDTO))
                            .with(csrf()))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.player1.score").value(testData.PLAYER_1_SCORE))
                    .andExpect(jsonPath("$.player2.score").value(testData.PLAYER_2_SCORE));

            verify(bracketService).updateBracketScore(eq(testData.bracketId), any(Bracket.class));
        }
    }

    @Nested
    @DisplayName("PUT /tournaments/brackets/{bracketId}/end")
    class EndBracketTests {
        @Test
        @WithMockUser
        @DisplayName("Should successfully end bracket")
        void endBracket_Success() throws Exception {
            when(bracketService.endBracket(testData.bracketId))
                    .thenReturn(testData.completedBracket);
            when(bracketMapper.bracketToBracketDTO(testData.completedBracket))
                    .thenReturn(testData.completedBracketDTO);

            mockMvc.perform(put("/tournaments/brackets/{bracketId}/end", testData.bracketId)
                            .with(csrf()))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.status").value("completed"))
                    .andExpect(jsonPath("$.winner").value(testData.PLAYER_1_USERNAME));

            verify(bracketService).endBracket(testData.bracketId);
        }

        @Test
        @WithMockUser
        @DisplayName("Should return 404 when bracket not found")
        void endBracket_NotFound() throws Exception {
            when(bracketService.endBracket(testData.bracketId))
                    .thenThrow(new BracketNotFoundException("Bracket not found"));

            mockMvc.perform(put("/tournaments/brackets/{bracketId}/end", testData.bracketId)
                            .with(csrf()))  // Add CSRF token
                    .andExpect(status().isNotFound())
                    .andExpect(jsonPath("$.message").value("Bracket not found"));
        }
    }

    @Test
    @DisplayName("Should return 401 when user is not authenticated")
    void whenNotAuthenticated_Unauthorized() throws Exception {
        mockMvc.perform(get("/tournaments/brackets/{bracketId}", testData.bracketId))
                .andExpect(status().isUnauthorized());
    }

    private static class TestData {
        final UUID bracketId = UUID.randomUUID();
        final String PLAYER_1_USERNAME = "player1";
        final String PLAYER_2_USERNAME = "player2";
        final int PLAYER_1_SCORE = 10;
        final int PLAYER_2_SCORE = 5;

        final Bracket bracket;
        final BracketDTO bracketDTO;
        final UpdateBracketScoreDTO updateScoreDTO;
        final Bracket completedBracket;
        final BracketDTO completedBracketDTO;

        TestData() {
            Tournament tournament = createTournament();
            Round round = createRound(tournament);

            // Create regular bracket and DTO
            this.bracket = createBracket(round, tournament, Status.ONGOING);
            this.bracketDTO = createBracketDTO(Status.ONGOING, null);

            // Create completed bracket and DTO
            this.completedBracket = createBracket(round, tournament, Status.COMPLETED);
            this.completedBracket.setWinner(PLAYER_1_USERNAME);
            this.completedBracketDTO = createBracketDTO(Status.COMPLETED, PLAYER_1_USERNAME);

            // Create update score DTO
            this.updateScoreDTO = createUpdateScoreDTO();
        }

        private Tournament createTournament() {
            Tournament tournament = new Tournament();
            tournament.setId(UUID.randomUUID());
            tournament.setStatus(Status.ONGOING);
            return tournament;
        }

        private Round createRound(Tournament tournament) {
            Round round = new Round();
            round.setId(UUID.randomUUID());
            round.setStatus(Status.ONGOING);
            round.setTournament(tournament);
            return round;
        }

        private Bracket createBracket(Round round, Tournament tournament, Status status) {
            Bracket bracket = new Bracket();
            bracket.setId(bracketId);
            bracket.setSeqId(1);
            bracket.setStatus(status);
            bracket.setPlayer1(PLAYER_1_USERNAME);
            bracket.setPlayer2(PLAYER_2_USERNAME);
            bracket.setPlayer1Score(PLAYER_1_SCORE);
            bracket.setPlayer2Score(PLAYER_2_SCORE);
            bracket.setRound(round);
            bracket.setTournament(tournament);
            return bracket;
        }

        private BracketDTO createBracketDTO(Status status, String winner) {
            BracketDTO dto = new BracketDTO();
            dto.setId(bracketId.toString());
            dto.setSeqId(1);
            dto.setStatus(status.toString().toLowerCase());
            dto.setWinner(winner);

            BracketUserDTO player1 = new BracketUserDTO();
            player1.setUsername(PLAYER_1_USERNAME);
            player1.setScore(PLAYER_1_SCORE);

            BracketUserDTO player2 = new BracketUserDTO();
            player2.setUsername(PLAYER_2_USERNAME);
            player2.setScore(PLAYER_2_SCORE);

            dto.setPlayer1(player1);
            dto.setPlayer2(player2);
            return dto;
        }

        private UpdateBracketScoreDTO createUpdateScoreDTO() {
            UpdateBracketScoreDTO dto = new UpdateBracketScoreDTO();

            PlayerInfo player1Info = new PlayerInfo();
            player1Info.setId(PLAYER_1_USERNAME);
            player1Info.setScore(PLAYER_1_SCORE);

            PlayerInfo player2Info = new PlayerInfo();
            player2Info.setId(PLAYER_2_USERNAME);
            player2Info.setScore(PLAYER_2_SCORE);

            dto.setPlayer1(player1Info);
            dto.setPlayer2(player2Info);
            return dto;
        }
    }
}