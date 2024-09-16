package com.cs203.smucode.services;

import com.cs203.smucode.models.Tournament;

import java.util.List;

public interface TournamentService {

    List<Tournament> findAllTournaments();

    Tournament findTournamentById(String id);

    Tournament saveTournament(Tournament tournament);

    void deleteTournamentById(String id);

}