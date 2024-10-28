package com.cs203.smucode.dto;

import lombok.Data;

@Data
public class UserTournamentCardDTO extends TournamentCardDTO {

//    TODO: implement signedUp derivation logic
    private boolean signedUp;

//    TODO: implement participated derivation logic
    private boolean participated;

//    TODO: implement placing derivation logic (eg. eliminated in Round of 16, champion)
    private int placing;

}
