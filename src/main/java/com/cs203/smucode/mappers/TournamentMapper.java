package com.cs203.smucode.mappers;

import com.cs203.smucode.consumers.UserServiceConsumer;
import com.cs203.smucode.dtos.tournaments.*;
import com.cs203.smucode.dtos.users.ParticipantUserDTO;
import com.cs203.smucode.dtos.users.TournamentParticipantsDTO;
import com.cs203.smucode.models.Tournament;
import org.mapstruct.Context;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * @author jered
 * @version 1.0
 * @since 2024-09-04
 *
 * This class is used to map tournament to its Data Transfer Objects (DTOs).
 */

@Mapper(componentModel="spring", uses={RoundMapper.class})
public interface TournamentMapper {
    static final Logger logger = LoggerFactory.getLogger(TournamentMapper.class);

    //    TournamentCardDTO
    @Mapping(target = "numberOfSignups", expression = "java(tournament.getSignups().size())")
    @Mapping(target = "signupsOpen", expression = "java(getSignupsOpen(tournament))")
    AdminTournamentCardDTO tournamentToAdminTournamentCardDTO(Tournament tournament);
    List<AdminTournamentCardDTO> tournamentsToAdminTournamentCardDTOs(List<Tournament> tournaments);

    @Mapping(target = "numberOfSignups", expression = "java(tournament.getSignups().size())")
    @Mapping(target = "signupsOpen", expression = "java(getSignupsOpen(tournament))")
    @Mapping(target = "signedUp", ignore = true) // We will set signedUp manually
    @Mapping(target = "participated", ignore = true) // We will set participated manually
    UserTournamentCardDTO mapTournamentToUserTournamentCardDTO(Tournament tournament);

    // TournamentDTO
    @Mapping(target = "numberOfSignups", expression = "java(tournament.getSignups().size())")
    @Mapping(target = "signupsOpen", expression = "java(getSignupsOpen(tournament))")
    AdminTournamentDTO tournamentToAdminTournamentDTO(Tournament tournament);

    @Mapping(target = "numberOfSignups", expression = "java(tournament.getSignups().size())")
    @Mapping(target = "signupsOpen", expression = "java(getSignupsOpen(tournament))")
    @Mapping(target = "signedUp", ignore = true) // We will set signedUp manually
    UserTournamentDTO mapTournamentToUserTournamentDTO(Tournament tournament);

    // TournamentBracketsDTO
    TournamentBracketsDTO tournamentToTournamentBracketsDTO(Tournament tournament);

    // TournamentParticipantsDTO
    @Mapping(target = "participants", expression = "java(getParticipants(tournament, userServiceConsumer))")
    TournamentParticipantsDTO tournamentToTournamentParticipantsDTO(Tournament tournament,
                                                                    @Context UserServiceConsumer userServiceConsumer);

    // DetailedTournamentDTO
    DetailedTournamentDTO tournamentToDetailedTournamentDTO(Tournament tournament);
    Tournament detailedTournamentDTOToTournament(DetailedTournamentDTO detailedTournamentDTO);

//    helper functions

    // Custom method to handle multiple parameters - to derive signedUp and participated for UserTournamentCardDTO
    default UserTournamentCardDTO tournamentToUserTournamentCardDTO(Tournament tournament, String username) {
        // Use the MapStruct-generated mapping method for the rest of the fields
        UserTournamentCardDTO dto = mapTournamentToUserTournamentCardDTO(tournament);

        // Manually set "signedUp" field
        dto.setSignedUp(tournament.getSignups().contains(username));

        // Manually set "participated" field
        dto.setParticipated(tournament.getParticipants().contains(username));

        return dto;
    }

    default List<UserTournamentCardDTO> tournamentsToUserTournamentCardDTOs(List<Tournament> tournaments, String username) {
        List<UserTournamentCardDTO> dtos = new ArrayList<>();
        for (Tournament tournament : tournaments) {
            dtos.add(tournamentToUserTournamentCardDTO(tournament, username));
        }
        return dtos;
    }

    // Custom method to handle multiple parameters - to derive signedUp and participated for UserTournamentDTO
    default UserTournamentDTO tournamentToUserTournamentDTO(Tournament tournament,
                                                            String username) {
        // Use the MapStruct-generated mapping method for the rest of the fields
        UserTournamentDTO dto = mapTournamentToUserTournamentDTO(tournament);

        // Manually set "signedUp" field
        dto.setSignedUp(tournament.getSignups().contains(username));

        return dto;
    }

    default List<UserTournamentDTO> tournamentsToUserTournamentDTOs(List<Tournament> tournaments,
                                                                    String username) {
        List<UserTournamentDTO> dtos = new ArrayList<>();
        for (Tournament tournament : tournaments) {
            dtos.add(tournamentToUserTournamentDTO(tournament, username));
        }
        return dtos;
    }

    // Get participant information
    default Set<ParticipantUserDTO> getParticipants(Tournament tournament,
                                                    @Context UserServiceConsumer userServiceConsumer) {
        Set<String> participants = tournament.getParticipants();
        Set<ParticipantUserDTO> dtos = new HashSet<>();
        for (String participant : participants) {
            ParticipantUserDTO dto = new ParticipantUserDTO();
            dto.setUsername(participant);
            dto.setProfileImageUrl(userServiceConsumer.getUserById(participant).profileImageUrl());
            dtos.add(dto);
        }

        return dtos;
    }


    // Derive signup status
    default boolean getSignupsOpen(Tournament tournament) {
        logger.info("current time: {}", LocalDateTime.now());
        logger.info("signup end date: {}", tournament.getSignupEndDate());
        logger.info("signups open?: {}", LocalDateTime.now().isBefore(tournament.getSignupEndDate()));
        return LocalDateTime.now().isBefore(tournament.getSignupEndDate());
    }
}
