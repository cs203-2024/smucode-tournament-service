package com.cs203.smucode.controllers;

import com.cs203.smucode.constants.Status;
import com.cs203.smucode.dto.DetailedTournamentDTO;
import com.cs203.smucode.dto.TournamentCardDTO;
import com.cs203.smucode.dto.TournamentDTO;
import com.cs203.smucode.mappers.TournamentMapper;
import com.cs203.smucode.models.Tournament;
import com.cs203.smucode.services.TournamentService;
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
    @GetMapping()
    public List<? extends TournamentCardDTO> getAllTournaments(@RequestParam String id) {
//        TODO: if admin
        if (id.equals("admin")) {
            List<Tournament> tournaments = tournamentService.findAllTournamentsByOrganiser(id);
            return tournamentMapper.tournamentsToAdminTournamentCardDTOs(tournaments);
        }

//        TODO: if user
        Set<Tournament> tournaments = new HashSet<>();
        tournaments.addAll(tournamentService.findAllTournamentsByStatus(Status.UPCOMING));
        tournaments.addAll(tournamentService.findAllTournamentsByParticipant(id));
        return tournamentMapper.tournamentsToUserTournamentCardDTOs(tournaments.stream().toList());
    }


//    expose "/{id}" and return specified tournament
    @GetMapping("/{id}")
    public TournamentDTO getTournamentById(@PathVariable UUID id) {
        Tournament tournament = tournamentService.findTournamentById(id);
        return tournamentMapper.tournamentToTournamentDTO(tournament);
    }

//    POST mapping "/" to create new tournament
    @CrossOrigin(origins = "http://localhost:3000")
    @ResponseStatus(HttpStatus.CREATED)
    @PostMapping("/create")
    public DetailedTournamentDTO createTournament(@Valid @RequestBody DetailedTournamentDTO tournamentDTO) {
        Tournament tournament = tournamentMapper.detailedTournamentDTOToTournament(tournamentDTO);
        tournamentService.createTournament(tournament);
        return tournamentDTO;
    }

//    PUT mapping "/{id}" to update tournament
    @PutMapping("/{id}")
    public DetailedTournamentDTO updateTournament(@PathVariable UUID id, @Valid @RequestBody DetailedTournamentDTO tournamentDTO) {
        Tournament tournament = tournamentMapper.detailedTournamentDTOToTournament(tournamentDTO);
        tournamentService.updateTournament(id, tournament);
        return tournamentDTO;
    }

//    POST mapping "/signup" to create new signup
    @PostMapping("/{id}/signup")
    public DetailedTournamentDTO addTournamentSignups(@PathVariable UUID id, @RequestParam String user) {
        Tournament tournament = tournamentService.findTournamentById(id);
        tournamentService.addTournamentSignup(id, user);
        return tournamentMapper.tournamentToDetailedTournamentDTO(tournament);
    }

    //    DELETE mapping "/signup" to delete signup
    @DeleteMapping("/{id}/signup")
    public DetailedTournamentDTO deleteTournamentSignups(@PathVariable UUID id, @RequestParam String user) {
        Tournament tournament = tournamentService.findTournamentById(id);
        tournamentService.deleteTournamentSignup(id, user);
        return tournamentMapper.tournamentToDetailedTournamentDTO(tournament);
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
