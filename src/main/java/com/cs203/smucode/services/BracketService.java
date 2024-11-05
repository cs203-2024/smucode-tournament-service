package com.cs203.smucode.services;

import com.cs203.smucode.dto.BracketDTO;
import com.cs203.smucode.models.Bracket;

import java.util.List;
import java.util.UUID;

public interface BracketService {

    List<Bracket> findAllBracketsByRoundId(UUID roundId);

    Bracket findBracketById(UUID id);

    Bracket findBracketByRoundIdAndSeqId(UUID roundId, int seqId);

    Bracket findBracketByRoundIdAndPlayer(UUID roundId, String username);

    Bracket createBracket(Bracket bracketDTO);

    Bracket updateBracket(UUID id, Bracket bracket);

    Bracket endBracket(UUID id);

    Bracket removePlayerFromBracket(Bracket bracket, String playerId);

//    Bracket updateBracketPlayers(UUID bracketId, List<UUID> playerIds);

    void deleteBracketById(UUID id);
}
