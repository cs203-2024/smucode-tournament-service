package com.cs203.smucode.dto;

import lombok.Data;

import java.util.List;
import java.util.UUID;

@Data
public class BracketScoreDTO {
    private List<UUID> playerIds;
}
