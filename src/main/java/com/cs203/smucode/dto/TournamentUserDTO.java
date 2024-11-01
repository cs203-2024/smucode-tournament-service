package com.cs203.smucode.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class TournamentUserDTO {

    private String username;

    private String profileImageUrl;
}
