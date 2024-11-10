package com.cs203.smucode.mappers;

import com.cs203.smucode.consumers.UserServiceConsumer;
import com.cs203.smucode.dtos.brackets.BracketDTO;
import com.cs203.smucode.dtos.rounds.DetailedRoundDTO;
import com.cs203.smucode.dtos.rounds.RoundDTO;
import com.cs203.smucode.models.Bracket;
import com.cs203.smucode.models.Round;
import org.mapstruct.Context;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;

import java.util.List;
import java.util.stream.Collectors;

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
