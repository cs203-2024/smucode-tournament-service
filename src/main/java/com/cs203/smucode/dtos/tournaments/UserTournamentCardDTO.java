package com.cs203.smucode.dtos.tournaments;

import lombok.Data;

@Data
public class UserTournamentCardDTO extends TournamentCardDTO {

    private boolean signedUp;

    private boolean participated;

//    TODO: implement placing derivation logic (eg. "Eliminated in Round of 16", "Champion", etc)
    private int placing;

}
