package com.cs203.smucode.controllers;

import com.cs203.smucode.configs.TestSecurityConfig;
import com.cs203.smucode.constants.Status;
import com.cs203.smucode.consumers.UserServiceConsumer;
import com.cs203.smucode.dtos.brackets.*;
import com.cs203.smucode.dtos.users.*;
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
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.UUID;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.ActiveProfiles;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
@Import(TestSecurityConfig.class)
class BracketRestControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private BracketService bracketService;

    @MockBean
    private BracketMapper bracketMapper;

    @MockBean
    private UserServiceConsumer userServiceConsumer;

    @Value("${feign.access.token}")
    private String testJWT;

    private TestData testData;

    @BeforeEach
    void setUp() {
        testData = new TestData();
        setupMocks();
    }

    private void setupMocks() {
        when(bracketService.findBracketById(testData.bracketId))
                .thenReturn(testData.bracket);
        when(bracketMapper.bracketToBracketDTO(any(Bracket.class), eq(userServiceConsumer)))
                .thenReturn(testData.bracketDTO);
        when(bracketMapper.updateBracketScoreDTOToBracket(any()))
                .thenReturn(testData.bracket);
    }

    @Nested
    @DisplayName("Bracket Retrieval Operations")
    class BracketRetrievalOperations {

        @Test
        @DisplayName("Should get bracket successfully")
        void getBracket_ValidId_Success() throws Exception {
            mockMvc.perform(get("/tournaments/brackets/{bracketId}", testData.bracketId)
                            .header("Authorization", "Bearer " + testJWT))
                    .andDo(print())
                    .andExpect(status().isOk())
                    .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                    .andExpect(jsonPath("$.id").value(testData.bracketId.toString()))
                    .andExpect(jsonPath("$.player1.username").value(testData.player1Username))
                    .andExpect(jsonPath("$.player2.username").value(testData.player2Username));

            verify(bracketService).findBracketById(testData.bracketId);
        }

        @Test
        @DisplayName("Should handle non-existent bracket")
        void getBracket_InvalidId_ReturnsNotFound() throws Exception {
            when(bracketService.findBracketById(any()))
                    .thenThrow(new BracketNotFoundException("Bracket not found"));

            mockMvc.perform(get("/tournaments/brackets/{bracketId}", UUID.randomUUID())
                            .header("Authorization", "Bearer " + testJWT))
                    .andDo(print())
                    .andExpect(status().isNotFound())
                    .andExpect(jsonPath("$.message").value("Bracket not found"));
        }
    }

    @Nested
    @DisplayName("Bracket Score Update Operations")
    class BracketScoreUpdateOperations {

        @Test
        @DisplayName("Should update bracket score successfully")
        void updateBracketScore_ValidData_Success() throws Exception {
            when(bracketService.updateBracketScore(eq(testData.bracketId), any(Bracket.class)))
                    .thenReturn(testData.bracket);

            when(bracketMapper.bracketToBracketDTO(any(Bracket.class), eq(userServiceConsumer)))
                    .thenReturn(testData.bracketDTO);

            mockMvc.perform(put("/tournaments/brackets/{bracketId}", testData.bracketId)
                            .header("Authorization", "Bearer " + testJWT)
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(testData.updateScoreDTO)))
                    .andDo(print())
                    .andExpect(status().isOk())
                    .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                    .andExpect(jsonPath("$.id").value(testData.bracketId.toString()))
                    .andExpect(jsonPath("$.seqId").value(1))
                    .andExpect(jsonPath("$.status").value("ongoing"))
                    .andExpect(jsonPath("$.player1.username").value(testData.player1Username))
                    .andExpect(jsonPath("$.player1.score").value(testData.player1Score))
                    .andExpect(jsonPath("$.player2.username").value(testData.player2Username))
                    .andExpect(jsonPath("$.player2.score").value(testData.player2Score));

            // Verify the correct service method was called
            verify(bracketService).updateBracketScore(eq(testData.bracketId), any(Bracket.class));
            verify(bracketMapper).bracketToBracketDTO(any(Bracket.class), eq(userServiceConsumer));
        }
    }

    @Nested
    @DisplayName("Bracket Completion Operations")
    class BracketCompletionOperations {

        @Test
        @DisplayName("Should end bracket successfully")
        void endBracket_ValidId_Success() throws Exception {
            when(bracketService.endBracket(testData.bracketId))
                    .thenReturn(testData.completedBracket);
            when(bracketMapper.bracketToBracketDTO(testData.completedBracket, userServiceConsumer))
                    .thenReturn(testData.completedBracketDTO);

            mockMvc.perform(put("/tournaments/brackets/{bracketId}/end", testData.bracketId)
                            .header("Authorization", "Bearer " + testJWT))
                    .andDo(print())
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.status").value("completed"))
                    .andExpect(jsonPath("$.winner").value(testData.player1Username));

            verify(bracketService).endBracket(testData.bracketId);
        }

        @Test
        @DisplayName("Should handle non-existent bracket when ending")
        void endBracket_InvalidId_ReturnsNotFound() throws Exception {
            when(bracketService.endBracket(any()))
                    .thenThrow(new BracketNotFoundException("Bracket not found"));

            mockMvc.perform(put("/tournaments/brackets/{bracketId}/end", UUID.randomUUID())
                            .header("Authorization", "Bearer " + testJWT))
                    .andDo(print())
                    .andExpect(status().isNotFound())
                    .andExpect(jsonPath("$.message").value("Bracket not found"));
        }
    }

    @Nested
    @DisplayName("Error Handling")
    class ErrorHandling {

        @Test
        @DisplayName("Should handle unauthorized access")
        void anyEndpoint_NoToken_ReturnsUnauthorized() throws Exception {
            mockMvc.perform(get("/tournaments/brackets/{bracketId}", testData.bracketId))
                    .andDo(print())
                    .andExpect(status().isUnauthorized());
        }

        @Test
        @DisplayName("Should handle missing content type")
        void updateBracketScore_MissingContentType_ReturnsUnsupportedMediaType() throws Exception {
            mockMvc.perform(put("/tournaments/brackets/{bracketId}", testData.bracketId)
                            .header("Authorization", "Bearer " + testJWT)
                            .content(objectMapper.writeValueAsString(testData.updateScoreDTO)))
                    .andDo(print())
                    .andExpect(status().isUnsupportedMediaType());
        }

        @Test
        @DisplayName("Should handle invalid token format")
        void anyEndpoint_InvalidTokenFormat_ReturnsUnauthorized() throws Exception {
            mockMvc.perform(get("/tournaments/brackets/{bracketId}", testData.bracketId)
                            .header("Authorization", "Invalid-Format"))
                    .andDo(print())
                    .andExpect(status().isUnauthorized());
        }
    }

    private static class TestData {
        final UUID bracketId = UUID.randomUUID();
        final String player1Username = "player1";
        final String player2Username = "player2";
        final int player1Score = 10;
        final int player2Score = 5;

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
            this.completedBracket.setWinner(player1Username);
            this.completedBracketDTO = createBracketDTO(Status.COMPLETED, player1Username);

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
            Bracket newBracket = new Bracket();
            newBracket.setId(bracketId);
            newBracket.setSeqId(1);
            newBracket.setStatus(status);
            newBracket.setPlayer1(player1Username);
            newBracket.setPlayer2(player2Username);
            newBracket.setPlayer1Score(player1Score);
            newBracket.setPlayer2Score(player2Score);
            newBracket.setRound(round);
            newBracket.setTournament(tournament);
            return newBracket;
        }

        private BracketDTO createBracketDTO(Status status, String winner) {
            BracketDTO dto = new BracketDTO();
            dto.setId(bracketId.toString());
            dto.setSeqId(1);
            dto.setStatus(status.toString().toLowerCase());
            dto.setWinner(winner);

            BracketUserDTO player1 = new BracketUserDTO();
            player1.setUsername(player1Username);
            player1.setScore(player1Score);
            player1.setImage("/default-image.jpg");  // Add default image
            player1.setWinProbability(0.5);  // Add default win probability

            BracketUserDTO player2 = new BracketUserDTO();
            player2.setUsername(player2Username);
            player2.setScore(player2Score);
            player2.setImage("/default-image.jpg");  // Add default image
            player2.setWinProbability(0.5);  // Add default win probability

            dto.setPlayer1(player1);
            dto.setPlayer2(player2);
            return dto;
        }

        private UpdateBracketScoreDTO createUpdateScoreDTO() {
            UpdateBracketScoreDTO dto = new UpdateBracketScoreDTO();

            PlayerInfo player1Info = new PlayerInfo();
            player1Info.setId(player1Username);
            player1Info.setScore(player1Score);

            PlayerInfo player2Info = new PlayerInfo();
            player2Info.setId(player2Username);
            player2Info.setScore(player2Score);

            dto.setPlayer1(player1Info);
            dto.setPlayer2(player2Info);
            return dto;
        }
    }
}