package com.cs203.smucode.mappers;

import com.cs203.smucode.dto.CreateTournamentDTO;
import com.cs203.smucode.dto.TournamentDTO;
import com.cs203.smucode.models.Tournament;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.util.List;
import java.util.UUID;

@Mapper(componentModel="spring", uses={RoundMapper.class})
public interface TournamentMapper {

//    TournamentDTO
    TournamentDTO tournamentToTournamentDTO(Tournament tournament);

    Tournament tournamentDTOToTournament(TournamentDTO tournamentDTO);

    List<TournamentDTO> tournamentsToTournamentDTOs(List<Tournament> tournaments);

    List<Tournament> tournamentDTOsToTournaments(List<TournamentDTO> tournamentDTOs);

//    CreateTournamentDTO
    CreateTournamentDTO tournamentToCreateTournamentDTO(Tournament tournament);

    Tournament createTournamentDTOToTournament(CreateTournamentDTO createTournamentDTO);

    List<CreateTournamentDTO> tournamentsToCreateTournamentDTOs(List<Tournament> tournaments);

    List<Tournament> tournamentDTOsToCreateTournaments(List<TournamentDTO> tournamentDTOs);

}
