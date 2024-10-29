package com.cs203.smucode.dto;

import com.cs203.smucode.models.PlayerInfo;
import lombok.Data;

import java.util.ArrayList;
import java.util.List;

@Data
public class UpdateBracketScoreDTO {

    private List<PlayerInfo> players = new ArrayList<>();

}
