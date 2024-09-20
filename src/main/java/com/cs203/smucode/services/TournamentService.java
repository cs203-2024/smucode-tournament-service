package com.cs203.smucode.services;

import com.cs203.smucode.models.Tournament;

import java.time.LocalDateTime;
import java.util.List;

public interface TournamentService {

    List<Tournament> findAllTournaments();

    Tournament findTournamentById(String id);

    Tournament createTournament(Tournament tournament);

    Tournament updateTournament(String id, Tournament tournament);

    void deleteTournamentById(String id);

    List<Tournament> findTournamentsBySignUpDeadline(LocalDateTime dateTime, String status);
}