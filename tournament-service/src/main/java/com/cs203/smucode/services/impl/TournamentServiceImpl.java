package com.cs203.smucode.services.impl;

import com.cs203.smucode.factories.TournamentServiceFactory;
import com.cs203.smucode.models.Tournament;
import com.cs203.smucode.services.ITournamentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class TournamentServiceImpl implements ITournamentService {
    private final TournamentServiceFactory tournamentServiceFactory;

    @Autowired
    public TournamentServiceImpl(TournamentServiceFactory tournamentServiceFactory) {
        this.tournamentServiceFactory = tournamentServiceFactory;
    }

    @Override
    public void createTournament(Tournament tournament) {
        ITournamentService tournamentService = tournamentServiceFactory.getTournamentService(tournament.getFormat());
        tournamentService.createTournament(tournament);
    }
}
