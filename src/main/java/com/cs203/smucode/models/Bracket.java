package com.cs203.smucode.models;

import com.cs203.smucode.constants.Status;
import com.cs203.smucode.converters.StatusConverter;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

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
    @Column(name = "status", nullable = false, insertable = false)
    private Status status;

//    @ManyToOne
//    @JoinColumn(name = "winner")
    @Column(name = "winner")
    private String winner;

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

    @ElementCollection
    @CollectionTable(
            name = "bracket_players",
            joinColumns = @JoinColumn(name = "bracket_id")
    )
    private List<PlayerInfo> players = new ArrayList<>();
}
