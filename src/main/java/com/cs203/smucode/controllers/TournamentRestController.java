package com.cs203.smucode.controllers;

import com.cs203.smucode.models.Tournament;
import com.cs203.smucode.services.TournamentService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/tournaments")
public class TournamentRestController {

    private TournamentService tournamentService;

    @Autowired
    public TournamentRestController(TournamentService tournamentService) { this.tournamentService = tournamentService; }

//    expose "/" and return list of tournaments
    @GetMapping("/")
    public List<Tournament> getAllTournaments() { return tournamentService.findAllTournaments(); }

//    expose "/{id}" and return specified tournament
    @GetMapping("/{id}")
    public Tournament getTournamentById(@PathVariable String id) {
        Tournament tournament = tournamentService.findTournamentById(id);

//        TODO: create proper exceptions?
        if (tournament == null) {
            throw new RuntimeException("Tournament with id " + id + " not found");
        }

        return tournamentService.findTournamentById(id);
    }

//    POST mapping "/" to create new tournament
    @ResponseStatus(HttpStatus.CREATED)
    @PostMapping("/")
    public Tournament createTournament(@Valid @RequestBody Tournament tournament) { return tournamentService.createTournament(tournament); }

//    PUT mapping "/{id}" to update tournament
    @PutMapping("/{id}")
    public Tournament updateTournament(@PathVariable String id, @Valid @RequestBody Tournament tournament) { return tournamentService.updateTournament(id, tournament); }

//    DELETE mapping "/{id}" to delete tournament
    @DeleteMapping("/{id}")
    public void deleteTournamentById(@PathVariable String id) { tournamentService.deleteTournamentById(id); }

}
