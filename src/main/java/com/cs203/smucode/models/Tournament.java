package com.cs203.smucode.models;

import com.cs203.smucode.dto.UserDTO;
import lombok.AllArgsConstructor;
import lombok.Data;

import java.math.BigDecimal;
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
    private String id;

    private String name; 
    private String description; // e.g "Trees", "LinkedList", "Recursion"
    private String status; // e.g "active", "inactive", "suspended",
    private LocalDateTime startDate;
    private LocalDateTime endDate;
    private String format; // e.g., "single-elimination", "double-elimination", "round-robin"
    private int capacity;
    private String icon;
    private UserDTO owner;
    private LocalDateTime signUpDeadline;
    private BigDecimal timeWeight;
    private BigDecimal memWeight;
    private BigDecimal testCaseWeight;

//    public Tournament(String name, String description, String status, LocalDateTime startDate, LocalDateTime endDate, String format) {
//        this.name = name;
//        this.description = description;
//        this.status = status;
//        this.startDate = startDate;
//        this.endDate = endDate;
//        this.format = format;
//    }
}
