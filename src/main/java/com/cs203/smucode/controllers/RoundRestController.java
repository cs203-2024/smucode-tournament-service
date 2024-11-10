package com.cs203.smucode.controllers;

import com.cs203.smucode.consumers.UserServiceConsumer;
import com.cs203.smucode.dtos.rounds.DetailedRoundDTO;
import com.cs203.smucode.dtos.rounds.RoundDTO;
import com.cs203.smucode.mappers.RoundMapper;
import com.cs203.smucode.models.Round;
import com.cs203.smucode.services.RoundService;
import io.swagger.v3.oas.annotations.Operation;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("tournaments/rounds")
public class RoundRestController {

    private RoundService roundService;
    private RoundMapper roundMapper;
    private UserServiceConsumer userServiceConsumer;

    @Autowired
    public RoundRestController(RoundService roundService,
                               RoundMapper roundMapper,
                               UserServiceConsumer userServiceConsumer) {
        this.roundService = roundService;
        this.roundMapper = roundMapper;
        this.userServiceConsumer = userServiceConsumer;
    }

    @Operation(summary = "Get round by round ID")
    @GetMapping("/{roundId}")
    public RoundDTO getRoundById(@PathVariable UUID roundId) {
        Round round = roundService.findRoundById(roundId);
        return roundMapper.roundToRoundDTO(round, userServiceConsumer);
    }

    @Operation(summary = "Update round by round ID")
    @PutMapping("/{roundId}")
    public RoundDTO updateRound(@PathVariable UUID roundId, @Valid @RequestBody RoundDTO roundDTO) {
        Round newRoundInfo = roundMapper.roundDTOToRound(roundDTO);
        Round round = roundService.updateRound(roundId, newRoundInfo);
        return roundMapper.roundToRoundDTO(round, userServiceConsumer);
    }
}
