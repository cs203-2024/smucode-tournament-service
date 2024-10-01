package com.cs203.smucode.services;

import com.cs203.smucode.dto.CreateTournamentDTO;
import com.cs203.smucode.dto.TournamentDTO;
import com.cs203.smucode.models.Tournament;

import java.util.List;
import java.util.UUID;

public interface TournamentService {
//    reason for having mapping within controller and not service:
//    services may use one another - would have to map everytime

    List<Tournament> findAllTournaments();

    Tournament findTournamentById(UUID id);

    Tournament createTournament(Tournament tournament);

    Tournament updateTournament(UUID id, Tournament tournament);

    void deleteTournamentById(UUID id);

}