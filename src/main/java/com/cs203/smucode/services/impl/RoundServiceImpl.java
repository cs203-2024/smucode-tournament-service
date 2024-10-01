package com.cs203.smucode.services.impl;

import com.cs203.smucode.dto.RoundDTO;
import com.cs203.smucode.exceptions.RoundNotFoundException;
import com.cs203.smucode.exceptions.TournamentNotFoundException;
import com.cs203.smucode.mappers.RoundMapper;
import com.cs203.smucode.models.Round;
import com.cs203.smucode.repositories.RoundServiceRepository;
import com.cs203.smucode.services.RoundService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
public class RoundServiceImpl implements RoundService {

    private final RoundServiceRepository roundServiceRepository;
    private final RoundMapper roundMapper;

    @Autowired
    public RoundServiceImpl(RoundServiceRepository roundServiceRepository, RoundMapper roundMapper) {
        this.roundServiceRepository = roundServiceRepository;
        this.roundMapper = roundMapper;
    }

    public List<RoundDTO> findAllRoundsByTournamentId(UUID tournamentId) {
        List<Round> rounds = roundServiceRepository.findByTournamentId(tournamentId);
        return roundMapper.roundsToRoundDTOs(rounds);
    }

    public RoundDTO findRoundById(UUID id) {
        return roundMapper.roundToRoundDTO(roundServiceRepository.findById(id).orElse(null));
    }

//    public Round createRound(Round round) {
//        return roundServiceRepository.save(round);
//    }
//
//    public Round updateRound(UUID id, Round round) {
//        return roundServiceRepository.findById(id).map(existingRound -> {
//            existingRound.setName(round.getName());
//            existingRound.setStartDate(round.getStartDate());
//            existingRound.setEndDate(round.getEndDate());
//
//            return roundServiceRepository.save(existingRound);
//        }).orElseThrow(() -> new RoundNotFoundException("Round not found with id: " + id));
//    }

    public void deleteRoundById(UUID id) { roundServiceRepository.deleteById(id); }

}
