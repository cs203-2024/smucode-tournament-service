package com.cs203.smucode.factories;

import com.cs203.smucode.services.TournamentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Map;

@Component
public class TournamentServiceFactory {
    private final Map<String, TournamentService> tournamentServiceMap;

    @Autowired
    public TournamentServiceFactory(Map<String, TournamentService> tournamentServiceMap) {
        this.tournamentServiceMap = tournamentServiceMap;
    }

    public TournamentService getTournamentService(String tournamentName) {
        return tournamentServiceMap.get(tournamentName);
    }
}