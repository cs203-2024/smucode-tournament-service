package com.cs203.smucode.dto;

import lombok.Data;

import java.util.List;

@Data
public class BracketDTO {
    private String id;
    private int seqId;
    private String status;
    private List<String> playerIds;
    private String winner;
}
