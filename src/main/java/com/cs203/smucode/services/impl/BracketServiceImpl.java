package com.cs203.smucode.services.impl;

import com.cs203.smucode.constants.Status;
import com.cs203.smucode.exceptions.BracketNotFoundException;
import com.cs203.smucode.models.Bracket;
import com.cs203.smucode.models.Round;
import com.cs203.smucode.models.Tournament;
import com.cs203.smucode.repositories.BracketServiceRepository;
import com.cs203.smucode.repositories.RoundServiceRepository;
import com.cs203.smucode.repositories.TournamentServiceRepository;
import com.cs203.smucode.services.BracketService;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
public class BracketServiceImpl implements BracketService {

    private final BracketServiceRepository bracketServiceRepository;
    private final RoundServiceRepository roundServiceRepository;
    private final TournamentServiceRepository tournamentServiceRepository;

    @Autowired
    public BracketServiceImpl(BracketServiceRepository bracketServiceRepository,
                              RoundServiceRepository roundServiceRepository,
                              TournamentServiceRepository tournamentServiceRepository) {
        this.bracketServiceRepository = bracketServiceRepository;
        this.roundServiceRepository = roundServiceRepository;
        this.tournamentServiceRepository = tournamentServiceRepository;
    }

    @Transactional
    public List<Bracket> findAllBracketsByRoundId(UUID roundId) {
        return bracketServiceRepository.findByRoundId(roundId).orElse(null);
    }

    @Transactional
    public Bracket findBracketById(UUID id) {
        return bracketServiceRepository.findById(id).orElse(null);
    }

    @Transactional
    public Bracket findBracketByRoundIdAndSeqId(UUID id, int seqId) {
        return bracketServiceRepository.findByRoundIdAndSeqId(id, seqId).orElseThrow(() ->
                new BracketNotFoundException("Bracket with id " + id + " not found"));
    }

    @Transactional
    public Bracket createBracket(Bracket bracket) {
        return bracketServiceRepository.save(bracket);
    }

    @Transactional
    public Bracket updateBracket(UUID id, Bracket bracket) {
        Optional<Bracket> bracketOptional = bracketServiceRepository.findById(id);

        if (bracketOptional.isEmpty()) {
            throw new BracketNotFoundException("Bracket with id " + id + " not found");
        }

        Bracket bracketToUpdate = bracketOptional.get();

        // Update status of parent round
        Round parentRound = bracketToUpdate.getRound();
        if (parentRound.getStatus() == Status.UPCOMING) {
            parentRound.setStatus(Status.ONGOING);
            roundServiceRepository.save(parentRound);
        }

        // Update tournament current round and status
        Tournament tournament = parentRound.getTournament();
        tournament.setCurrentRound(parentRound.getName());
        if (tournament.getStatus() == Status.UPCOMING) {
            tournament.setStatus(Status.ONGOING);
        }
        tournamentServiceRepository.save(tournament);

        // Update bracket
        if (bracketToUpdate.getStatus() == Status.UPCOMING) { // set status to ongoing if previously upcoming
            bracketToUpdate.setStatus(Status.ONGOING);
        }
        if (bracket.getWinner() != null) {
            bracketToUpdate.setWinner(bracket.getWinner()); // set status to completed if winner is passed
            bracketToUpdate.setStatus(Status.COMPLETED);
        }
        bracketToUpdate.setPlayer1(bracket.getPlayer1());
        bracketToUpdate.setPlayer2(bracket.getPlayer2());
        bracketServiceRepository.save(bracketToUpdate);

        return bracket;
    }

    // Set winner of bracket
    @Transactional
    public Bracket endBracket(UUID id) {
        Optional<Bracket> bracketOptional = bracketServiceRepository.findById(id);

        if (bracketOptional.isEmpty()) {
            throw new BracketNotFoundException("Bracket with id " + id + " not found");
        }

        Bracket bracket = bracketOptional.get();
        String winner = bracket.getPlayer1Score() > bracket.getPlayer2Score() ? bracket.getPlayer1() : bracket.getPlayer2();
        bracket.setWinner(winner);
        bracket.setStatus(Status.COMPLETED);

        return bracketServiceRepository.save(bracket);
    }

    @Transactional
    public void deleteBracketById(UUID id) {
        if (!bracketServiceRepository.existsById(id)) {
            throw new BracketNotFoundException("Bracket with id " + id + " not found");
        }
        bracketServiceRepository.deleteById(id);
    }
}
