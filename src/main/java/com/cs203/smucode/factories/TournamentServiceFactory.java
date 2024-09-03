package com.cs203.smucode.factories;

import com.cs203.smucode.services.ITournamentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Map;

@Component
public class TournamentServiceFactory {
    private final Map<String, ITournamentService> tournamentServiceMap;

    @Autowired
    public TournamentServiceFactory(Map<String, ITournamentService> tournamentServiceMap) {
        this.tournamentServiceMap = tournamentServiceMap;
    }

    public ITournamentService getTournamentService(String tournamentName) {
        return tournamentServiceMap.get(tournamentName);
    }
}