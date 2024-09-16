package com.cs203.smucode.services.impl;

import com.cs203.smucode.repositories.TournamentServiceRepository;
import com.cs203.smucode.models.Tournament;
import com.cs203.smucode.services.TournamentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class TournamentServiceImpl implements TournamentService {
    private final TournamentServiceRepository tournamentServiceRepository;

    @Autowired
    public TournamentServiceImpl(TournamentServiceRepository tournamentServiceRepository) {
        this.tournamentServiceRepository = tournamentServiceRepository;
    }

    public List<Tournament> findAllTournaments() { return tournamentServiceRepository.findAll(); }

    public Tournament findTournamentById(String id) {
        Optional<Tournament> tournament = tournamentServiceRepository.findById(id);
        return tournament.orElse(null);
    }

    public Tournament saveTournament(Tournament tournament) {
        if (tournament == null) { return null; } // data insert validation

        return tournamentServiceRepository.save(tournament);
    }

    public void deleteTournamentById(String id) { tournamentServiceRepository.deleteById(id); }

}