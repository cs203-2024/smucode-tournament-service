package com.cs203.smucode.mappers;

import com.cs203.smucode.consumers.UserServiceConsumer;
import com.cs203.smucode.dtos.rounds.RoundDTO;
import com.cs203.smucode.models.Round;
import org.mapstruct.Context;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

/**
 * @author jered
 * @version 1.0
 * @since 2024-09-04
 *
 * This class is used to map Round to its Data Transfer Objects (DTOs).
 */

@Mapper(componentModel = "spring", uses = {BracketMapper.class})
public interface RoundMapper {

    @Mapping(target = "brackets", source = "brackets")
    RoundDTO roundToRoundDTO(Round round, @Context UserServiceConsumer userServiceConsumer);

    @Mapping(target = "brackets", ignore = true)
    Round roundDTOToRound(RoundDTO roundDTO);

}
