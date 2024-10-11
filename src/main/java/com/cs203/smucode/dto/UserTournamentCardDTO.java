package com.cs203.smucode.dto;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class UserTournamentCardDTO extends TournamentCardDTO{

    private boolean signedUp;
    private boolean participated;
    private boolean signupsOpen;
    private int placing;

}
