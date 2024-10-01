package com.cs203.smucode.mappers;

import com.cs203.smucode.dto.BracketDTO;
import com.cs203.smucode.models.Bracket;
import org.mapstruct.Mapper;

import java.util.List;

@Mapper(componentModel = "spring")
public interface BracketMapper {

    BracketDTO bracketToBracketDTO(Bracket bracket);

    List<BracketDTO> bracketsToBracketDTOs(List<Bracket> brackets);

    Bracket bracketDTOToBracket(BracketDTO bracketDTO);

    List<Bracket> bracketDTOsToBrackets(List<BracketDTO> bracketDTOs);

}
