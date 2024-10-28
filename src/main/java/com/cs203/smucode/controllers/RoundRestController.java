package com.cs203.smucode.controllers;

import com.cs203.smucode.dto.RoundDTO;
import com.cs203.smucode.mappers.RoundMapper;
import com.cs203.smucode.models.Round;
import com.cs203.smucode.services.RoundService;
import io.swagger.v3.oas.annotations.Operation;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/rounds")
public class RoundRestController {

    private RoundService roundService;
    private RoundMapper roundMapper;

    @Autowired
    public RoundRestController(RoundService roundService,
                               RoundMapper roundMapper) {
        this.roundService = roundService;
        this.roundMapper = roundMapper;
    }

    @Operation(summary = "Get all rounds associated to tournament")
    @GetMapping("/tournament/{tournamentId}")
    public List<RoundDTO> getAllRoundsByTournamentId(@PathVariable UUID tournamentId) {
        List<Round> rounds = roundService.findAllRoundsByTournamentId(tournamentId);
        return roundMapper.roundsToRoundDTOs(rounds);
    }
    @Operation(summary = "Get round by round ID")
    @GetMapping("/{roundId}")
    public RoundDTO getRoundById(@PathVariable UUID roundId) {
        Round round = roundService.findRoundById(roundId);
        return roundMapper.roundToRoundDTO(round);
    }

    @Operation(summary = "Update round by round ID")
    @PutMapping("/{roundId}")
    public RoundDTO updateRound(@PathVariable UUID roundId, @Valid @RequestBody RoundDTO roundDTO) {
        Round round = roundMapper.roundDTOToRound(roundDTO);
        roundService.updateRound(roundId, round);
        return roundDTO;
    }

    @Operation(summary = "End round - populate next round brackets")
    @PutMapping("/{roundId}/end")
    public RoundDTO endRound(@PathVariable UUID roundId) {
        Round round = roundService.endRound(roundId);
        return roundMapper.roundToRoundDTO(round);
    }

    @Operation(summary = "Delete existing round by round ID")
    @DeleteMapping("/{roundId}")
    public void deleteRound(@PathVariable UUID roundId) {
        roundService.deleteRoundById(roundId);
    }
}
