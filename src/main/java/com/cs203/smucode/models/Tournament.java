package com.cs203.smucode.models;

import lombok.Data;
import lombok.AllArgsConstructor; // Subject to change
import java.time.LocalDateTime;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

/**
 * @author gav
 * @version 1.0
 * @since 27/8/2024
 */
@Data
@AllArgsConstructor
@Document(collection = "tournaments")
public class Tournament {
    @Id
    private Long id;
    private String name; 
    private String description; // e.g "Trees", "LinkedList", "Recursion"
    private String status; // e.g "active", "inactive", "suspended",
    private LocalDateTime startDate;
    private LocalDateTime endDate;
    private String format; // e.g., "single-elimination", "double-elimination", "round-robin"
}
