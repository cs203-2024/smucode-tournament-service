package com.cs203.smucode.dto;

import com.cs203.smucode.models.PlayerInfo;
import lombok.Data;

import java.util.ArrayList;
import java.util.List;

@Data
public class BracketDTO {

    private String id;

    private int seqId;

    private String status;

    private UserBracketDTO player1;

    private UserBracketDTO player2;

//    private List<PlayerInfo> players = new ArrayList<>();

    private String winner;

}
