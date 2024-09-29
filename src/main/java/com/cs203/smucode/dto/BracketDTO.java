package com.cs203.smucode.dto;

import lombok.Data;

import java.util.List;
import java.util.Set;

@Data
public class BracketDTO {
    private String id;
    private String status;
//    private UserDTO player1;
//    private UserDTO player2;
    private List<UserDTO> players;
    private UserDTO winner;
}
