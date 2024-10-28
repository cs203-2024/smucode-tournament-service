package com.cs203.smucode.dto;

import lombok.Data;

@Data
public class UserTournamentCardDTO extends TournamentCardDTO {

//    TODO: implement signedUp derivation logic
    private boolean signedUp;

//    TODO: implement participated derivation logic
    private boolean participated;

//    private int placing;

}
