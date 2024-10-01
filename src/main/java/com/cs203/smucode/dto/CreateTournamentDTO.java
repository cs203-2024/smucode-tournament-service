package com.cs203.smucode.dto;

import lombok.Data;

import java.time.LocalDateTime;
import java.util.UUID;

@Data
public class CreateTournamentDTO {
    private String name;
    private String format;
    private int capacity;
    private String band;
    private LocalDateTime signupStartDate;
    private LocalDateTime signupEndDate;
    private String signupStatus;
    private LocalDateTime startDate;
    private LocalDateTime endDate;
    private String status;
    private int timeWeight;
    private int memWeight;
    private int testCaseWeight;
    private String description;
    private String icon;
    private UUID owner;
}
