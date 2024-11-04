package com.cs203.smucode.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class BracketUserDTO {

    private String username;

    private String image;

    private int score;

    private Double winProbability;

}
