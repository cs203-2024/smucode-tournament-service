package com.cs203.smucode.services.impl;

import com.cs203.smucode.dto.CreateTournamentDTO;
import com.cs203.smucode.dto.RoundDTO;
import com.cs203.smucode.mappers.TournamentMapper;
import com.cs203.smucode.dto.TournamentDTO;
import com.cs203.smucode.exceptions.TournamentNotFoundException;
import com.cs203.smucode.models.Round;
import com.cs203.smucode.models.Tournament;
import com.cs203.smucode.repositories.TournamentServiceRepository;
import com.cs203.smucode.services.RoundService;
import com.cs203.smucode.services.TournamentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
public class TournamentServiceImpl implements TournamentService {
    private final TournamentServiceRepository tournamentServiceRepository;
    private final RoundService roundService;

    @Autowired
    public TournamentServiceImpl(TournamentServiceRepository tournamentServiceRepository,
                                 RoundService roundService) {
        this.tournamentServiceRepository = tournamentServiceRepository;
        this.roundService = roundService;
    }

    public List<Tournament> findAllTournaments() {
        return tournamentServiceRepository.findAll();
    }

    public Tournament findTournamentById(UUID id) {
        Optional<Tournament> tournament = tournamentServiceRepository.findById(id);
        if (tournament.isEmpty()) {
            throw new TournamentNotFoundException("Tournament with id " + id + " not found");
        }
        return tournament.get();
    }

    public Tournament createTournament(Tournament tournament) {

        // TODO: data insert validation
        if (tournament == null) { return null; }

        // TODO: generate rounds logic

        tournamentServiceRepository.save(tournament);
        createRounds(tournament);

        return tournament;
    }

    public Tournament updateTournament(UUID id, Tournament tournament) {
//        return tournamentServiceRepository.findById(id).map(existingTournament -> {
//            existingTournament.setName(tournament.getName());
//            existingTournament.setDescription(tournament.getDescription());
//            existingTournament.setStatus(tournament.getStatus());
//            existingTournament.setStartDate(tournament.getStartDate());
//            existingTournament.setEndDate(tournament.getEndDate());
//            existingTournament.setFormat(tournament.getFormat());
//            existingTournament.setCapacity(tournament.getCapacity());
//            existingTournament.setIcon(tournament.getIcon());
//            existingTournament.setOwner(tournament.getOwner());
//            existingTournament.setSignUpDeadline(tournament.getSignUpDeadline());
//            existingTournament.setTimeWeight(tournament.getTimeWeight());
//            existingTournament.setMemWeight(tournament.getMemWeight());
//            existingTournament.setTestCaseWeight(tournament.getTestCaseWeight());
//
//            return tournamentServiceRepository.save(existingTournament);
//        }).orElseThrow(() -> new TournamentNotFoundException("Tournament not found with id: " +id));
        return null;
    }

    public void deleteTournamentById(UUID id) { tournamentServiceRepository.deleteById(id); }

//    helper classes
    List<Round> createRounds(Tournament tournament) {
        // generate list of rounds
        List<Integer> roundSizes = new ArrayList<>();
        int capacity = tournament.getCapacity();
        while (capacity > 1) {
            roundSizes.add(capacity);
            capacity /= 2;
        }

        List<Round> createdRounds = new ArrayList<>();
        for (int roundSize : roundSizes) {
            Round round = new Round();
            round.setTournament(tournament);
            round.setName("Round of " + roundSize);

            Round createdRound = roundService.createRound(round);
            createdRounds.add(createdRound);

        }
        return createdRounds;

    }
}

