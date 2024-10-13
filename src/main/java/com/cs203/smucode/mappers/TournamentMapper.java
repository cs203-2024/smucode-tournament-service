package com.cs203.smucode.mappers;

import com.cs203.smucode.dto.*;
import com.cs203.smucode.models.Tournament;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

@Mapper(componentModel="spring", uses={RoundMapper.class})
public interface TournamentMapper {

//    TournamentCardDTO
    @Mapping(target = "numberOfSignups", expression = "java(tournament.getSignups().size())")
    AdminTournamentCardDTO tournamentToAdminTournamentCardDTO(Tournament tournament);
    Tournament AdminTournamentCardDTOToTournament(AdminTournamentCardDTO adminTournamentCardDTO);
    List<AdminTournamentCardDTO> tournamentsToAdminTournamentCardDTOs(List<Tournament> tournaments);
    List<Tournament> adminTournamentCardDTOsToTournaments(List<AdminTournamentCardDTO> adminTournamentCardDTOs);

    @Mapping(target = "numberOfSignups", expression = "java(tournament.getSignups().size())")
    @Mapping(target = "signedUp", ignore = true) // We will set signedUp manually
    @Mapping(target = "participated", ignore = true) // We will set participated manually
    UserTournamentCardDTO mapTournamentToUserTournamentCardDTO(Tournament tournament);
    Tournament UserTournamentCardDTOToTournament(UserTournamentCardDTO userTournamentCardDTO);
    List<UserTournamentCardDTO> mapTournamentsToUserTournamentCardDTOs(List<Tournament> tournaments);
    List<Tournament> userTournamentCardDTOsToTournaments(List<UserTournamentCardDTO> userTournamentCardDTOs);

//    TournamentDTO
    @Mapping(target = "numberOfSignups", expression = "java(tournament.getSignups().size())")
    TournamentDTO tournamentToTournamentDTO(Tournament tournament);
    Tournament tournamentDTOToTournament(TournamentDTO tournamentDTO);
    List<TournamentDTO> tournamentsToTournamentDTOs(List<Tournament> tournaments);
    List<Tournament> tournamentDTOsToTournaments(List<TournamentDTO> tournamentDTOs);

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
    // Custom method to handle multiple parameters - to derive signedUp and participated
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
}
