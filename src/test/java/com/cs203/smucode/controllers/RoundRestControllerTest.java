package com.cs203.smucode.controllers;

import com.cs203.smucode.configs.TestSecurityConfig;
import com.cs203.smucode.constants.Status;
import com.cs203.smucode.dto.RoundDTO;
import com.cs203.smucode.exceptions.RoundNotFoundException;
import com.cs203.smucode.models.Round;
import com.cs203.smucode.models.Tournament;
import com.cs203.smucode.services.RoundService;
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
import java.util.ArrayList;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@Import(TestSecurityConfig.class)
@ActiveProfiles("test")
@TestPropertySource(properties = {
        "eureka.client.enabled=false",
        "spring.cloud.config.enabled=false"
})
class RoundRestControllerTest {

    @LocalServerPort
    private int port;

    @Autowired
    private TestRestTemplate restTemplate;

    @MockBean
    private RoundService roundService;

    private String baseUrl;
    private Round sampleRound;
    private UUID roundId;

    @BeforeEach
    void setUp() {
        baseUrl = String.format("http://localhost:%d/api/tournaments/rounds", port);
        roundId = UUID.randomUUID();
        sampleRound = createSampleRound();
        when(roundService.findRoundById(roundId)).thenReturn(sampleRound);
    }

    private Round createSampleRound() {
        Tournament tournament = new Tournament();
        tournament.setId(UUID.randomUUID());

        Round round = new Round();
        round.setId(roundId);
        round.setSeqId(1);
        round.setName("Round of 16");
        round.setStartDate(LocalDateTime.now());
        round.setEndDate(LocalDateTime.now().plusDays(1));
        round.setStatus(Status.ONGOING);
        round.setTournament(tournament);
        round.setBrackets(new ArrayList<>());
        return round;
    }

    @Test
    void getRoundById_Success() {
        // Test successful round retrieval
        ResponseEntity<RoundDTO> response = restTemplate.getForEntity(
                baseUrl + "/" + roundId,
                RoundDTO.class
        );

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(roundId.toString(), response.getBody().getId());
        assertEquals("Round of 16", response.getBody().getName());
        verify(roundService).findRoundById(roundId);
    }

    @Test
    void getRoundById_NotFound() {
        // Test round not found scenario
        UUID nonExistentId = UUID.randomUUID();
        when(roundService.findRoundById(nonExistentId))
                .thenThrow(new RoundNotFoundException("Round not found"));

        ResponseEntity<String> response = restTemplate.getForEntity(
                baseUrl + "/" + nonExistentId,
                String.class
        );

        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        assertTrue(response.getBody().contains("Round not found"));
    }

    @Test
    void updateRound_Success() {
        // Create round update request
        RoundDTO updateRequest = new RoundDTO();
        updateRequest.setName("Updated Round");
        updateRequest.setStartDate(LocalDateTime.now());
        updateRequest.setEndDate(LocalDateTime.now().plusDays(1));
        updateRequest.setStatus("ONGOING");

        // Mock service to return updated round
        Round updatedRound = createSampleRound();
        updatedRound.setName("Updated Round");
        when(roundService.updateRound(eq(roundId), any(Round.class))).thenReturn(updatedRound);

        // Perform update request
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        HttpEntity<RoundDTO> request = new HttpEntity<>(updateRequest, headers);

        ResponseEntity<RoundDTO> response = restTemplate.exchange(
                baseUrl + "/" + roundId,
                HttpMethod.PUT,
                request,
                RoundDTO.class
        );

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals("Updated Round", response.getBody().getName());
        verify(roundService).updateRound(eq(roundId), any(Round.class));
    }
}