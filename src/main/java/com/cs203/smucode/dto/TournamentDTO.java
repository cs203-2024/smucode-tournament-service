package com.cs203.smucode.dto;

import lombok.Data;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

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
    private List<RoundDTO> rounds = new ArrayList<>();
    private Set<String> signups = new HashSet<>();
}
