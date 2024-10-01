package com.cs203.smucode.services;

import com.cs203.smucode.dto.RoundDTO;
import com.cs203.smucode.models.Round;

import java.util.List;
import java.util.UUID;

public interface RoundService {

    List<Round> findAllRoundsByTournamentId(UUID tournamentId);

    Round findRoundById(UUID id);

    Round createRound(Round round);

    Round updateRound(UUID id, Round round);

    void deleteRoundById(UUID id);
}
