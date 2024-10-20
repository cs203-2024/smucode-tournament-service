package com.cs203.smucode.controllers;

import com.cs203.smucode.constants.Status;
import com.cs203.smucode.dto.DetailedTournamentDTO;
import com.cs203.smucode.dto.TournamentBracketsDTO;
import com.cs203.smucode.dto.TournamentCardDTO;
import com.cs203.smucode.dto.TournamentDTO;
import com.cs203.smucode.mappers.TournamentMapper;
import com.cs203.smucode.models.Tournament;
import com.cs203.smucode.services.TournamentService;
import io.swagger.v3.oas.annotations.Operation;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.*;

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
    @Operation(summary = "Get all tournaments")
    @GetMapping()
    public List<? extends TournamentCardDTO> getAllTournaments(@RequestParam String username) {
//        TODO: if admin
        if (username.equals("admin")) {
            List<Tournament> tournaments = tournamentService.findAllTournamentsByOrganiser(username);
            return tournamentMapper.tournamentsToAdminTournamentCardDTOs(tournaments);
        }

//        TODO: if user
        Set<Tournament> tournaments = new HashSet<>();
        tournaments.addAll(tournamentService.findAllTournamentsByStatus(Status.UPCOMING));
        tournaments.addAll(tournamentService.findAllTournamentsByParticipant(username));
        return tournamentMapper.tournamentsToUserTournamentCardDTOs(tournaments.stream().toList(), username);
    }


//    expose "/{id}" and return specified tournament
    @Operation(summary = "Get tournament by tournament ID")
    @GetMapping("/{tournamentId}")
    public TournamentDTO getTournamentById(@PathVariable UUID tournamentId) {
        Tournament tournament = tournamentService.findTournamentById(tournamentId);
        return tournamentMapper.tournamentToTournamentDTO(tournament);
    }

//    endpoint for specified tournament's brackets
    @Operation(summary = "Get tournament brackets by tournament ID")
    @GetMapping("/{tournamentId}/brackets")
    public TournamentBracketsDTO getTournamentBracketsByTournamentId(@PathVariable UUID tournamentId) {
        Tournament tournament = tournamentService.findTournamentById(tournamentId);
        return tournamentMapper.tournamentToTournamentBracketsDTO(tournament);
    }

//    POST mapping "/" to create new tournament
    @Operation(summary = "Create new tournament")
    @CrossOrigin(origins = "http://localhost:3000")
    @ResponseStatus(HttpStatus.CREATED)
    @PostMapping("/create")
    public DetailedTournamentDTO createTournament(@Valid @RequestBody DetailedTournamentDTO tournamentDTO) {
        Tournament tournament = tournamentMapper.detailedTournamentDTOToTournament(tournamentDTO);
        tournamentService.createTournament(tournament);
        return tournamentDTO;
    }

//    PUT mapping "/{id}" to update tournament
    @Operation(summary = "Update tournament by tournament ID")
    @PutMapping("/{tournamentId}")
    public DetailedTournamentDTO updateTournament(@PathVariable UUID tournamentId, @Valid @RequestBody DetailedTournamentDTO tournamentDTO) {
        Tournament tournament = tournamentMapper.detailedTournamentDTOToTournament(tournamentDTO);
        tournamentService.updateTournament(tournamentId, tournament);
        return tournamentDTO;
    }

//    POST mapping "/signup" to create new signup
    @Operation(summary = "Create new tournament sign up for user")
    @PostMapping("/{tournamentId}/signup")
    public DetailedTournamentDTO addTournamentSignups(@PathVariable UUID tournamentId, @RequestParam String user) {
        Tournament tournament = tournamentService.findTournamentById(tournamentId);
        tournamentService.addTournamentSignup(tournamentId, user);
        return tournamentMapper.tournamentToDetailedTournamentDTO(tournament);
    }

    //    DELETE mapping "/signup" to delete signup
    @Operation(summary = "Delete existing tournament sign up for user")
    @DeleteMapping("/{tournamentId}/signup")
    public DetailedTournamentDTO deleteTournamentSignups(@PathVariable UUID tournamentId, @RequestParam String user) {
        Tournament tournament = tournamentService.findTournamentById(tournamentId);
        tournamentService.deleteTournamentSignup(tournamentId, user);
        return tournamentMapper.tournamentToDetailedTournamentDTO(tournament);
    }

//    TODO: can create more focused DTOs
//    TODO: should these apis (updateBracketScore, endRound) be here or in round / bracket controller
//    public TournamentDTO updateTournamentScore(@PathVariable UUID bracketId, @Valid @RequestBody) {}

    @Operation(summary = "Update tournament progression - end round")
    @PutMapping("/{tournamentId}/progress")
    public TournamentDTO updateTournamentProgression(@PathVariable UUID tournamentId) {
        Tournament tournament = tournamentService.updateTournamentProgress(tournamentId);
        return tournamentMapper.tournamentToTournamentDTO(tournament);
    }

//    DELETE mapping "/{id}" to delete tournament
    @Operation(summary = "Delete existing tournament by tournament ID")
    @DeleteMapping("/{tournamentId}")
    public void deleteTournamentById(@PathVariable UUID tournamentId) { tournamentService.deleteTournamentById(tournamentId); }

}
