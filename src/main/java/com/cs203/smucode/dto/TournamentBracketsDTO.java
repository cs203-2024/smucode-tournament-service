package com.cs203.smucode.dto;

import lombok.Data;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Data
public class TournamentBracketsDTO {

    private List<RoundDTO> rounds = new ArrayList<>();

//    private Set<String> signups = new HashSet<>();

}
