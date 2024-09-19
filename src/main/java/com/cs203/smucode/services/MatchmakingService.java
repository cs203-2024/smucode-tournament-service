package com.cs203.smucode.services;

import com.cs203.smucode.models.Match;
import com.cs203.smucode.models.UserDTO;

import java.util.List;


public interface MatchmakingService {
    List<Match> selectAndPairPlayers(List<UserDTO> signups, int tournamentCapacity, String selectionType, String tournamentId);
}
