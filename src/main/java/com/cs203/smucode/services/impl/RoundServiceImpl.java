package com.cs203.smucode.services.impl;

import com.cs203.smucode.exceptions.RoundNotFoundException;
import com.cs203.smucode.exceptions.TournamentNotFoundException;
import com.cs203.smucode.models.Round;
import com.cs203.smucode.repositories.RoundServiceRepository;
import com.cs203.smucode.services.RoundService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class RoundServiceImpl implements RoundService {

    RoundServiceRepository roundServiceRepository;

    @Autowired
    public RoundServiceImpl(RoundServiceRepository roundServiceRepository) {
        this.roundServiceRepository = roundServiceRepository;
    }

    public List<Round> findAllRoundsByTournamentId(String tournamentId) {
        return roundServiceRepository.findByTournamentId(tournamentId);
    }

    public Round findRoundById(String id) {
        return roundServiceRepository.findById(id).orElse(null);
    }

    public Round createRound(Round round) {
        return roundServiceRepository.save(round);
    }

    public Round updateRound(String id, Round round) {
        return roundServiceRepository.findById(id).map(existingRound -> {
            existingRound.setName(round.getName());
            existingRound.setStartDate(round.getStartDate());
            existingRound.setEndDate(round.getEndDate());

            return roundServiceRepository.save(existingRound);
        }).orElseThrow(() -> new RoundNotFoundException("Round not found with id: " + id));

    }

    public void deleteRoundById(String id) { roundServiceRepository.deleteById(id); }

}
