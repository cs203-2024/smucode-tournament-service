package com.cs203.smucode.controllers;

import com.cs203.smucode.configs.TestSecurityConfig;
import com.cs203.smucode.constants.Status;
import com.cs203.smucode.consumers.UserServiceConsumer;
import com.cs203.smucode.dto.*;
import com.cs203.smucode.exceptions.TournamentNotFoundException;
import com.cs203.smucode.models.Tournament;
import com.cs203.smucode.services.TournamentService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.context.annotation.Import;
import org.springframework.http.*;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.TestPropertySource;

import java.time.LocalDateTime;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@Import(TestSecurityConfig.class)
@ActiveProfiles("test")
@TestPropertySource(properties = {
        "eureka.client.enabled=false",
        "spring.cloud.config.enabled=false"
})
class TournamentRestControllerTest {

    @LocalServerPort
    private int port;

    @Autowired
    private TestRestTemplate restTemplate;

    @MockBean
    private TournamentService tournamentService;

    @MockBean
    private UserServiceConsumer userServiceConsumer;

    private String baseUrl;
    private Tournament sampleTournament;
    private UUID tournamentId;
    private UUID bracketId;
    private UUID roundId;

    @BeforeEach
    void setUp() {
        baseUrl = String.format("http://localhost:%d/api/tournaments", port);
        tournamentId = UUID.randomUUID();
        bracketId = UUID.randomUUID();
        roundId = UUID.randomUUID();
        sampleTournament = createSampleTournament();
        setupMocks();
    }

    private Tournament createSampleTournament() {
        Tournament tournament = new Tournament();
        tournament.setId(tournamentId);
        tournament.setName("Test Tournament");
        tournament.setStatus(Status.ONGOING);
        tournament.setParticipants(new HashSet<>(Arrays.asList("user1", "user2")));
        tournament.setRounds(new ArrayList<>());
        tournament.setBrackets(new ArrayList<>());

        // Set dates to prevent null pointer in isBefore() checks
        LocalDateTime now = LocalDateTime.now();
        tournament.setStartDate(now);
        tournament.setEndDate(now.plusDays(7));
        tournament.setSignupStartDate(now.minusDays(1));
        tournament.setSignupEndDate(now.plusDays(1));

        return tournament;
    }

    private void setupMocks() {
        // Mock tournament service
        when(tournamentService.findTournamentById(tournamentId)).thenReturn(sampleTournament);
        when(tournamentService.endBracket(bracketId)).thenReturn(sampleTournament);
        when(tournamentService.endRound(roundId)).thenReturn(sampleTournament);

        // Mock user service
        UserDTO mockUserDTO = new UserDTO(
                "user1",
                "password",
                "user1@test.com",
                "/images/profile.jpg",
                "ROLE_USER",
                25.0,
                8.33,
                1200.0
        );
        when(userServiceConsumer.getUserById(anyString())).thenReturn(mockUserDTO);
    }

    @Test
    void getTournamentBrackets_Success() {
        ResponseEntity<TournamentBracketsDTO> response = restTemplate.getForEntity(
                baseUrl + "/" + tournamentId + "/brackets",
                TournamentBracketsDTO.class
        );

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        verify(tournamentService).findTournamentById(tournamentId);
    }

    @Test
    void getTournamentBrackets_NotFound() {
        when(tournamentService.findTournamentById(any()))
                .thenThrow(new TournamentNotFoundException("Tournament not found"));

        ResponseEntity<String> response = restTemplate.getForEntity(
                baseUrl + "/" + UUID.randomUUID() + "/brackets",
                String.class
        );

        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        assertTrue(response.getBody().contains("Tournament not found"));
    }

    @Test
    void getTournamentParticipants_Success() {
        ResponseEntity<TournamentParticipantsDTO> response = restTemplate.getForEntity(
                baseUrl + "/" + tournamentId + "/participants",
                TournamentParticipantsDTO.class
        );

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertNotNull(response.getBody().getParticipants());
        assertFalse(response.getBody().getParticipants().isEmpty());
        verify(tournamentService).findTournamentById(tournamentId);
        verify(userServiceConsumer, atLeast(1)).getUserById(anyString());
    }

    @Test
    void getTournamentParticipants_NotFound() {
        when(tournamentService.findTournamentById(any()))
                .thenThrow(new TournamentNotFoundException("Tournament not found"));

        ResponseEntity<String> response = restTemplate.getForEntity(
                baseUrl + "/" + UUID.randomUUID() + "/participants",
                String.class
        );

        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        assertTrue(response.getBody().contains("Tournament not found"));
    }

    @Test
    void endBracket_Success() {
        ResponseEntity<TournamentDTO> response = restTemplate.exchange(
                baseUrl + "/brackets/" + bracketId + "/end",
                HttpMethod.PUT,
                null,
                TournamentDTO.class
        );

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        verify(tournamentService).endBracket(bracketId);
    }

    @Test
    void endRound_Success() {
        ResponseEntity<TournamentDTO> response = restTemplate.exchange(
                baseUrl + "/rounds/" + roundId + "/end",
                HttpMethod.PUT,
                null,
                TournamentDTO.class
        );

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        verify(tournamentService).endRound(roundId);
    }

    @Test
    void deleteTournament_Success() {
        ResponseEntity<Void> response = restTemplate.exchange(
                baseUrl + "/" + tournamentId,
                HttpMethod.DELETE,
                null,
                Void.class
        );

        assertEquals(HttpStatus.OK, response.getStatusCode());
        verify(tournamentService).deleteTournamentById(tournamentId);
    }

    @Test
    void deleteTournament_NotFound() {
        doThrow(new TournamentNotFoundException("Tournament not found"))
                .when(tournamentService).deleteTournamentById(any());

        ResponseEntity<String> response = restTemplate.exchange(
                baseUrl + "/" + UUID.randomUUID(),
                HttpMethod.DELETE,
                null,
                String.class
        );

        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        assertTrue(response.getBody().contains("Tournament not found"));
    }
}