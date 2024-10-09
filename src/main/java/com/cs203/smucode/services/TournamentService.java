package com.cs203.smucode.services;

import com.cs203.smucode.dto.TournamentDTO;
import com.cs203.smucode.models.Tournament;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

public interface TournamentService {

    List<TournamentDTO> findAllTournaments();

    TournamentDTO findTournamentById(UUID id);

//    TournamentDTO createTournament(TournamentDTO tournament);

//    TournamentDTO updateTournament(String id, TournamentDTO tournament);

    void deleteTournamentById(UUID id);

    List<Tournament> findTournamentsBySignUpDeadline(LocalDateTime dateTime, String status);
}