package com.cs203.smucode.dto;

import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

@Data
public class TournamentDTO {
    private String id;
    private String icon;
    private String name;
    private LocalDateTime startDate;
    private LocalDateTime endDate;
    private LocalDateTime signupStartDate;
    private LocalDateTime signupEndDate;
    private String signupStatus;
    private String status;
    private int capacity;
    private String band;
    private String currentRound;
    private List<RoundDTO> rounds;
}
