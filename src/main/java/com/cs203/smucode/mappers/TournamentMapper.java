package com.cs203.smucode.mappers;

import com.cs203.smucode.dto.TournamentDTO;
import com.cs203.smucode.models.Tournament;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.util.List;

@Mapper(componentModel="spring", uses={RoundMapper.class})
public interface TournamentMapper {

    @Mapping(target = "rounds", source = "rounds")
    TournamentDTO tournamentToTournamentDTO(Tournament tournament);

    Tournament tournamentDTOToTournament(TournamentDTO tournamentDTO);

    List<TournamentDTO> tournamentsToTournamentDTOs(List<Tournament> tournaments);

    List<Tournament> tournamentDTOsToTournaments(List<TournamentDTO> tournamentDTOs);

}
