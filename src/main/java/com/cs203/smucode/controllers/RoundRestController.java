package com.cs203.smucode.controllers;

import com.cs203.smucode.dto.RoundDTO;
import com.cs203.smucode.mappers.RoundMapper;
import com.cs203.smucode.models.Round;
import com.cs203.smucode.services.RoundService;
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

    @GetMapping("/tournament/{id}")
    public List<RoundDTO> getAllRoundsByTournamentId(@PathVariable UUID id) {
        List<Round> rounds = roundService.findAllRoundsByTournamentId(id);
        return roundMapper.roundsToRoundDTOs(rounds);
    }

    @GetMapping("/{id}")
    public RoundDTO getRoundById(@PathVariable UUID id) {
        Round round = roundService.findRoundById(id);
        return roundMapper.roundToRoundDTO(round);
    }

//    @ResponseStatus(HttpStatus.CREATED)
//    @PostMapping("/")
//    public RoundDTO createRound(@Valid @RequestBody RoundDTO roundDTO) {
//        Round round = roundMapper.roundDTOToRound(roundDTO);
//        roundService.createRound(round);
//        return roundDTO;
//    }

    @PutMapping("/{id}")
    public RoundDTO updateRound(@PathVariable UUID id, @Valid @RequestBody RoundDTO roundDTO) {
        Round round = roundMapper.roundDTOToRound(roundDTO);
        roundService.updateRound(id, round);
        return roundDTO;
    }

    @DeleteMapping("/{id}")
    public void deleteRound(@PathVariable UUID id) {
        roundService.deleteRoundById(id);
    }
}
