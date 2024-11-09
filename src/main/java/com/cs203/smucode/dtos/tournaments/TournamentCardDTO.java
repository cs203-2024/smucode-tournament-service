package com.cs203.smucode.dtos.tournaments;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class TournamentCardDTO {

    private String id;

    private String icon;

    private String name;

    private int capacity;

    private String format;

    private LocalDateTime startDate;

    private LocalDateTime endDate;

    private LocalDateTime signupStartDate;

    private LocalDateTime signupEndDate;

    private boolean signupsOpen;

    private String status;

    private int numberOfSignups;

    private String currentRound;

    private LocalDateTime currentRoundEndDate;

    private int timeWeight;

    private int memWeight;

    private int testCaseWeight;

}
