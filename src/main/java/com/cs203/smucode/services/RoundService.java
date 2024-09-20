package com.cs203.smucode.services;

import com.cs203.smucode.models.Round;

import java.util.List;

public interface RoundService {

    List<Round> findAllRoundsByTournamentId(String tournamentId);

    Round findRoundById(String id);

    Round createRound(Round round);

    Round updateRound(String id, Round round);

    void deleteRoundById(String id);
}
