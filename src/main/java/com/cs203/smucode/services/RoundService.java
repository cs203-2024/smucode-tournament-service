package com.cs203.smucode.services;

import com.cs203.smucode.dto.RoundDTO;
import com.cs203.smucode.models.Round;

import java.util.List;
import java.util.UUID;

public interface RoundService {

    List<RoundDTO> findAllRoundsByTournamentId(UUID tournamentId);

    RoundDTO findRoundById(UUID id);

//    RoundDTO createRound(Round round);
//
//    RoundDTO updateRound(String id, Round round);

    void deleteRoundById(UUID id);

//    List<RoundDTO> toRoundDTOs(List<Round> rounds);
}
