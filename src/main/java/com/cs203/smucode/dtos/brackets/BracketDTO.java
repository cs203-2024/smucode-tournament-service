package com.cs203.smucode.dtos.brackets;

import com.cs203.smucode.dtos.users.BracketUserDTO;
import lombok.Data;

@Data
public class BracketDTO {

    private String id;

    private int seqId;

    private String status;

    private BracketUserDTO player1;

    private BracketUserDTO player2;

    private String winner;

}
