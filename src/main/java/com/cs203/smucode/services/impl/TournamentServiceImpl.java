package com.cs203.smucode.services.impl;

import com.cs203.smucode.exceptions.TournamentNotFoundException;
import com.cs203.smucode.repositories.TournamentServiceRepository;
import com.cs203.smucode.models.Tournament;
import com.cs203.smucode.services.TournamentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
public class TournamentServiceImpl implements TournamentService {
    private final TournamentServiceRepository tournamentServiceRepository;

    @Autowired
    public TournamentServiceImpl(TournamentServiceRepository tournamentServiceRepository) {
        this.tournamentServiceRepository = tournamentServiceRepository;
    }

    public List<Tournament> findAllTournaments() { return tournamentServiceRepository.findAll(); }

    public Tournament findTournamentById(String id) {
        return tournamentServiceRepository.findById(id).orElse(null);
    }

    public Tournament createTournament(Tournament tournament) {
        if (tournament == null) { return null; } // data insert validation

        return tournamentServiceRepository.save(tournament);
    }

    public Tournament updateTournament(String id, Tournament tournament) {
        return tournamentServiceRepository.findById(id).map(existingTournament -> {
            existingTournament.setName(tournament.getName());
            existingTournament.setDescription(tournament.getDescription());
            existingTournament.setStatus(tournament.getStatus());
            existingTournament.setStartDate(tournament.getStartDate());
            existingTournament.setEndDate(tournament.getEndDate());
            existingTournament.setFormat(tournament.getFormat());
            existingTournament.setCapacity(tournament.getCapacity());
            existingTournament.setIcon(tournament.getIcon());
            existingTournament.setOwner(tournament.getOwner());
            existingTournament.setSignUpDeadline(tournament.getSignUpDeadline());
            existingTournament.setTimeWeight(tournament.getTimeWeight());
            existingTournament.setMemWeight(tournament.getMemWeight());
            existingTournament.setTestCaseWeight(tournament.getTestCaseWeight());

            return tournamentServiceRepository.save(existingTournament);
        }).orElseThrow(() -> new TournamentNotFoundException("Tournament not found with id: " +id));
    }

    public void deleteTournamentById(String id) { tournamentServiceRepository.deleteById(id); }

    @Override
    public List<Tournament> findTournamentsBySignUpDeadline(LocalDateTime dateTime, String status) {
        return tournamentServiceRepository.findBySignUpDeadlineBeforeAndStatus(dateTime, status);
    }
}