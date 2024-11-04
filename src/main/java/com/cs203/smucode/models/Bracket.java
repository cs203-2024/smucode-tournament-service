package com.cs203.smucode.models;

import com.cs203.smucode.constants.Status;
import com.cs203.smucode.converters.StatusConverter;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity(name="brackets")
public class Bracket {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private UUID id;

    @Column(name = "seq_id")
    private int seqId;

    @Convert(converter = StatusConverter.class)
//    @Column(name = "status", nullable = false, insertable = false)
    @Column(name = "status", nullable = false)
    private Status status;

    @Column(name = "player1")
    private String player1;

    @Column(name = "player1_score", nullable = false)
    private int player1Score;

    @Column(name = "player1_win_probability")
    private Double player1WinProbability = 0.0;

    @Column(name = "player2")
    private String player2;

    @Column(name = "player2_score", nullable = false)
    private int player2Score;

    @Column(name = "player2_win_probability")
    private Double player2WinProbability = 0.0;
//    @ManyToOne
//    @JoinColumn(name = "winner")
    @Column(name = "winner")
    private String winner;

    @ToString.Exclude
    @ManyToOne
    @JoinColumn(name = "round_id", nullable = false)
    private Round round;

    @ToString.Exclude
    @ManyToOne
    @JoinColumn(name = "tournament_id", nullable = false)
    private Tournament tournament;

}
