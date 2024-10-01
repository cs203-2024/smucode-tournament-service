package com.cs203.smucode.mappers;

import com.cs203.smucode.dto.RoundDTO;
import com.cs203.smucode.models.Round;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.util.List;

@Mapper(componentModel = "spring", uses = {BracketMapper.class})
public interface RoundMapper {

    @Mapping(target = "brackets", source = "brackets")
    RoundDTO roundToRoundDTO(Round round);

    Round roundDTOToRound(RoundDTO roundDTO);

    List<RoundDTO> roundsToRoundDTOs(List<Round> rounds);

    List<Round> roundDTOsToRounds(List<RoundDTO> roundDTOs);
}
