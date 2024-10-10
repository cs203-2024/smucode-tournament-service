package com.cs203.smucode.controllers;

import com.cs203.smucode.dto.CreateTournamentDTO;
import com.cs203.smucode.dto.TournamentDTO;
import com.cs203.smucode.exceptions.TournamentNotFoundException;
import com.cs203.smucode.mappers.TournamentMapper;
import com.cs203.smucode.models.Tournament;
import com.cs203.smucode.services.TournamentService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/tournaments")
public class TournamentRestController {

    private TournamentService tournamentService;
    private TournamentMapper tournamentMapper;

    @Autowired
    public TournamentRestController(TournamentService tournamentService,
                                    TournamentMapper tournamentMapper) {
        this.tournamentService = tournamentService;
        this.tournamentMapper = tournamentMapper;
    }

//    expose "/" and return list of tournaments
    @GetMapping("/")
    public List<TournamentDTO> getAllTournaments() {
        List<Tournament> tournaments = tournamentService.findAllTournaments();
        return tournamentMapper.tournamentsToTournamentDTOs(tournaments);
    }


//    expose "/{id}" and return specified tournament
    @GetMapping("/{id}")
    public TournamentDTO getTournamentById(@PathVariable UUID id) {
        Tournament tournament = tournamentService.findTournamentById(id);
        return tournamentMapper.tournamentToTournamentDTO(tournament);
    }

//    POST mapping "/" to create new tournament
    @ResponseStatus(HttpStatus.CREATED)
    @PostMapping("/create")
    public CreateTournamentDTO createTournament(@Valid @RequestBody CreateTournamentDTO tournamentDTO) {
        Tournament tournament = tournamentMapper.createTournamentDTOToTournament(tournamentDTO);
        tournamentService.createTournament(tournament);
        return tournamentDTO;
    }

//    PUT mapping "/{id}" to update tournament
    @PutMapping("/{id}")
    public CreateTournamentDTO updateTournament(@PathVariable UUID id, @Valid @RequestBody CreateTournamentDTO tournamentDTO) {
        Tournament tournament = tournamentMapper.createTournamentDTOToTournament(tournamentDTO);
        tournamentService.updateTournament(id, tournament);
        return tournamentDTO;
    }

//    PUT mapping "/signups" to update tournament signups
    @PutMapping("/{id}/signups")
    public CreateTournamentDTO updateTournamentSignups(@PathVariable UUID id, @Valid @RequestBody CreateTournamentDTO tournamentDTO) {
        Tournament tournament = tournamentMapper.createTournamentDTOToTournament(tournamentDTO);
        tournamentService.updateTournamentSignups(id, tournament.getSignups());
        return tournamentDTO;
    }

//    TODO: can create more focused DTOs
//    TODO: should these apis (updateBracketScore, endRound) be here or in round / bracket controller
//    public TournamentDTO updateTournamentScore(@PathVariable UUID bracketId, @Valid @RequestBody) {}

    @PutMapping("/{id}/progress")
    public TournamentDTO updateTournamentProgression(@PathVariable UUID id) {
        Tournament tournament = tournamentService.updateTournamentProgress(id);
        return tournamentMapper.tournamentToTournamentDTO(tournament);
    }

//    DELETE mapping "/{id}" to delete tournament
    @DeleteMapping("/{id}")
    public void deleteTournamentById(@PathVariable UUID id) { tournamentService.deleteTournamentById(id); }

}
