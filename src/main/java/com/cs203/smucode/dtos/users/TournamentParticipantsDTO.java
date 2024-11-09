package com.cs203.smucode.dtos.users;

import lombok.Data;

import java.util.HashSet;
import java.util.Set;

@Data
public class TournamentParticipantsDTO {

    private Set<ParticipantUserDTO> participants = new HashSet<>();

}
