package com.cs203.smucode.services;

import com.cs203.smucode.dto.BracketDTO;
import com.cs203.smucode.models.Bracket;

import java.util.List;
import java.util.UUID;

public interface BracketService {

    List<BracketDTO> findAllBracketsByRoundId(UUID roundId);

    Bracket findBracketById(UUID id);

    Bracket createBracket(Bracket bracket);

    Bracket updateBracket(UUID id, Bracket bracket);

    void deleteBracketById(UUID id);
}
