package com.cs203.smucode.controllers;

import com.cs203.smucode.constants.Band;
import com.cs203.smucode.constants.Status;
import com.cs203.smucode.dtos.rounds.RoundDTO;
import com.cs203.smucode.models.Round;
import com.cs203.smucode.models.Tournament;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.UUID;

import static org.hamcrest.Matchers.containsString;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import com.cs203.smucode.configs.TestSecurityConfig;
import com.cs203.smucode.repositories.RoundServiceRepository;
import com.cs203.smucode.repositories.TournamentServiceRepository;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.ActiveProfiles;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
@Import(TestSecurityConfig.class)
class RoundRestControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private RoundServiceRepository roundRepository;

    @Autowired
    private TournamentServiceRepository tournamentRepository;

    @Value("${feign.access.token}")
    private String testJWT;

    private TestData testData;

    @BeforeEach
    void setUp() {
        roundRepository.deleteAll();
        tournamentRepository.deleteAll();
        testData = new TestData();
        setupTestData();
    }

    private void setupTestData() {
        // Save tournament first
        testData.tournament = tournamentRepository.save(testData.tournament);
        // Then save round
        testData.round = roundRepository.save(testData.round);
    }

    @AfterEach
    void cleanup() {
        roundRepository.deleteAll();
        tournamentRepository.deleteAll();
    }

    @Nested
    @DisplayName("Round Retrieval Operations")
    class RoundRetrievalOperations {

        @Test
        @DisplayName("Should get round successfully")
        void getRound_ValidId_Success() throws Exception {
            mockMvc.perform(get("/tournaments/rounds/{roundId}", testData.round.getId())
                            .header("Authorization", "Bearer " + testJWT))
                    .andDo(print())
                    .andExpect(status().isOk())
                    .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                    .andExpect(jsonPath("$.id").value(testData.round.getId().toString()))
                    .andExpect(jsonPath("$.name").value(testData.round.getName()))
                    .andExpect(jsonPath("$.status").value(testData.round.getStatus().toString().toUpperCase())) // Add toLowerCase()
                    .andExpect(jsonPath("$.seqId").value(testData.round.getSeqId()));

            Round retrievedRound = roundRepository.findById(testData.round.getId()).orElseThrow();
            assertThat(retrievedRound).isNotNull();
            assertThat(retrievedRound.getName()).isEqualTo(testData.roundName);
        }

        @Test
        @DisplayName("Should handle non-existent round")
        void getRound_InvalidId_ReturnsNotFound() throws Exception {
            UUID nonExistentId = UUID.randomUUID();
            mockMvc.perform(get("/tournaments/rounds/{roundId}", nonExistentId)
                            .header("Authorization", "Bearer " + testJWT))
                    .andDo(print())
                    .andExpect(status().isNotFound())
                    .andExpect(jsonPath("$.message").value(containsString("not found")));
        }
    }

    @Nested
    @DisplayName("Round Update Operations")
    class RoundUpdateOperations {

        @Test
        @DisplayName("Should update round successfully")
        void updateRound_ValidData_Success() throws Exception {
            RoundDTO updateDTO = testData.createUpdateRoundDTO();

            mockMvc.perform(put("/tournaments/rounds/{roundId}", testData.round.getId())
                            .header("Authorization", "Bearer " + testJWT)
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(updateDTO)))
                    .andDo(print())
                    .andExpect(status().isOk())
                    .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                    .andExpect(jsonPath("$.name").value(testData.updatedRoundName))
                    .andExpect(jsonPath("$.status").value("COMPLETED"));  // Changed to match actual response

            Round updatedRound = roundRepository.findById(testData.round.getId()).orElseThrow();
            assertThat(updatedRound.getName()).isEqualTo(testData.updatedRoundName);
            assertThat(updatedRound.getStatus()).isEqualTo(Status.COMPLETED);
        }

        @Test
        @DisplayName("Should handle non-existent round")
        void getRound_InvalidId_ReturnsNotFound() throws Exception {
            UUID nonExistentId = UUID.randomUUID();

            mockMvc.perform(get("/tournaments/rounds/{roundId}", nonExistentId)
                            .header("Authorization", "Bearer " + testJWT))
                    .andDo(print())
                    .andExpect(status().isNotFound())
                    .andExpect(jsonPath("$.message").value(containsString("not found")));
        }
    }

    @Nested
    @DisplayName("Error Handling")
    class ErrorHandling {

        @Test
        @DisplayName("Should handle unauthorized access")
        void anyEndpoint_NoToken_ReturnsUnauthorized() throws Exception {
            mockMvc.perform(get("/tournaments/rounds/{roundId}", testData.round.getId()))
                    .andDo(print())
                    .andExpect(status().isUnauthorized());
        }

        @Test
        @DisplayName("Should handle missing content type")
        void updateRound_MissingContentType_ReturnsUnsupportedMediaType() throws Exception {
            RoundDTO updateDTO = testData.createUpdateRoundDTO();

            mockMvc.perform(put("/tournaments/rounds/{roundId}", testData.round.getId())
                            .header("Authorization", "Bearer " + testJWT)
                            .content(objectMapper.writeValueAsString(updateDTO)))
                    .andDo(print())
                    .andExpect(status().isUnsupportedMediaType());
        }

        @Test
        @DisplayName("Should handle invalid token format")
        void anyEndpoint_InvalidTokenFormat_ReturnsUnauthorized() throws Exception {
            mockMvc.perform(get("/tournaments/rounds/{roundId}", testData.round.getId())
                            .header("Authorization", "Invalid-Format"))
                    .andDo(print())
                    .andExpect(status().isUnauthorized());
        }
    }

    private static class TestData {
        final String roundName = "Round of 16";
        final String updatedRoundName = "Updated Round";
        final LocalDateTime startDate = LocalDateTime.now();
        final LocalDateTime endDate = LocalDateTime.now().plusDays(1);

        Tournament tournament;
        Round round;

        TestData() {
            this.tournament = createTournament();
            this.round = createRound(tournament, Status.ONGOING, roundName);
        }

        private Tournament createTournament() {
            Tournament tournament = new Tournament();
            tournament.setName("Test Tournament");
            tournament.setDescription("Test tournament for integration testing");
            tournament.setStartDate(LocalDateTime.now().plusDays(7)); // 7 days from now
            tournament.setEndDate(LocalDateTime.now().plusDays(14));  // 14 days from now
            tournament.setFormat("single-elimination");
            tournament.setCapacity(16); // Common tournament size
            tournament.setIcon("/default-tournament-icon.jpg");
            tournament.setOrganiser("test-admin");

            // Weights should sum to 100
            tournament.setTimeWeight(40);
            tournament.setMemWeight(30);
            tournament.setTestCaseWeight(30);

            tournament.setStatus(Status.ONGOING);
            tournament.setSignupStartDate(LocalDateTime.now());
            tournament.setSignupEndDate(LocalDateTime.now().plusDays(5));
            tournament.setBand(Band.MIDDLE);
            tournament.setCurrentRound("Round of 16");

            // Initialize collections
            tournament.setRounds(new ArrayList<>());
            tournament.setBrackets(new ArrayList<>());
            tournament.setSignups(new HashSet<>());
            tournament.setParticipants(new HashSet<>());

            return tournament;
        }

        private Round createRound(Tournament tournament, Status status, String name) {
            Round newRound = new Round();
            newRound.setSeqId(1);
            newRound.setName(name);
            newRound.setStatus(status);
            newRound.setStartDate(startDate);
            newRound.setEndDate(endDate);
            newRound.setTournament(tournament);
            newRound.setBrackets(new ArrayList<>());
            return newRound;
        }

        private RoundDTO createUpdateRoundDTO() {
            RoundDTO dto = new RoundDTO();
            dto.setId(round.getId().toString());
            dto.setSeqId(1);
            dto.setName(updatedRoundName);
            dto.setStatus("COMPLETED");  // Changed to uppercase
            dto.setStartDate(startDate);
            dto.setEndDate(endDate);
            dto.setBrackets(new ArrayList<>());
            return dto;
        }
    }
}