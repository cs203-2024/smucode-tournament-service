package com.cs203.smucode.dto;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class AdminTournamentCardDTO extends TournamentCardDTO{

    private String band;
    private int timeWeight;
    private int memWeight;
    private int testCaseWeight;

}
