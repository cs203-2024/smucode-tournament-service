package com.cs203.smucode.services.impl;

import com.cs203.smucode.models.Tournament;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class TournamentServiceImpl {
    private final TournamentServiceFactory tournamentServiceFactory;

    @Autowired
    public TournamentServiceImpl(TournamentServiceFactory tournamentServiceFactory) {
        this.tournamentServiceFactory = tournamentServiceFactory;
    }

    void createTournament(Tournament tournament) {
        return;
    }
}