package com.cs203.smucode.models;

import com.cs203.smucode.constants.Status;
import com.cs203.smucode.converters.StatusConverter;
import com.cs203.smucode.dto.UserBracketDTO;
import com.cs203.smucode.dto.UserDTO;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

import java.util.ArrayList;
import java.util.List;
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

    @Column(name = "player2")
    private String player2;

    @Column(name = "player2_score", nullable = false)
    private int player2Score;

//    @ManyToOne
//    @JoinColumn(name = "winner")
    @Column(name = "winner")
    private String winner;

    @ToString.Exclude
    @ManyToOne
    @JoinColumn(name = "round_id", nullable = false)
    private Round round;

//    @ManyToMany(cascade = CascadeType.ALL)
//    @JoinTable(
//            name = "bracket_players",
//            joinColumns = @JoinColumn(name = "bracket_id"),
//            inverseJoinColumns = @JoinColumn(name = "player_id")
//    )
//    private List<User> players = new ArrayList<>();

//    @ElementCollection
//    @CollectionTable(
//            name = "bracket_players",
//            joinColumns = @JoinColumn(name = "bracket_id")
//    )
//    private List<PlayerInfo> players = new ArrayList<>();
}
