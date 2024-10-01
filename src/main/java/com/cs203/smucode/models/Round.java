package com.cs203.smucode.models;

import com.cs203.smucode.constants.Status;
import com.cs203.smucode.converters.StatusConverter;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity(name = "rounds")
public class Round {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private UUID id;

    @Column(nullable = false)
    private String name;

    @Column(name = "start_date")
    // TODO: check that startDate and endDate within tournament startDate and endDate
    private LocalDateTime startDate;

    @Column(name = "end_date")
    private LocalDateTime endDate;

    @Convert(converter = StatusConverter.class)
    @Column(name = "status", nullable = false, insertable = false)
    private Status status;

    @OneToMany(mappedBy = "round", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Bracket> brackets;

//    @Column(name = "tournament_id", nullable = false)
//    private UUID tournamentId;

    @ManyToOne
    @JoinColumn(name = "tournament_id", nullable = false)
    private Tournament tournament;

}
