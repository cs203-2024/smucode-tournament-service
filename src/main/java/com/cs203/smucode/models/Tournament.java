package com.cs203.smucode.models;

import com.cs203.smucode.constants.SignupStatus;
import com.cs203.smucode.constants.Status;
import com.cs203.smucode.converters.SignupStatusConverter;
import com.cs203.smucode.converters.StatusConverter;
import com.cs203.smucode.validation.WeightSum;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

import lombok.NoArgsConstructor;

/**
 * @author gav
 * @version 1.0
 * @since 27/8/2024
 */

@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name="tournaments")
@WeightSum
public class Tournament {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private UUID id;

    @Column(nullable = false, length = 255)
    private String name;

    @Column(columnDefinition = "TEXT")
    private String description; // e.g "Trees", "LinkedList", "Recursion"

//    TODO: change to enum
//    private String status; // e.g "active", "inactive", "suspended",

    @Column(name = "start_date", nullable = false)
    private LocalDateTime startDate;

    @Column(name = "end_date", nullable = false)
    private LocalDateTime endDate;

    @Column(name = "format", length = 50)
    private String format; // e.g., "single-elimination", "double-elimination", "round-robin"

    @Column(nullable = false)
    private int capacity;

    @Column(length = 255)
    private String icon;

    @ManyToOne
    @JoinColumn(name = "owner", referencedColumnName = "id")
    private User owner;

    @Column(name = "signup_deadline", nullable = false)
    private LocalDateTime signUpDeadline;

    @Column(name = "time_weight", nullable = false)
    private int timeWeight;

    @Column(name = "mem_Weight", nullable = false)
    private int memWeight;

    @Column(name = "test_case_weight", nullable = false)
    private int testCaseWeight;

    @Convert(converter = StatusConverter.class)
//    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Status status;

    @Column(name = "signup_start_date", nullable = false)
    private LocalDateTime signupStartDate;

    @Column(name = "signup_end_date", nullable = false)
    private LocalDateTime signupEndDate;

    @Convert(converter = SignupStatusConverter.class)
//    @Enumerated(EnumType.STRING)
    @Column(name = "signup_status", nullable = false)
    private SignupStatus signupStatus;

    @OneToMany(mappedBy = "tournament", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Round> rounds;

//    @OneToMany(mappedBy = "tournament", cascade = CascadeType.ALL, orphanRemoval = true)
//    List<Bracket> brackets;
}
