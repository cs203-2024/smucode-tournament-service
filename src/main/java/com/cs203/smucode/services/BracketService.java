package com.cs203.smucode.services;

import com.cs203.smucode.models.Bracket;

import java.util.List;

public interface BracketService {

    List<Bracket> findAllBracketsByRoundId(String roundId);

    Bracket findBracketById(String id);

    Bracket createBracket(Bracket bracket);

    Bracket updateBracket(String id, Bracket bracket);

    void deleteBracketById(String id);
}
