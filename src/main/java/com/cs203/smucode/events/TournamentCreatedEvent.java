package com.cs203.smucode.events;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.UUID;

@Data
@AllArgsConstructor
public class TournamentCreatedEvent {
    private UUID tournamentId;
    private String username;
    private String tournamentName;
    private String message;
    private String type;
}
