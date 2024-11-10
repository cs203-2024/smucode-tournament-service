package com.cs203.smucode.controllers;

import com.cs203.smucode.constants.Status;
import com.cs203.smucode.consumers.UserServiceConsumer;
import com.cs203.smucode.dtos.users.*;
import com.cs203.smucode.dtos.tournaments.*;
import com.cs203.smucode.mappers.TournamentMapper;
import com.cs203.smucode.models.Tournament;
import com.cs203.smucode.services.TournamentService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.jwt;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(TournamentRestController.class)
@DisplayName("TournamentController Integration Tests")
class TournamentRestControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private TournamentService tournamentService;

    @MockBean
    private TournamentMapper tournamentMapper;

    @MockBean
    private UserServiceConsumer userServiceConsumer;

    private TestData testData;

    @BeforeEach
    void setUp() {
        testData = new TestData();
        setupMocks();
    }

    private void setupMocks() {
        when(tournamentService.findTournamentById(testData.tournamentId))
                .thenReturn(testData.tournament);
        when(tournamentMapper.tournamentToAdminTournamentDTO(any(Tournament.class)))
                .thenReturn(testData.adminTournamentDTO);
        when(tournamentMapper.tournamentToUserTournamentDTO(any(Tournament.class), anyString()))
                .thenReturn(testData.userTournamentDTO);
    }

    @Nested
    @DisplayName("GET /tournaments")
    class GetAllTournamentsTests {

        @Test
        @DisplayName("Should return admin's tournaments when user is admin")
        void getAllTournaments_AsAdmin() throws Exception {
            when(tournamentService.findAllTournamentsByOrganiser(anyString()))
                    .thenReturn(List.of(testData.tournament));
            when(tournamentMapper.tournamentsToAdminTournamentCardDTOs(anyList()))
                    .thenReturn(List.of(testData.adminTournamentCardDTO));

            mockMvc.perform(get("/tournaments")
                            .with(jwt().jwt(jwt -> jwt.claim("sub", "admin").claim("scope", "ROLE_ADMIN"))))
                    .andExpect(status().isOk())
                    .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                    .andExpect(jsonPath("$[0].id").value(testData.tournamentId.toString()))
                    .andExpect(jsonPath("$[0].name").value(testData.tournamentName))
                    .andExpect(jsonPath("$[0].band").exists());

            verify(tournamentService).findAllTournamentsByOrganiser(anyString());
        }

        @Test
        @DisplayName("Should return user's tournaments when user is regular user")
        void getAllTournaments_AsUser() throws Exception {
            when(tournamentService.findAllTournamentsByRegistrant(anyString()))
                    .thenReturn(List.of(testData.tournament));
            when(tournamentMapper.tournamentsToUserTournamentCardDTOs(anyList(), anyString()))
                    .thenReturn(List.of(testData.userTournamentCardDTO));

            mockMvc.perform(get("/tournaments")
                            .with(jwt().jwt(jwt -> jwt.claim("sub", "user").claim("scope", "ROLE_USER"))))
                    .andExpect(status().isOk())
                    .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                    .andExpect(jsonPath("$[0].id").value(testData.tournamentId.toString()))
                    .andExpect(jsonPath("$[0].name").value(testData.tournamentName))
                    .andExpect(jsonPath("$[0].signedUp").exists());

            verify(tournamentService).findAllTournamentsByRegistrant(anyString());
        }

        @Test
        @DisplayName("Should return 401 when not authenticated")
        void getAllTournaments_Unauthorized() throws Exception {
            mockMvc.perform(get("/tournaments"))
                    .andExpect(status().isUnauthorized());
        }
    }

    @Nested
    @DisplayName("GET /tournaments/explore")
    class GetAllEligibleTournamentsTests {

        @Test
        @DisplayName("Should return eligible tournaments for user")
        void getAllEligibleTournaments_AsUser() throws Exception {
            when(tournamentService.findAllEligibleTournamentsForUser(anyString()))
                    .thenReturn(List.of(testData.tournament));
            when(tournamentMapper.tournamentsToUserTournamentCardDTOs(anyList(), anyString()))
                    .thenReturn(List.of(testData.userTournamentCardDTO));

            mockMvc.perform(get("/tournaments/explore")
                            .with(jwt().jwt(jwt -> jwt.claim("sub", "user").claim("scope", "ROLE_USER"))))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$[0].id").value(testData.tournamentId.toString()))
                    .andExpect(jsonPath("$[0].name").value(testData.tournamentName))
                    .andExpect(jsonPath("$[0].signedUp").exists());

            verify(tournamentService).findAllEligibleTournamentsForUser(anyString());
        }

        @Test
        @DisplayName("Should return 401 when not authenticated")
        void getAllEligibleTournaments_Unauthorized() throws Exception {
            mockMvc.perform(get("/tournaments/explore"))
                    .andExpect(status().isUnauthorized());
        }
    }

    @Nested
    @DisplayName("GET /tournaments/{tournamentId}")
    class GetTournamentByIdTests {

        @Test
        @DisplayName("Should return tournament details for admin")
        void getTournamentById_AsAdmin() throws Exception {
            when(tournamentMapper.tournamentToAdminTournamentDTO(any(Tournament.class)))
                    .thenReturn(testData.adminTournamentDTO);

            mockMvc.perform(get("/tournaments/{tournamentId}", testData.tournamentId)
                            .with(jwt().jwt(jwt -> jwt.claim("sub", "admin").claim("scope", "ROLE_ADMIN"))))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.id").value(testData.tournamentId.toString()))
                    .andExpect(jsonPath("$.name").value(testData.tournamentName))
                    .andExpect(jsonPath("$.band").exists());

            verify(tournamentService).findTournamentById(testData.tournamentId);
        }

        @Test
        @DisplayName("Should return tournament details for user")
        void getTournamentById_AsUser() throws Exception {
            when(tournamentMapper.tournamentToUserTournamentDTO(any(Tournament.class), anyString()))
                    .thenReturn(testData.userTournamentDTO);

            mockMvc.perform(get("/tournaments/{tournamentId}", testData.tournamentId)
                            .with(jwt().jwt(jwt -> jwt.claim("sub", "user").claim("scope", "ROLE_USER"))))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.id").value(testData.tournamentId.toString()))
                    .andExpect(jsonPath("$.name").value(testData.tournamentName))
                    .andExpect(jsonPath("$.signedUp").exists());

            verify(tournamentService).findTournamentById(testData.tournamentId);
        }

        @Test
        @DisplayName("Should return 401 when not authenticated")
        void getTournamentById_Unauthorized() throws Exception {
            mockMvc.perform(get("/tournaments/{tournamentId}", testData.tournamentId))
                    .andExpect(status().isUnauthorized());
        }
    }

    @Nested
    @DisplayName("POST /tournaments/create")
    class CreateTournamentTests {

        @Test
        @DisplayName("Should successfully create tournament")
        void createTournament_Success() throws Exception {
            when(tournamentMapper.detailedTournamentDTOToTournament(any(DetailedTournamentDTO.class)))
                    .thenReturn(testData.tournament);

            mockMvc.perform(post("/tournaments/create")
                            .with(jwt().jwt(jwt -> jwt.claim("sub", "admin").claim("scope", "ROLE_ADMIN")))
                            .with(csrf())
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(testData.createTournamentDTO)))
                    .andExpect(status().isOk());
//                    .andExpect(jsonPath("$.name").value(testData.tournamentName));

            verify(tournamentService).createTournament(any(Tournament.class));
        }

        @Test
        @DisplayName("Should return 400 when request body is invalid")
        void createTournament_InvalidBody() throws Exception {
            DetailedTournamentDTO invalidDTO = new DetailedTournamentDTO(); // Empty DTO

            mockMvc.perform(post("/tournaments/create")
                            .with(jwt().jwt(jwt -> jwt.claim("sub", "admin").claim("scope", "ROLE_ADMIN")))
                            .with(csrf())
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(invalidDTO)))
                    .andExpect(status().isBadRequest());
        }
    }

    @Nested
    @DisplayName("PUT /tournaments/{tournamentId}")
    class UpdateTournamentTests {

        @Test
        @DisplayName("Should successfully update tournament when user is organiser")
        void updateTournament_Success() throws Exception {
            when(tournamentMapper.detailedTournamentDTOToTournament(any(DetailedTournamentDTO.class)))
                    .thenReturn(testData.tournament);
            when(tournamentService.findTournamentById(any(UUID.class)))
                    .thenReturn(testData.tournament);

            mockMvc.perform(put("/tournaments/{tournamentId}", testData.tournamentId)
                            .with(jwt().jwt(jwt -> jwt.claim("sub", "admin").claim("scope", "ROLE_ADMIN")))
                            .with(csrf())
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(testData.detailedTournamentDTO)))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.name").value(testData.tournamentName));

            verify(tournamentService).updateTournament(eq(testData.tournamentId), any(Tournament.class));
        }

        @Test
        @DisplayName("Should return 403 when user is not organiser")
        void updateTournament_Forbidden() throws Exception {
            // Simulate that the tournament's organiser is different from the user
            Tournament differentTournament = testData.tournament;
            differentTournament.setOrganiser("differentUser");
            when(tournamentService.findTournamentById(any(UUID.class)))
                    .thenReturn(differentTournament);
            when(tournamentMapper.detailedTournamentDTOToTournament(any(DetailedTournamentDTO.class)))
                    .thenReturn(differentTournament);

            mockMvc.perform(put("/tournaments/{tournamentId}", testData.tournamentId)
                            .with(jwt().jwt(jwt -> jwt.claim("sub", "admin").claim("scope", "ROLE_ADMIN")))
                            .with(csrf())
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(testData.detailedTournamentDTO)))
                    .andExpect(status().isForbidden());
        }

        @Test
        @DisplayName("Should return 400 when request body is invalid")
        void updateTournament_InvalidBody() throws Exception {
            DetailedTournamentDTO invalidDTO = new DetailedTournamentDTO(); // Empty DTO
            when(tournamentService.findTournamentById(any(UUID.class)))
                    .thenReturn(testData.tournament);

            mockMvc.perform(put("/tournaments/{tournamentId}", testData.tournamentId)
                            .with(jwt().jwt(jwt -> jwt.claim("sub", "admin").claim("scope", "ROLE_ADMIN")))
                            .with(csrf())
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(invalidDTO)))
                    .andExpect(status().isBadRequest());
        }
    }

    @Nested
    @DisplayName("POST /tournaments/{tournamentId}/signup")
    class AddTournamentSignupsTests {

        @Test
        @DisplayName("Should successfully sign up user to tournament")
        void addTournamentSignup_Success() throws Exception {
            when(tournamentMapper.tournamentToDetailedTournamentDTO(any(Tournament.class)))
                    .thenReturn(testData.detailedTournamentDTO);

            mockMvc.perform(post("/tournaments/{tournamentId}/signup", testData.tournamentId)
                            .with(jwt().jwt(jwt -> jwt.claim("sub", "user").claim("scope", "ROLE_USER")))
                            .with(csrf()))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.name").value(testData.tournamentName));

            verify(tournamentService).addTournamentSignup(testData.tournamentId, "user");
        }

        @Test
        @DisplayName("Should return 401 when not authenticated")
        void addTournamentSignup_Unauthorized() throws Exception {
            mockMvc.perform(post("/tournaments/{tournamentId}/signup", testData.tournamentId)
                            .with(csrf()))
                    .andExpect(status().isUnauthorized());
        }
    }

    @Nested
    @DisplayName("DELETE /tournaments/{tournamentId}/signup")
    class DeleteTournamentSignupsTests {

        @Test
        @DisplayName("Should successfully delete user's signup from tournament")
        void deleteTournamentSignup_Success() throws Exception {
            when(tournamentMapper.tournamentToDetailedTournamentDTO(any(Tournament.class)))
                    .thenReturn(testData.detailedTournamentDTO);

            mockMvc.perform(delete("/tournaments/{tournamentId}/signup", testData.tournamentId)
                            .with(jwt().jwt(jwt -> jwt.claim("sub", "user").claim("scope", "ROLE_USER")))
                            .with(csrf()))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.name").value(testData.tournamentName));

            verify(tournamentService).deleteTournamentSignup(testData.tournamentId,"user");
        }

        @Test
        @DisplayName("Should return 401 when not authenticated")
        void deleteTournamentSignup_Unauthorized() throws Exception {
            mockMvc.perform(delete("/tournaments/{tournamentId}/signup", testData.tournamentId)
                            .with(csrf()))
                    .andExpect(status().isUnauthorized());
        }
    }

    @Nested
    @DisplayName("PATCH /tournaments/{tournamentId}/leave")
    class LeaveTournamentTests {

        @Test
        @DisplayName("Should successfully leave tournament")
        void leaveTournament_Success() throws Exception {
            when(tournamentMapper.tournamentToDetailedTournamentDTO(any(Tournament.class)))
                    .thenReturn(testData.detailedTournamentDTO);

            mockMvc.perform(patch("/tournaments/{tournamentId}/leave", testData.tournamentId)
                            .with(jwt().jwt(jwt -> jwt.claim("sub", "user").claim("scope", "ROLE_USER")))
                            .with(csrf()))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.name").value(testData.tournamentName));

            verify(tournamentService).deleteTournamentParticipant(testData.tournamentId, "user");
        }

        @Test
        @DisplayName("Should return 401 when not authenticated")
        void leaveTournament_Unauthorized() throws Exception {
            mockMvc.perform(patch("/tournaments/{tournamentId}/leave", testData.tournamentId)
                            .with(csrf()))
                    .andExpect(status().isUnauthorized());
        }
    }

    @Nested
    @DisplayName("PUT /tournaments/rounds/{roundId}/end")
    class EndRoundTests {

        @Test
        @DisplayName("Should successfully end round and progress tournament")
        void endRound_Success() throws Exception {
            when(tournamentService.progressTournamentToNextRound(any(UUID.class)))
                    .thenReturn(testData.tournament);
            when(tournamentMapper.tournamentToAdminTournamentDTO(any(Tournament.class)))
                    .thenReturn(testData.adminTournamentDTO);

            mockMvc.perform(put("/tournaments/rounds/{roundId}/end", testData.roundId)
                            .with(jwt().jwt(jwt -> jwt.claim("sub", "admin").claim("scope", "ROLE_ADMIN")))
                            .with(csrf()))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.id").value(testData.tournamentId.toString()))
                    .andExpect(jsonPath("$.name").value(testData.tournamentName));

            verify(tournamentService).progressTournamentToNextRound(testData.roundId);
        }

        @Test
        @DisplayName("Should return 401 when not authenticated")
        void endRound_Unauthorized() throws Exception {
            mockMvc.perform(put("/tournaments/rounds/{roundId}/end", testData.roundId)
                            .with(csrf()))
                    .andExpect(status().isUnauthorized());
        }
    }

    @Nested
    @DisplayName("DELETE /tournaments/{tournamentId}")
    class DeleteTournamentByIdTests {

        @Test
        @DisplayName("Should successfully delete tournament when user is admin")
        void deleteTournament_Success() throws Exception {
            mockMvc.perform(delete("/tournaments/{tournamentId}", testData.tournamentId)
                            .with(jwt().jwt(jwt -> jwt.claim("sub", "admin").claim("scope", "ROLE_ADMIN")))
                            .with(csrf()))
                    .andExpect(status().isOk());

            verify(tournamentService).deleteTournamentById(testData.tournamentId);
        }

        @Test
        @DisplayName("Should return 401 when not authenticated")
        void deleteTournament_Unauthorized() throws Exception {
            mockMvc.perform(delete("/tournaments/{tournamentId}", testData.tournamentId)
                            .with(csrf()))
                    .andExpect(status().isUnauthorized());
        }
    }

    @Nested
    @DisplayName("GET /tournaments/{tournamentId}/brackets")
    class GetTournamentBracketsTests {

        @Test
        @DisplayName("Should return tournament brackets when authenticated")
        void getTournamentBrackets_Success() throws Exception {
            when(tournamentMapper.tournamentToTournamentBracketsDTO(any(Tournament.class), eq(userServiceConsumer)))
                    .thenReturn(testData.tournamentBracketsDTO);

            mockMvc.perform(get("/tournaments/{tournamentId}/brackets", testData.tournamentId)
                            .with(jwt().jwt(jwt -> jwt.claim("sub", "user").claim("scope", "ROLE_USER"))))
                    .andExpect(status().isOk());

            verify(tournamentService).findTournamentById(testData.tournamentId);
            verify(tournamentMapper).tournamentToTournamentBracketsDTO(any(Tournament.class), eq(userServiceConsumer));
        }

        @Test
        @DisplayName("Should return 401 when not authenticated")
        void getTournamentBrackets_Unauthorized() throws Exception {
            mockMvc.perform(get("/tournaments/{tournamentId}/brackets", testData.tournamentId))
                    .andExpect(status().isUnauthorized());
        }
    }

    @Nested
    @DisplayName("GET /tournaments/{tournamentId}/participants")
    class GetTournamentParticipantsTests {

        @Test
        @DisplayName("Should return tournament participants when authenticated")
        void getTournamentParticipants_Success() throws Exception {
            when(tournamentMapper.tournamentToTournamentParticipantsDTO(any(Tournament.class), any(UserServiceConsumer.class)))
                    .thenReturn(testData.tournamentParticipantsDTO);

            mockMvc.perform(get("/tournaments/{tournamentId}/participants", testData.tournamentId)
                            .with(jwt().jwt(jwt -> jwt.claim("sub", "user").claim("scope", "ROLE_USER"))))
                    .andExpect(status().isOk());
            // Add more assertions as needed

            verify(tournamentService).findTournamentById(testData.tournamentId);
            verify(tournamentMapper).tournamentToTournamentParticipantsDTO(any(Tournament.class), any(UserServiceConsumer.class));
        }

        @Test
        @DisplayName("Should return 401 when not authenticated")
        void getTournamentParticipants_Unauthorized() throws Exception {
            mockMvc.perform(get("/tournaments/{tournamentId}/participants", testData.tournamentId))
                    .andExpect(status().isUnauthorized());
        }
    }



    private static class TestData {
        final UUID tournamentId = UUID.randomUUID();
        final UUID roundId = UUID.randomUUID();
        final String tournamentName = "Test Tournament";
        final LocalDateTime startDate = LocalDateTime.now().plusDays(7);
        final LocalDateTime endDate = LocalDateTime.now().plusDays(14);
        final LocalDateTime signupStartDate = LocalDateTime.now();
        final LocalDateTime signupEndDate = LocalDateTime.now().plusDays(5);

        final Tournament tournament;
        final AdminTournamentDTO adminTournamentDTO;
        final UserTournamentDTO userTournamentDTO;
        final AdminTournamentCardDTO adminTournamentCardDTO;
        final UserTournamentCardDTO userTournamentCardDTO;
        final DetailedTournamentDTO createTournamentDTO;
        final DetailedTournamentDTO detailedTournamentDTO;
        final TournamentBracketsDTO tournamentBracketsDTO;
        final TournamentParticipantsDTO tournamentParticipantsDTO;

        TestData() {
            this.tournament = createTournament();
            this.adminTournamentDTO = createAdminTournamentDTO();
            this.userTournamentDTO = createUserTournamentDTO();
            this.adminTournamentCardDTO = createAdminTournamentCardDTO();
            this.userTournamentCardDTO = createUserTournamentCardDTO();
            this.createTournamentDTO = createDetailedTournamentDTO();
            this.detailedTournamentDTO = createDetailedTournamentDTO();
            this.tournamentBracketsDTO = createTournamentBracketsDTO();
            this.tournamentParticipantsDTO = createTournamentParticipantsDTO();
        }

        private Tournament createTournament() {
            Tournament newTournament = new Tournament();
            newTournament.setId(tournamentId);
            newTournament.setName(tournamentName);
            newTournament.setStartDate(startDate);
            newTournament.setEndDate(endDate);
            newTournament.setSignupStartDate(signupStartDate);
            newTournament.setSignupEndDate(signupEndDate);
            newTournament.setStatus(Status.UPCOMING);
            newTournament.setOrganiser("admin");
            return newTournament;
        }

        private AdminTournamentDTO createAdminTournamentDTO() {
            AdminTournamentDTO dto = new AdminTournamentDTO();
            dto.setId(tournamentId.toString());
            dto.setName(tournamentName);
            dto.setBand("MIDDLE");
            return dto;
        }

        private UserTournamentDTO createUserTournamentDTO() {
            UserTournamentDTO dto = new UserTournamentDTO();
            dto.setId(tournamentId.toString());
            dto.setName(tournamentName);
            dto.setSignedUp(true);
            return dto;
        }

        private AdminTournamentCardDTO createAdminTournamentCardDTO() {
            AdminTournamentCardDTO dto = new AdminTournamentCardDTO();
            dto.setId(tournamentId.toString());
            dto.setName(tournamentName);
            dto.setBand("MIDDLE");
            dto.setStartDate(startDate);
            dto.setEndDate(endDate);
            dto.setSignupStartDate(signupStartDate);
            dto.setSignupEndDate(signupEndDate);
            dto.setCapacity(16);
            dto.setFormat("single-elimination");
            dto.setStatus(Status.UPCOMING.toString().toLowerCase());
            dto.setNumberOfSignups(0);
            dto.setSignupsOpen(true);
            dto.setTimeWeight(30);
            dto.setMemWeight(30);
            dto.setTestCaseWeight(40);
            return dto;
        }

        private UserTournamentCardDTO createUserTournamentCardDTO() {
            UserTournamentCardDTO dto = new UserTournamentCardDTO();
            dto.setId(tournamentId.toString());
            dto.setName(tournamentName);
            dto.setStartDate(startDate);
            dto.setEndDate(endDate);
            dto.setSignupStartDate(signupStartDate);
            dto.setSignupEndDate(signupEndDate);
            dto.setCapacity(16);
            dto.setFormat("single-elimination");
            dto.setStatus(Status.UPCOMING.toString().toLowerCase());
            dto.setNumberOfSignups(0);
            dto.setSignupsOpen(true);
            dto.setSignedUp(true);
            dto.setParticipated(false);
            dto.setTimeWeight(30);
            dto.setMemWeight(30);
            dto.setTestCaseWeight(40);
            return dto;
        }

        private DetailedTournamentDTO createDetailedTournamentDTO() {
            DetailedTournamentDTO dto = new DetailedTournamentDTO();
            dto.setName(tournamentName);
            dto.setStartDate(startDate);
            dto.setEndDate(endDate);
            dto.setSignupStartDate(signupStartDate);
            dto.setSignupEndDate(signupEndDate);
            dto.setCapacity(16);
            return dto;
        }

        private TournamentBracketsDTO createTournamentBracketsDTO() {
            return new TournamentBracketsDTO();
        }

        private TournamentParticipantsDTO createTournamentParticipantsDTO() {
            return new TournamentParticipantsDTO();
        }
    }
}
