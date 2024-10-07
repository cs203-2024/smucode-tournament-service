package com.cs203.smucode.services.impl;

import com.cs203.smucode.dto.UserDTO;
import java.util.List;

public interface UserServiceClient {
    List<UserDTO> getTournamentSignups(String tournamentId);
}
