package com.cs203.smucode.controllers;

import com.cs203.smucode.constants.OAuth2Constants;
import com.cs203.smucode.constants.Status;
import com.cs203.smucode.constants.UserRole;
import com.cs203.smucode.dto.*;
import com.cs203.smucode.mappers.TournamentMapper;
import com.cs203.smucode.models.Round;
import com.cs203.smucode.models.Tournament;
import com.cs203.smucode.services.TournamentService;
import com.cs203.smucode.utils.JWTUtil;
import io.swagger.v3.oas.annotations.Operation;
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.*;

@RestController
@RequestMapping("/tournaments")
public class TournamentRestController {

    private static Logger logger = LoggerFactory.getLogger(TournamentRestController.class);
    private TournamentService tournamentService;
    private TournamentMapper tournamentMapper;

    @Autowired
    public TournamentRestController(TournamentService tournamentService,
                                    TournamentMapper tournamentMapper) {
        this.tournamentService = tournamentService;
        this.tournamentMapper = tournamentMapper;
    }

    @Operation(summary = "Get all tournaments")
    @GetMapping()
    public List<? extends TournamentCardDTO> getAllTournaments() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String role = JWTUtil.getClaim(authentication, OAuth2Constants.SCOPE);
        String username = JWTUtil.getClaim(authentication, OAuth2Constants.SUBJECT);

        // Admin
        if (UserRole.ADMIN.getAuthority().equals(role)) {
            List<Tournament> tournaments = tournamentService.findAllTournamentsByOrganiser(username);
            return tournamentMapper.tournamentsToAdminTournamentCardDTOs(tournaments);
        }
        // User
        Set<Tournament> tournaments = new HashSet<>();
        tournaments.addAll(tournamentService.findAllTournamentsByStatus(Status.UPCOMING));
        tournaments.addAll(tournamentService.findAllTournamentsByParticipant(username));
        return tournamentMapper.tournamentsToUserTournamentCardDTOs(tournaments.stream().toList(), username);
    }

    @GetMapping("/explore")
    public List<UserTournamentCardDTO> getAllEligibleTournaments() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String username = JWTUtil.getClaim(authentication, OAuth2Constants.SUBJECT);
        List<Tournament> eligibleTournaments = tournamentService.findAllEligibleTournamentsForUser(username);
        return tournamentMapper.tournamentsToUserTournamentCardDTOs(eligibleTournaments, username);
    }

    @Operation(summary = "Get tournament by tournament ID")
    @GetMapping("/{tournamentId}")
    public TournamentDTO getTournamentById(@PathVariable UUID tournamentId) {
        Tournament tournament = tournamentService.findTournamentById(tournamentId);
        return tournamentMapper.tournamentToTournamentDTO(tournament);
    }

    @Operation(summary = "Get tournament brackets by tournament ID")
    @GetMapping("/{tournamentId}/brackets")
    public TournamentBracketsDTO getTournamentBracketsByTournamentId(@PathVariable UUID tournamentId) {
        Tournament tournament = tournamentService.findTournamentById(tournamentId);
        return tournamentMapper.tournamentToTournamentBracketsDTO(tournament);
    }

    @Operation(summary = "Create new tournament")
    @CrossOrigin(origins = "http://localhost:3000")
    @ResponseStatus(HttpStatus.CREATED)
    @PostMapping("/create")
    public DetailedTournamentDTO createTournament(@Valid @RequestBody DetailedTournamentDTO tournamentDTO) {
        Tournament tournament = tournamentMapper.detailedTournamentDTOToTournament(tournamentDTO);
        tournamentService.createTournament(tournament);
        return tournamentDTO;
    }

    @Operation(summary = "Update tournament by tournament ID")
    @PutMapping("/{tournamentId}")
    public DetailedTournamentDTO updateTournament(@PathVariable UUID tournamentId, @Valid @RequestBody DetailedTournamentDTO tournamentDTO) {
        Tournament tournament = tournamentMapper.detailedTournamentDTOToTournament(tournamentDTO);
        tournamentService.updateTournament(tournamentId, tournament);
        return tournamentDTO;
    }

    @Operation(summary = "Create new tournament sign up for user")
    @PostMapping("/{tournamentId}/signup")
    public DetailedTournamentDTO addTournamentSignups(@PathVariable UUID tournamentId) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String username = JWTUtil.getClaim(authentication, OAuth2Constants.SUBJECT);

        Tournament tournament = tournamentService.findTournamentById(tournamentId);
        tournamentService.addTournamentSignup(tournamentId, username);
        return tournamentMapper.tournamentToDetailedTournamentDTO(tournament);
    }

    @Operation(summary = "Delete existing tournament sign up for user")
    @DeleteMapping("/{tournamentId}/signup")
    public DetailedTournamentDTO deleteTournamentSignups(@PathVariable UUID tournamentId) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String username = JWTUtil.getClaim(authentication, OAuth2Constants.SUBJECT);

        Tournament tournament = tournamentService.findTournamentById(tournamentId);
        tournamentService.deleteTournamentSignup(tournamentId, username);
        return tournamentMapper.tournamentToDetailedTournamentDTO(tournament);
    }

    @Operation(summary = "End current round and populate next round brackets")
    @PutMapping("/round/{roundId}/end")
    public TournamentDTO endRound(@PathVariable UUID roundId) {
        Tournament tournament = tournamentService.endRound(roundId);
        return tournamentMapper.tournamentToTournamentDTO(tournament);
    }

    @Operation(summary = "Delete existing tournament by tournament ID")
    @DeleteMapping("/{tournamentId}")
    public void deleteTournamentById(@PathVariable UUID tournamentId) { tournamentService.deleteTournamentById(tournamentId); }

}
