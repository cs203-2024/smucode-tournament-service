package com.cs203.smucode.models;

import com.cs203.smucode.dto.UserDTO;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import org.springframework.data.mongodb.core.mapping.Document;

@Data
@AllArgsConstructor
@Document(collection = "brackets")
public class Bracket {
    private String id;

    private UserDTO player1; // TODO: how to differentiate player from owner (use dtos? or separate entities)
    private UserDTO player2;
    @NotNull
    // TODO: check if roundId exists
    private String roundId;
}
