package com.cs203.smucode.models;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@Document(collection = "rounds")
public class Round {
    private String id;

    @NotNull
    // TODO: check that tounamentId exists
    private String tournamentId;
    private String name;
    // TODO: check that startDate and endDate within tournament startDate and endDate
    private LocalDateTime startDate;
    private LocalDateTime endDate;
}
