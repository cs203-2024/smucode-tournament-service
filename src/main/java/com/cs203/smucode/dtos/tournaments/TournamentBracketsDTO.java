package com.cs203.smucode.dtos.tournaments;

import com.cs203.smucode.dtos.rounds.RoundDTO;
import lombok.Data;

import java.util.ArrayList;
import java.util.List;

@Data
public class TournamentBracketsDTO {

    private List<RoundDTO> rounds = new ArrayList<>();

}
