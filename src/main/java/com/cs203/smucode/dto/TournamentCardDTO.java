package com.cs203.smucode.dto;

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

//    private String signupStatus;

    private String status;

    private int numberOfSignups;

    private String currentRound;

    private LocalDateTime currentRoundEndDate;

}
