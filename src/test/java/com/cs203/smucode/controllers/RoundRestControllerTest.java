package com.cs203.smucode.controllers;

import com.cs203.smucode.constants.Status;
import com.cs203.smucode.dto.RoundDTO;
import com.cs203.smucode.exceptions.RoundNotFoundException;
import com.cs203.smucode.mappers.RoundMapper;
import com.cs203.smucode.models.Round;
import com.cs203.smucode.models.Tournament;
import com.cs203.smucode.services.RoundService;
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

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.UUID;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(RoundRestController.class)
@DisplayName("RoundController Integration Tests")
class RoundRestControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private RoundService roundService;

    @MockBean
    private RoundMapper roundMapper;

    private TestData testData;

    @BeforeEach
    void setUp() {
        testData = new TestData();
        setupMocks();
    }

    private void setupMocks() {
        when(roundService.findRoundById(testData.roundId))
                .thenReturn(testData.round);
        when(roundMapper.roundToRoundDTO(any(Round.class)))
                .thenReturn(testData.roundDTO);
        when(roundMapper.roundDTOToRound(any(RoundDTO.class)))
                .thenReturn(testData.round);
    }

    @Nested
    @DisplayName("GET /tournaments/rounds/{roundId}")
    class GetRoundTests {

        @Test
        @WithMockUser
        @DisplayName("Should successfully retrieve round")
        void getRound_Success() throws Exception {
            mockMvc.perform(get("/tournaments/rounds/{roundId}", testData.roundId))
                    .andExpect(status().isOk())
                    .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                    .andExpect(jsonPath("$.id").value(testData.roundId.toString()))
                    .andExpect(jsonPath("$.name").value(testData.ROUND_NAME))
                    .andExpect(jsonPath("$.status").value(testData.round.getStatus().toString().toLowerCase()));

            verify(roundService).findRoundById(testData.roundId);
            verify(roundMapper).roundToRoundDTO(testData.round);
        }

        @Test
        @WithMockUser
        @DisplayName("Should return 404 when round not found")
        void getRound_NotFound() throws Exception {
            when(roundService.findRoundById(testData.roundId))
                    .thenThrow(new RoundNotFoundException("Round not found"));

            mockMvc.perform(get("/tournaments/rounds/{roundId}", testData.roundId))
                    .andExpect(status().isNotFound())
                    .andExpect(jsonPath("$.message").value("Round not found"));
        }

        @Test
        @DisplayName("Should return 401 when not authenticated")
        void getRound_Unauthorized() throws Exception {
            mockMvc.perform(get("/tournaments/rounds/{roundId}", testData.roundId))
                    .andExpect(status().isUnauthorized());
        }
    }

    @Nested
    @DisplayName("PUT /tournaments/rounds/{roundId}")
    class UpdateRoundTests {

        @Test
        @WithMockUser
        @DisplayName("Should successfully update round")
        void updateRound_Success() throws Exception {
            when(roundService.updateRound(eq(testData.roundId), any(Round.class)))
                    .thenReturn(testData.updatedRound);
            when(roundMapper.roundToRoundDTO(testData.updatedRound))
                    .thenReturn(testData.updatedRoundDTO);

            mockMvc.perform(put("/tournaments/rounds/{roundId}", testData.roundId)
                            .with(csrf())
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(testData.updateRoundDTO)))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.name").value(testData.UPDATED_ROUND_NAME))
                    .andExpect(jsonPath("$.status").value(Status.COMPLETED.toString().toLowerCase()));

            verify(roundService).updateRound(eq(testData.roundId), any(Round.class));
            verify(roundMapper).roundDTOToRound(any(RoundDTO.class));
        }

        @Test
        @WithMockUser
        @DisplayName("Should return 404 when round not found")
        void updateRound_NotFound() throws Exception {
            when(roundService.updateRound(eq(testData.roundId), any(Round.class)))
                    .thenThrow(new RoundNotFoundException("Round not found"));

            mockMvc.perform(put("/tournaments/rounds/{roundId}", testData.roundId)
                            .with(csrf())
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(testData.updateRoundDTO)))
                    .andExpect(status().isNotFound())
                    .andExpect(jsonPath("$.message").value("Round not found"));
        }

        @Test
        @WithMockUser
        @DisplayName("Should return 403 when CSRF token is missing")
        void updateRound_MissingCsrf() throws Exception {
            mockMvc.perform(put("/tournaments/rounds/{roundId}", testData.roundId)
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(testData.updateRoundDTO)))
                    .andExpect(status().isForbidden());
        }
    }

    private static class TestData {
        final UUID roundId = UUID.randomUUID();
        final String ROUND_NAME = "Round of 16";
        final String UPDATED_ROUND_NAME = "Updated Round";
        final LocalDateTime startDate = LocalDateTime.now();
        final LocalDateTime endDate = LocalDateTime.now().plusDays(1);

        final Round round;
        final RoundDTO roundDTO;
        final Round updatedRound;
        final RoundDTO updatedRoundDTO;
        final RoundDTO updateRoundDTO;

        TestData() {
            Tournament tournament = createTournament();

            // Create regular round and DTO
            this.round = createRound(tournament, Status.ONGOING, ROUND_NAME);
            this.roundDTO = createRoundDTO(Status.ONGOING, ROUND_NAME);

            // Create updated round and DTO
            this.updatedRound = createRound(tournament, Status.COMPLETED, UPDATED_ROUND_NAME);
            this.updatedRoundDTO = createRoundDTO(Status.COMPLETED, UPDATED_ROUND_NAME);

            // Create update DTO
            this.updateRoundDTO = createRoundDTO(Status.COMPLETED, UPDATED_ROUND_NAME);
        }

        private Tournament createTournament() {
            Tournament tournament = new Tournament();
            tournament.setId(UUID.randomUUID());
            tournament.setStatus(Status.ONGOING);
            return tournament;
        }

        private Round createRound(Tournament tournament, Status status, String name) {
            Round round = new Round();
            round.setId(roundId);
            round.setSeqId(1);
            round.setName(name);
            round.setStatus(status);
            round.setStartDate(startDate);
            round.setEndDate(endDate);
            round.setTournament(tournament);
            round.setBrackets(new ArrayList<>());
            return round;
        }

        private RoundDTO createRoundDTO(Status status, String name) {
            RoundDTO dto = new RoundDTO();
            dto.setId(roundId.toString());
            dto.setSeqId(1);
            dto.setName(name);
            dto.setStatus(status.toString().toLowerCase());
            dto.setStartDate(startDate);
            dto.setEndDate(endDate);
            dto.setBrackets(new ArrayList<>());
            return dto;
        }
    }
}