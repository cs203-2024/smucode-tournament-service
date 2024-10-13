package com.cs203.smucode.dto;

import lombok.Data;

@Data
public class UserTournamentCardDTO extends TournamentCardDTO {

    private boolean signedUp;

    private boolean participated;

    private int placing;

}
