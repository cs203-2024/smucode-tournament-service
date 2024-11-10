package com.cs203.smucode.dtos.rounds;

import com.cs203.smucode.dtos.brackets.BracketDTO;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

@Data
public class RoundDTO {

    private String id;

    private int seqId;

    private String name;

    private LocalDateTime startDate;

    private LocalDateTime endDate;

    private String status;

    private List<BracketDTO> brackets;

}
