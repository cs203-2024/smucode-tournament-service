package com.cs203.smucode.controllers;

import com.cs203.smucode.configs.TestSecurityConfig;
import com.cs203.smucode.constants.Status;
import com.cs203.smucode.dto.BracketDTO;
import com.cs203.smucode.dto.BracketUserDTO;
import com.cs203.smucode.dto.UpdateBracketScoreDTO;
import com.cs203.smucode.models.Bracket;
import com.cs203.smucode.models.PlayerInfo;
import com.cs203.smucode.models.Round;
import com.cs203.smucode.models.Tournament;
import com.cs203.smucode.services.BracketService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
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

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@Import(TestSecurityConfig.class)
@ActiveProfiles("test")
@TestPropertySource(properties = {
        "eureka.client.enabled=false",
        "spring.cloud.config.enabled=false",
        "spring.cloud.discovery.enabled=false"
})
class BracketRestControllerTest {

    @LocalServerPort
    private int port;

    @Autowired
    private TestRestTemplate restTemplate;

    @MockBean
    private BracketService bracketService;

    private String baseUrl;
    private Bracket sampleBracket;
    private UUID bracketId;
    private HttpHeaders headers;

    @BeforeEach
    void setUp() {
        baseUrl = String.format("http://localhost:%d/api/tournaments/brackets", port);
        bracketId = UUID.randomUUID();
        sampleBracket = createSampleBracket();
        headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        setupMocks();
    }

    private Bracket createSampleBracket() {
        Tournament tournament = new Tournament();
        tournament.setId(UUID.randomUUID());
        tournament.setStatus(Status.ONGOING);

        Round round = new Round();
        round.setId(UUID.randomUUID());
        round.setStatus(Status.ONGOING);
        round.setName("Round 1");
        round.setTournament(tournament);

        Bracket bracket = new Bracket();
        bracket.setId(bracketId);
        bracket.setSeqId(1);
        bracket.setStatus(Status.ONGOING);
        bracket.setPlayer1("player1");
        bracket.setPlayer2("player2");
        bracket.setPlayer1Score(0);
        bracket.setPlayer2Score(0);
        bracket.setRound(round);
        bracket.setTournament(tournament);
        return bracket;
    }

    private void setupMocks() {
        when(bracketService.findBracketById(any(UUID.class))).thenReturn(sampleBracket);
        when(bracketService.updateBracket(any(UUID.class), any(Bracket.class))).thenReturn(sampleBracket);
    }

    @Nested
    class GetBracketTests {
        @Test
        void whenValidBracketId_thenReturnsBracket() {
            ResponseEntity<BracketDTO> response = restTemplate.getForEntity(
                    baseUrl + "/" + bracketId,
                    BracketDTO.class
            );

            assertEquals(HttpStatus.OK, response.getStatusCode());
            assertNotNull(response.getBody());
            assertNotNull(response.getBody().getPlayer1());
            assertEquals("player1", response.getBody().getPlayer1().getUsername());
            verify(bracketService).findBracketById(bracketId);
        }

//        @Test
//        void whenBracketNotFound_thenReturnsNotFound() {
//            when(bracketService.findBracketById(any(UUID.class)))
//                    .thenThrow(new RuntimeException("Bracket not found"));
//
//            ResponseEntity<String> response = restTemplate.getForEntity(
//                    baseUrl + "/" + UUID.randomUUID(),
//                    String.class
//            );
//
//            assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.getStatusCode());
//            assertTrue(response.getBody().contains("Bracket not found"));
//        }
    }

    @Nested
    class UpdateBracketScoreTests {
        @Test
        void whenValidUpdate_thenReturnsUpdatedBracket() {
            // Create an update request with all required fields
            UpdateBracketScoreDTO updateRequest = new UpdateBracketScoreDTO();
            PlayerInfo player1 = new PlayerInfo("player1", 10);
            PlayerInfo player2 = new PlayerInfo("player2", 5);
            updateRequest.setPlayer1(player1);
            updateRequest.setPlayer2(player2);

            // Setup mock to return updated bracket
            Bracket updatedBracket = createSampleBracket();
            updatedBracket.setPlayer1Score(10);
            updatedBracket.setPlayer2Score(5);
            when(bracketService.updateBracket(eq(bracketId), any(Bracket.class)))
                    .thenReturn(updatedBracket);

            HttpEntity<UpdateBracketScoreDTO> request = new HttpEntity<>(updateRequest, headers);

            ResponseEntity<BracketDTO> response = restTemplate.exchange(
                    baseUrl + "/" + bracketId,
                    HttpMethod.PUT,
                    request,
                    BracketDTO.class
            );

            assertEquals(HttpStatus.OK, response.getStatusCode());
            assertNotNull(response.getBody());
            assertEquals(10, response.getBody().getPlayer1().getScore());
            assertEquals(5, response.getBody().getPlayer2().getScore());
            verify(bracketService).updateBracket(eq(bracketId), any(Bracket.class));
        }

//        @Test
//        void whenMissingPlayer_thenReturnsBadRequest() {
//            UpdateBracketScoreDTO updateRequest = new UpdateBracketScoreDTO();
//            PlayerInfo player1 = new PlayerInfo("player1", 10);
//            updateRequest.setPlayer1(player1);
//            // Deliberately not setting player2
//
//            HttpEntity<UpdateBracketScoreDTO> request = new HttpEntity<>(updateRequest, headers);
//
//            ResponseEntity<String> response = restTemplate.exchange(
//                    baseUrl + "/" + bracketId,
//                    HttpMethod.PUT,
//                    request,
//                    String.class
//            );
//
//            assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
//        }

//        @Test
//        void whenInvalidScore_thenReturnsBadRequest() {
//            UpdateBracketScoreDTO updateRequest = new UpdateBracketScoreDTO();
//            PlayerInfo player1 = new PlayerInfo("player1", -10); // Invalid negative score
//            PlayerInfo player2 = new PlayerInfo("player2", 5);
//            updateRequest.setPlayer1(player1);
//            updateRequest.setPlayer2(player2);
//
//            HttpEntity<UpdateBracketScoreDTO> request = new HttpEntity<>(updateRequest, headers);
//
//            ResponseEntity<String> response = restTemplate.exchange(
//                    baseUrl + "/" + bracketId,
//                    HttpMethod.PUT,
//                    request,
//                    String.class
//            );
//
//            assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
//        }
    }
}