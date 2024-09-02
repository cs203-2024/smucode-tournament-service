package com.cs203.smucode.models;

import lombok.Data;
import lombok.AllArgsConstructor; // Subject to change
import java.time.LocalDateTime;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;

/**
 * @author gav
 * @version 1.0
 * @since 27/8/2024
 */
@Data
@AllArgsConstructor
@Entity
public class Tournament {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String name; 
    private String description; // e.g "Trees", "LinkedList", "Recursion"
    private String status; // e.g "active", "inactive", "suspended",
    private LocalDateTime startDate;
    private LocalDateTime endDate;
    private String format; // e.g., "single-elimination", "double-elimination", "round-robin"
}
