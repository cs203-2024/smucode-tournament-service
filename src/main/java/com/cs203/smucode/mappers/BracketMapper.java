package com.cs203.smucode.mappers;

import com.cs203.smucode.dto.BracketDTO;
import com.cs203.smucode.dto.UserDTO;
import com.cs203.smucode.models.Bracket;
import com.cs203.smucode.models.User;
import org.mapstruct.Context;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.util.List;
import java.util.Set;

@Mapper(componentModel = "spring", uses = {UserMapper.class})
public interface BracketMapper {

//    @Mapping(target = "player1", expression = "java(getPlayer1(bracket.getPlayers(), userMapper))")
//    @Mapping(target = "player2", expression = "java(getPlayer2(bracket.getPlayers(), userMapper))")
//    BracketDTO bracketToBracketDTO(Bracket bracket, @Context UserMapper userMapper);

    @Mapping(target = "players", source = "players")
    BracketDTO bracketToBracketDTO(Bracket bracket);

    List<BracketDTO> bracketsToBracketDTOs(List<Bracket> brackets);

//    Bracket bracketDTOToBracket(BracketDTO bracketDTO);

//    List<BracketDTO> bracketsToBracketDTOs(List<Bracket> brackets, @Context UserMapper userMapper);

//    List<Bracket> bracketDTOsToBrackets(List<BracketDTO> bracketDTOs);

//    default UserDTO getPlayer1(Set<User> players, UserMapper userMapper) {
//        User player1 = players.stream().findFirst().orElse(null);
//        System.out.println("--------------------------------------");
//        System.out.println(player1);
//        System.out.println("--------------------------------------");
//
//        return players != null && !players.isEmpty() ? players.stream().findFirst().map(userMapper::userToUserDTO).orElse(null) : null;
//    }
//
//    default UserDTO getPlayer2(Set<User> players, UserMapper userMapper) {
//        return players != null && players.size() >= 2 ? players.stream().skip(1).findFirst().map(userMapper::userToUserDTO).orElse(null) : null;
//    }

}
