package com.cs203.smucode.services.impl;

import com.cs203.smucode.mappers.TournamentMapper;
import com.cs203.smucode.dto.TournamentDTO;
import com.cs203.smucode.exceptions.TournamentNotFoundException;
import com.cs203.smucode.models.Tournament;
import com.cs203.smucode.repositories.TournamentServiceRepository;
import com.cs203.smucode.services.TournamentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
public class TournamentServiceImpl implements TournamentService {
    private final TournamentServiceRepository tournamentServiceRepository;
    private final TournamentMapper tournamentMapper;

    @Autowired
    public TournamentServiceImpl(TournamentServiceRepository tournamentServiceRepository,
                                 TournamentMapper tournamentMapper) {
        this.tournamentServiceRepository = tournamentServiceRepository;
        this.tournamentMapper = tournamentMapper;
    }

    public List<TournamentDTO> findAllTournaments() {
        List<Tournament> tournaments = tournamentServiceRepository.findAll();
        return tournamentMapper.tournamentsToTournamentDTOs(tournaments);
    }

    public TournamentDTO findTournamentById(UUID id) {
        Optional<Tournament> tournament = tournamentServiceRepository.findById(id);
        if (tournament.isEmpty()) {
            throw new TournamentNotFoundException("Tournament with id " + id + " not found");
        }
        return tournamentMapper.tournamentToTournamentDTO(tournament.get());
    }

//    public TournamentDTO createTournament(TournamentDTO tournamentDTO) {
//        if (tournamentDTO == null) { return null; } // data insert validation
//
//        Tournament tournament = tournamentMapper.toTournament(tournamentDTO);
//        tournamentServiceRepository.save(tournament);
//        return  tournamentDTO;
//    }

//    public Tournament updateTournament(String id, Tournament tournament) {
//        return tournamentServiceRepository.findById(id).map(existingTournament -> {
//            existingTournament.setName(tournament.getName());
//            existingTournament.setDescription(tournament.getDescription());
//            existingTournament.setStatus(tournament.getStatus());
//            existingTournament.setStartDate(tournament.getStartDate());
//            existingTournament.setEndDate(tournament.getEndDate());
//            existingTournament.setFormat(tournament.getFormat());
//            existingTournament.setCapacity(tournament.getCapacity());
//            existingTournament.setIcon(tournament.getIcon());
//            existingTournament.setOwner(tournament.getOwner());
//            existingTournament.setSignUpDeadline(tournament.getSignUpDeadline());
//            existingTournament.setTimeWeight(tournament.getTimeWeight());
//            existingTournament.setMemWeight(tournament.getMemWeight());
//            existingTournament.setTestCaseWeight(tournament.getTestCaseWeight());
//
//            return tournamentServiceRepository.save(existingTournament);
//        }).orElseThrow(() -> new TournamentNotFoundException("Tournament not found with id: " +id));
//    }

    public void deleteTournamentById(UUID id) { tournamentServiceRepository.deleteById(id); }

}