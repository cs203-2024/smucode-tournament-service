package com.cs203.smucode.dtos.tournaments;

import lombok.Data;

@Data
public class UserTournamentDTO extends TournamentDTO {

    private boolean signedUp;

    private boolean participated;

}
