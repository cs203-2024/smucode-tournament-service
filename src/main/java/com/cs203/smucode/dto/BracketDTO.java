package com.cs203.smucode.dto;

import lombok.Data;

import java.util.List;
import java.util.UUID;

@Data
public class BracketDTO {
    private String id;
    private String status;
//    private UserDTO player1;
//    private UserDTO player2;
    private List<UUID> playerIds;
    private UUID winner;
}
