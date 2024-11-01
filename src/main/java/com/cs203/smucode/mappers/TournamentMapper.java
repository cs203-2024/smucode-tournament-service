package com.cs203.smucode.mappers;

import com.cs203.smucode.constants.SignupStatus;
import com.cs203.smucode.consumers.UserServiceConsumer;
import com.cs203.smucode.dto.*;
import com.cs203.smucode.handlers.UserServiceHandler;
import com.cs203.smucode.models.Tournament;
import com.netflix.discovery.converters.Auto;
import org.mapstruct.Context;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

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
    Tournament adminTournamentCardDTOToTournament(AdminTournamentCardDTO adminTournamentCardDTO);
    List<AdminTournamentCardDTO> tournamentsToAdminTournamentCardDTOs(List<Tournament> tournaments);
    List<Tournament> adminTournamentCardDTOsToTournaments(List<AdminTournamentCardDTO> adminTournamentCardDTOs);

    @Mapping(target = "numberOfSignups", expression = "java(tournament.getSignups().size())")
    @Mapping(target = "signupsOpen", expression = "java(getSignupsOpen(tournament))")
    @Mapping(target = "signedUp", ignore = true) // We will set signedUp manually
    @Mapping(target = "participated", ignore = true) // We will set participated manually
    UserTournamentCardDTO mapTournamentToUserTournamentCardDTO(Tournament tournament);
    Tournament userTournamentCardDTOToTournament(UserTournamentCardDTO userTournamentCardDTO);
    List<UserTournamentCardDTO> mapTournamentsToUserTournamentCardDTOs(List<Tournament> tournaments);
    List<Tournament> userTournamentCardDTOsToTournaments(List<UserTournamentCardDTO> userTournamentCardDTOs);

//    TournamentDTO
    @Mapping(target = "numberOfSignups", expression = "java(tournament.getSignups().size())")
    @Mapping(target = "signupsOpen", expression = "java(getSignupsOpen(tournament))")
    @Mapping(target = "participants", expression = "java(getParticipants(tournament, userServiceConsumer))")
    AdminTournamentDTO tournamentToAdminTournamentDTO(Tournament tournament, @Context UserServiceConsumer userServiceConsumer);
//    Tournament adminTournamentDTOToTournament(AdminTournamentDTO adminTournamentDTO);
//    List<AdminTournamentDTO> tournamentsToAdminTournamentDTOs(List<Tournament> tournaments);
//    List<Tournament> adminTournamentDTOsToTournaments(List<AdminTournamentDTO> adminTournamentDTOs);

    @Mapping(target = "numberOfSignups", expression = "java(tournament.getSignups().size())")
    @Mapping(target = "signupsOpen", expression = "java(getSignupsOpen(tournament))")
    @Mapping(target = "signedUp", ignore = true) // We will set signedUp manually
    @Mapping(target = "participants", expression = "java(getParticipants(tournament, userServiceConsumer))")
    UserTournamentDTO mapTournamentToUserTournamentDTO(Tournament tournament, @Context UserServiceConsumer userServiceConsumer);
//    Tournament userTournamentDTOToTournament(UserTournamentDTO userTournamentDTO);
//    List<UserTournamentDTO> mapTournamentsToUserTournamentDTOs(List<Tournament> tournaments);
//    List<Tournament> userTournamentDTOsToTournaments(List<UserTournamentDTO> userTournamentCardDTOs);

//    TournamentBracketsDTO
    TournamentBracketsDTO tournamentToTournamentBracketsDTO(Tournament tournament);
    Tournament tournamentBracketsDTOToTournament(TournamentBracketsDTO tournamentBracketsDTO);
    List<TournamentBracketsDTO> tournamentsToTournamentBracketsDTOs(List<Tournament> tournaments);
    List<Tournament> tournamentBracketDTOsToTournaments(List<TournamentBracketsDTO> tournamentBracketDTOs);

//    DetailedTournamentDTO
    DetailedTournamentDTO tournamentToDetailedTournamentDTO(Tournament tournament);
    Tournament detailedTournamentDTOToTournament(DetailedTournamentDTO detailedTournamentDTO);
    List<DetailedTournamentDTO> tournamentsToDetailedTournamentDTOs(List<Tournament> tournaments);
//    List<Tournament> tournamentDTOsToCreateTournaments(List<TournamentDTO> tournamentDTOs);

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
                                                            String username,
                                                            @Context UserServiceConsumer userServiceConsumer) {
        // Use the MapStruct-generated mapping method for the rest of the fields
        UserTournamentDTO dto = mapTournamentToUserTournamentDTO(tournament, userServiceConsumer);

        // Manually set "signedUp" field
        dto.setSignedUp(tournament.getSignups().contains(username));

        return dto;
    }

    default List<UserTournamentDTO> tournamentsToUserTournamentDTOs(List<Tournament> tournaments,
                                                                    String username,
                                                                    @Context UserServiceConsumer userServiceConsumer) {
        List<UserTournamentDTO> dtos = new ArrayList<>();
        for (Tournament tournament : tournaments) {
            dtos.add(tournamentToUserTournamentDTO(tournament, username, userServiceConsumer));
        }
        return dtos;
    }

    // Get participant information
    default Set<TournamentUserDTO> getParticipants(Tournament tournament,
                                                  @Context UserServiceConsumer userServiceConsumer) {
        Set<String> participants = tournament.getParticipants();
        Set<TournamentUserDTO> dtos = new HashSet<>();
        for (String participant : participants) {
            TournamentUserDTO dto = new TournamentUserDTO();
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
