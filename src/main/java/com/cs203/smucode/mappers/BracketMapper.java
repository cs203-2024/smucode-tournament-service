package com.cs203.smucode.mappers;

import com.cs203.smucode.dto.BracketDTO;
import com.cs203.smucode.dto.UpdateBracketScoreDTO;
import com.cs203.smucode.dto.BracketUserDTO;
import com.cs203.smucode.models.Bracket;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;


/**
 * @author jered
 * @version 1.0
 * @since 2024-09-04
 *
 * This class is used to map Brackets to its Data Transfer Objects (DTOs).
 */

@Mapper(componentModel = "spring")
public interface BracketMapper {

    // Default bracket DTO
    @Mapping(target = "player1", expression = "java(getPlayerDTO(bracket, 1))")
    @Mapping(target = "player2", expression = "java(getPlayerDTO(bracket, 2))")
    BracketDTO bracketToBracketDTO(Bracket bracket);

    @Mapping(target = "player1", expression = "java(getPlayerUsername(bracketDTO, 1))")
    @Mapping(target = "player2", expression = "java(getPlayerUsername(bracketDTO, 2))")
    @Mapping(target = "player1Score", expression = "java(getPlayerScore(bracketDTO, 1))")
    @Mapping(target = "player2Score", expression = "java(getPlayerScore(bracketDTO, 2))")
    Bracket bracketDTOToBracket(BracketDTO bracketDTO);

    // Update bracket DTO
    @Mapping(target = "player1", expression = "java(getPlayerUsername(bracketDTO, 1))")
    @Mapping(target = "player2", expression = "java(getPlayerUsername(bracketDTO, 2))")
    @Mapping(target = "player1Score", expression = "java(getPlayerScore(bracketDTO, 1))")
    @Mapping(target = "player2Score", expression = "java(getPlayerScore(bracketDTO, 2))")
    Bracket updateBracketScoreDTOToBracket(UpdateBracketScoreDTO bracketDTO);

//    helper functions
//    TODO: refactor when user client set up
    default BracketUserDTO getPlayerDTO(Bracket bracket, int playerNumber) {
        if (playerNumber == 1) {
            BracketUserDTO bracketUserDTO = new BracketUserDTO();
            bracketUserDTO.setUsername(bracket.getPlayer1());
//        set user icon
            bracketUserDTO.setScore(bracket.getPlayer1Score());
            bracketUserDTO.setWinProbability(bracket.getPlayer1WinProbability());
            return bracketUserDTO;
        }
        if (playerNumber == 2) {
            BracketUserDTO bracketUserDTO = new BracketUserDTO();
            bracketUserDTO.setUsername(bracket.getPlayer2());
//        set user icon
            bracketUserDTO.setScore(bracket.getPlayer2Score());
            bracketUserDTO.setWinProbability(bracket.getPlayer2WinProbability());
            return bracketUserDTO;
        }

        return null;
    }

    default String getPlayerUsername(BracketDTO bracketDTO, int playerNumber) {
        if (playerNumber == 1) {
            return bracketDTO.getPlayer1().getUsername();
        }
        if (playerNumber == 2) {
            return bracketDTO.getPlayer2().getUsername();
        }
        return null;
    }

    default int getPlayerScore(BracketDTO bracketDTO, int playerNumber) {
        if (playerNumber == 1) {
            return bracketDTO.getPlayer1().getScore();
        }
        if (playerNumber == 2) {
            return bracketDTO.getPlayer2().getScore();
        }
        return 0;
    }

    // TODO: clean this up
    default String getPlayerUsername(UpdateBracketScoreDTO bracketDTO, int playerNumber) {
        if (playerNumber == 1) {
            return bracketDTO.getPlayer1().getId();
        }
        if (playerNumber == 2) {
            return bracketDTO.getPlayer2().getId();
        }
        return null;
    }

    default int getPlayerScore(UpdateBracketScoreDTO bracketDTO, int playerNumber) {
        if (playerNumber == 1) {
            return bracketDTO.getPlayer1().getScore();
        }
        if (playerNumber == 2) {
            return bracketDTO.getPlayer2().getScore();
        }
        return 0;
    }

}
