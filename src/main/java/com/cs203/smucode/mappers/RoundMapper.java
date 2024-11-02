package com.cs203.smucode.mappers;

import com.cs203.smucode.dto.RoundDTO;
import com.cs203.smucode.models.Round;
import org.mapstruct.Mapper;

/**
 * @author jered
 * @version 1.0
 * @since 2024-09-04
 *
 * This class is used to map Round to its Data Transfer Objects (DTOs).
 */

@Mapper(componentModel = "spring", uses = {BracketMapper.class})
public interface RoundMapper {

    RoundDTO roundToRoundDTO(Round round);

    Round roundDTOToRound(RoundDTO roundDTO);

}
