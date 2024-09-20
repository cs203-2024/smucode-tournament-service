package com.cs203.smucode.controllers;

import com.cs203.smucode.models.Round;
import com.cs203.smucode.services.RoundService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/rounds")
public class RoundRestController {

    private RoundService roundService;

    @Autowired
    public RoundRestController(RoundService roundService) { this.roundService = roundService; }

    @GetMapping("/tournament/{id}")
    public List<Round> getAllRoundsByTournamentId(@PathVariable String id) {
        return roundService.findAllRoundsByTournamentId(id);
    }

    @GetMapping("/{id}")
    public Round getRoundById(@PathVariable String id) {
        return roundService.findRoundById(id);
    }

    @ResponseStatus(HttpStatus.CREATED)
    @PostMapping("/")
    public Round createRound(@Valid @RequestBody Round round) {
        return roundService.createRound(round);
    }

    @PutMapping("/{id}")
    public Round updateRound(@PathVariable String id, @Valid @RequestBody Round round) {
        return roundService.updateRound(id, round);
    }

    @DeleteMapping("/{id}")
    public void deleteRound(@PathVariable String id) {
        roundService.deleteRoundById(id);
    }
}
