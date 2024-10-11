package com.cs203.smucode.models;

import com.cs203.smucode.constants.Band;
import com.cs203.smucode.constants.Status;
import com.cs203.smucode.converters.BandConverter;
import com.cs203.smucode.converters.StatusConverter;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
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
public class Tournament {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private UUID id;

    @Column(nullable = false)
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

    @Column(name = "icon")
    private String icon;

    @Column(name = "organiser", nullable = false)
    private String organiser;

    @Column(name = "time_weight", nullable = false)
    private int timeWeight;

    @Column(name = "mem_Weight", nullable = false)
    private int memWeight;

    @Column(name = "test_case_weight", nullable = false)
    private int testCaseWeight;

    @Convert(converter = StatusConverter.class)
    @Column(nullable = false)
    private Status status;

    @Column(name = "signup_start_date", nullable = false)
    private LocalDateTime signupStartDate;

    @Column(name = "signup_end_date", nullable = false)
    private LocalDateTime signupEndDate;

//    @Convert(converter = SignupStatusConverter.class)
//    @Column(name = "signup_status", nullable = false)
//    private SignupStatus signupStatus;

    @Convert(converter = BandConverter.class)
    @Column(name = "band")
    private Band band;

    @Column(name = "current_round")
    private String currentRound;

    @OneToMany(mappedBy = "tournament", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Round> rounds;

    @ElementCollection
    @CollectionTable(
            name = "tournament_signups",
            joinColumns = @JoinColumn(name = "tournament_id")
    )
    @Column(name = "signup")
    private Set<String> signups = new HashSet<>();

    @ElementCollection
    @CollectionTable(
            name = "tournament_participants",
            joinColumns = @JoinColumn(name = "tournament_id")
    )
    @Column(name = "participant")
    private Set<String> participants = new HashSet<>();
}
