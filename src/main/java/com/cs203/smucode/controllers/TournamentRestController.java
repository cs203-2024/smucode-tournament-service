package com.cs203.smucode.controllers;

import com.cs203.smucode.models.Tournament;
import com.cs203.smucode.services.TournamentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
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

//    POST mapping "/{id}" to create new tournament
    @PostMapping("/")
    public Tournament createTournament(Tournament tournament) { return tournamentService.saveTournament(tournament); }

//    PUT mapping "/{id}" to update tournament
//    @PutMapping("/")
//    public Tournament updateTournament()

}
