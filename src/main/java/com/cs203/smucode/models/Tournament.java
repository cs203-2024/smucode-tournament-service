package com.cs203.smucode.models;

import lombok.Data;
import java.time.LocalDateTime;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

/**
 * @author gav
 * @version 1.0
 * @since 27/8/2024
 */

@Data
@Document(collection = "tournaments")
public class Tournament {
    @Id
    private String id;

    private String name; 
    private String description; // e.g "Trees", "LinkedList", "Recursion"
    private String status; // e.g "active", "inactive", "suspended",
    private LocalDateTime startDate;
    private LocalDateTime endDate;
    private String format; // e.g., "single-elimination", "double-elimination", "round-robin"

    public Tournament(String name, String description, String status, LocalDateTime startDate, LocalDateTime endDate, String format) {
        this.name = name;
        this.description = description;
        this.status = status;
        this.startDate = startDate;
        this.endDate = endDate;
        this.format = format;
    }
}
