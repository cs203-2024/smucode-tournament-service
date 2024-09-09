package com.cs203.smucode.services.impl;

import com.cs203.smucode.repositories.TournamentServiceRepository;
import com.cs203.smucode.models.Tournament;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class TournamentServiceImpl {
    private final TournamentServiceRepository tournamentServiceRepository;

    @Autowired
    public TournamentServiceImpl(TournamentServiceRepository tournamentServiceRepository) {
        this.tournamentServiceRepository = tournamentServiceRepository;
    }

    public void createTournament(Tournament tournament) {
        tournamentServiceRepository.save(tournament);
    }

}