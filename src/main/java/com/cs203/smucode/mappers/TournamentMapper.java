package com.cs203.smucode.mappers;

import com.cs203.smucode.constants.SignupStatus;
import com.cs203.smucode.dto.*;
import com.cs203.smucode.models.Tournament;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * @author jered
 * @version 1.0
 * @since 2024-09-04
 *
 * This class is used to map tournament to its Data Transfer Objects (DTOs).
 */

@Mapper(componentModel="spring", uses={RoundMapper.class})
public interface TournamentMapper {

//    TournamentCardDTO
    @Mapping(target = "numberOfSignups", expression = "java(tournament.getSignups().size())")
    @Mapping(target = "signupStatus", expression = "java(getSignupStatus(tournament))")
    AdminTournamentCardDTO tournamentToAdminTournamentCardDTO(Tournament tournament);
    Tournament adminTournamentCardDTOToTournament(AdminTournamentCardDTO adminTournamentCardDTO);
    List<AdminTournamentCardDTO> tournamentsToAdminTournamentCardDTOs(List<Tournament> tournaments);
    List<Tournament> adminTournamentCardDTOsToTournaments(List<AdminTournamentCardDTO> adminTournamentCardDTOs);

    @Mapping(target = "numberOfSignups", expression = "java(tournament.getSignups().size())")
    @Mapping(target = "signedUp", ignore = true) // We will set signedUp manually
    @Mapping(target = "participated", ignore = true) // We will set participated manually
    UserTournamentCardDTO mapTournamentToUserTournamentCardDTO(Tournament tournament);
    Tournament userTournamentCardDTOToTournament(UserTournamentCardDTO userTournamentCardDTO);
    List<UserTournamentCardDTO> mapTournamentsToUserTournamentCardDTOs(List<Tournament> tournaments);
    List<Tournament> userTournamentCardDTOsToTournaments(List<UserTournamentCardDTO> userTournamentCardDTOs);

//    TournamentDTO
    @Mapping(target = "numberOfSignups", expression = "java(tournament.getSignups().size())")
    TournamentDTO tournamentToTournamentDTO(Tournament tournament);
    Tournament tournamentDTOToTournament(TournamentDTO tournamentDTO);
    List<TournamentDTO> tournamentsToTournamentDTOs(List<Tournament> tournaments);
    List<Tournament> tournamentDTOsToTournaments(List<TournamentDTO> tournamentDTOs);

    @Mapping(target = "numberOfSignups", expression = "java(tournament.getSignups().size())")
    @Mapping(target = "signupStatus", expression = "java(getSignupStatus(tournament))")
    AdminTournamentDTO tournamentToAdminTournamentDTO(Tournament tournament);
    Tournament adminTournamentDTOToTournament(AdminTournamentDTO adminTournamentDTO);
    List<AdminTournamentDTO> tournamentsToAdminTournamentDTOs(List<Tournament> tournaments);
    List<Tournament> adminTournamentDTOsToTournaments(List<AdminTournamentDTO> adminTournamentDTOs);

    @Mapping(target = "numberOfSignups", expression = "java(tournament.getSignups().size())")
    @Mapping(target = "signedUp", ignore = true) // We will set signedUp manually
    UserTournamentDTO mapTournamentToUserTournamentDTO(Tournament tournament);
    Tournament userTournamentDTOToTournament(UserTournamentDTO userTournamentDTO);
    List<UserTournamentDTO> mapTournamentsToUserTournamentDTOs(List<Tournament> tournaments);
    List<Tournament> userTournamentDTOsToTournaments(List<UserTournamentDTO> userTournamentCardDTOs);

//    TournamentBracketsDTO
    TournamentBracketsDTO tournamentToTournamentBracketsDTO(Tournament tournament);
    Tournament tournamentBracketsDTOToTournament(TournamentBracketsDTO tournamentBracketsDTO);
    List<TournamentBracketsDTO> tournamentsToTournamentBracketsDTOs(List<Tournament> tournaments);
    List<Tournament> tournamentBracketDTOsToTournaments(List<TournamentBracketsDTO> tournamentBracketDTOs);

//    DetailedTournamentDTO
    DetailedTournamentDTO tournamentToDetailedTournamentDTO(Tournament tournament);
    Tournament detailedTournamentDTOToTournament(DetailedTournamentDTO detailedTournamentDTO);
    List<DetailedTournamentDTO> tournamentsToDetailedTournamentDTOs(List<Tournament> tournaments);
    List<Tournament> tournamentDTOsToCreateTournaments(List<TournamentDTO> tournamentDTOs);

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
    default UserTournamentDTO tournamentToUserTournamentDTO(Tournament tournament, String username) {
        // Use the MapStruct-generated mapping method for the rest of the fields
        UserTournamentDTO dto = mapTournamentToUserTournamentDTO(tournament);

        // Manually set "signedUp" field
        dto.setSignedUp(tournament.getSignups().contains(username));

        return dto;
    }

    default List<UserTournamentDTO> tournamentsToUserTournamentDTOs(List<Tournament> tournaments, String username) {
        List<UserTournamentDTO> dtos = new ArrayList<>();
        for (Tournament tournament : tournaments) {
            dtos.add(tournamentToUserTournamentDTO(tournament, username));
        }
        return dtos;
    }

    // Derive signup status
    default String getSignupStatus(Tournament tournament) {
        return LocalDateTime.now().isBefore(tournament.getSignupEndDate()) ?
                SignupStatus.OPEN.toString() : SignupStatus.CLOSED.toString();
    }
}
