package com.cs203.smucode.mappers;

import com.cs203.smucode.dto.*;
import com.cs203.smucode.models.Tournament;
import org.mapstruct.Mapper;

import java.util.List;

@Mapper(componentModel="spring", uses={RoundMapper.class})
public interface TournamentMapper {

//    TournamentCardDTO
    AdminTournamentCardDTO tournamentToAdminTournamentCardDTO(Tournament tournament);
    Tournament AdminTournamentCardDTOToTournament(AdminTournamentCardDTO adminTournamentCardDTO);
    List<AdminTournamentCardDTO> tournamentsToAdminTournamentCardDTOs(List<Tournament> tournaments);
    List<Tournament> adminTournamentCardDTOsToTournaments(List<AdminTournamentCardDTO> adminTournamentCardDTOs);

    UserTournamentCardDTO tournamentToUserTournamentCardDTO(Tournament tournament);
    Tournament UserTournamentCardDTOToTournament(UserTournamentCardDTO userTournamentCardDTO);
    List<UserTournamentCardDTO> tournamentsToUserTournamentCardDTOs(List<Tournament> tournaments);
    List<Tournament> userTournamentCardDTOsToTournaments(List<UserTournamentCardDTO> userTournamentCardDTOs);


//    TournamentDTO
    TournamentDTO tournamentToTournamentDTO(Tournament tournament);
    Tournament tournamentDTOToTournament(TournamentDTO tournamentDTO);
    List<TournamentDTO> tournamentsToTournamentDTOs(List<Tournament> tournaments);
    List<Tournament> tournamentDTOsToTournaments(List<TournamentDTO> tournamentDTOs);

//    DetailedTournamentDTO
    DetailedTournamentDTO tournamentToDetailedTournamentDTO(Tournament tournament);
    Tournament detailedTournamentDTOToTournament(DetailedTournamentDTO detailedTournamentDTO);
    List<DetailedTournamentDTO> tournamentsToDetailedTournamentDTOs(List<Tournament> tournaments);
    List<Tournament> tournamentDTOsToCreateTournaments(List<TournamentDTO> tournamentDTOs);

}
