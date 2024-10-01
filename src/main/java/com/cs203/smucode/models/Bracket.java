package com.cs203.smucode.models;

import com.cs203.smucode.constants.Status;
import com.cs203.smucode.converters.StatusConverter;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.Set;
import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity(name="brackets")
public class Bracket {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private UUID id;

    @ManyToOne
    @JoinColumn(name = "round_id", nullable = false)
    private Round round;

    @Convert(converter = StatusConverter.class)
    @Column(nullable = false)
    private Status status;

    @ManyToMany
    @JoinTable(
            name = "bracket_players",
            joinColumns = @JoinColumn(name = "bracket_id"),
            inverseJoinColumns = @JoinColumn(name = "player_id")
    )
    private List<User> players;
//    private User player1;
//    private User player2;

    @ManyToOne
    @JoinColumn(name = "winner")
    private User winner;
}