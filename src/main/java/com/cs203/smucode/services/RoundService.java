package com.cs203.smucode.services;

import com.cs203.smucode.models.Round;

import java.util.UUID;

public interface RoundService {

    Round findRoundById(UUID id);

    Round findRoundByTournamentIdAndSeqId(UUID tournamentId, int seqId);

    Round findRoundByTournamentIdAndName(UUID tournamentId, String name);

    Round createRound(Round round);

    Round updateRound(UUID id, Round round);

    Round populateNextRound(UUID currRoundId, UUID nextRoundId);

    Round removePlayerFromOngoingRound(UUID roundId, String username);
}
