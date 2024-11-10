package com.cs203.smucode.mappers;

import com.cs203.smucode.consumers.UserServiceConsumer;
import com.cs203.smucode.dtos.brackets.BracketDTO;
import com.cs203.smucode.dtos.brackets.UpdateBracketScoreDTO;
import com.cs203.smucode.dtos.users.BracketUserDTO;
import com.cs203.smucode.dtos.users.UserDTO;
import com.cs203.smucode.models.Bracket;
import org.mapstruct.Context;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


/**
 * @author jered
 * @version 1.0
 * @since 2024-09-04
 *
 * This class is used to map Brackets to its Data Transfer Objects (DTOs).
 */

@Mapper(componentModel = "spring")
public interface BracketMapper {
    static final Logger logger = LoggerFactory.getLogger(BracketMapper.class);

    // Default bracket DTO
    @Mapping(target = "player1", expression = "java(getPlayerDTO(bracket, 1, userServiceConsumer))")
    @Mapping(target = "player2", expression = "java(getPlayerDTO(bracket, 2, userServiceConsumer))")
    BracketDTO bracketToBracketDTO(Bracket bracket, @Context UserServiceConsumer userServiceConsumer);

    // Update bracket DTO
    @Mapping(target = "player1", expression = "java(getPlayerUsername(bracketDTO, 1))")
    @Mapping(target = "player2", expression = "java(getPlayerUsername(bracketDTO, 2))")
    @Mapping(target = "player1Score", expression = "java(getPlayerScore(bracketDTO, 1))")
    @Mapping(target = "player2Score", expression = "java(getPlayerScore(bracketDTO, 2))")
    Bracket updateBracketScoreDTOToBracket(UpdateBracketScoreDTO bracketDTO);

//    helper functions
    default BracketUserDTO getPlayerDTO(Bracket bracket,
                                        int playerNumber,
                                        UserServiceConsumer userServiceConsumer) {
        if (playerNumber == 1) {
            if (bracket.getPlayer1() == null) {
                return new BracketUserDTO();
            }

            logger.info("fetching user: {} from user service", bracket.getPlayer1());
            UserDTO player1 = userServiceConsumer.getUserById(bracket.getPlayer1());
            logger.info("fetched user: {} from user service", player1);


            BracketUserDTO bracketUserDTO = new BracketUserDTO();
            bracketUserDTO.setUsername(player1.username());
            bracketUserDTO.setImage(player1.profileImageUrl());
            bracketUserDTO.setScore(bracket.getPlayer1Score());
            bracketUserDTO.setWinProbability(bracket.getPlayer1WinProbability());

            return bracketUserDTO;
        }
        if (playerNumber == 2) {
            if (bracket.getPlayer2() == null) {
                return new BracketUserDTO();
            }

            UserDTO player2 = userServiceConsumer.getUserById(bracket.getPlayer2());

            BracketUserDTO bracketUserDTO = new BracketUserDTO();
            bracketUserDTO.setUsername(player2.username());
            bracketUserDTO.setImage(player2.profileImageUrl());
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
