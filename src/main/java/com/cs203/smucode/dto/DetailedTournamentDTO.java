package com.cs203.smucode.dto;

import com.cs203.smucode.validation.PowerOfTwo;
import com.cs203.smucode.validation.WeightSum;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.Set;

@Data
//@WeightSum
// TODO: add rest of Bean validation
public class DetailedTournamentDTO {

    @NotNull
    private String name;

    private String format;

    @PowerOfTwo
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

    private String organiser;

    private String currentRound;

    private Set<String> signups;
}
