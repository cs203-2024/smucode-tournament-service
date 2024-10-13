package com.cs203.smucode.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class UserBracketDTO {

    private String username;

    private String icon;

    private int score;

}
