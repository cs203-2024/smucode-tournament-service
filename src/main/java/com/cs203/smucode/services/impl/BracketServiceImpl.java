package com.cs203.smucode.services.impl;

import com.cs203.smucode.constants.Status;
import com.cs203.smucode.exceptions.BracketNotFoundException;
import com.cs203.smucode.exceptions.UserNotFoundException;
import com.cs203.smucode.models.Bracket;
import com.cs203.smucode.models.Round;
import com.cs203.smucode.models.Tournament;
import com.cs203.smucode.repositories.BracketServiceRepository;
import com.cs203.smucode.repositories.RoundServiceRepository;
import com.cs203.smucode.repositories.TournamentServiceRepository;
import com.cs203.smucode.services.BracketService;
import com.cs203.smucode.services.RatingUpdateService;
import de.gesundkrank.jskills.Player;
import jakarta.transaction.Transactional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
public class BracketServiceImpl implements BracketService {
    private static final Logger logger = LoggerFactory.getLogger(BracketServiceImpl.class);

    private final BracketServiceRepository bracketServiceRepository;
    private final RoundServiceRepository roundServiceRepository;
    private final TournamentServiceRepository tournamentServiceRepository;

    private final RatingUpdateService ratingUpdateService;

    @Autowired
    public BracketServiceImpl(BracketServiceRepository bracketServiceRepository,
                              RoundServiceRepository roundServiceRepository,
                              TournamentServiceRepository tournamentServiceRepository,
                              RatingUpdateService ratingUpdateService) {
        this.bracketServiceRepository = bracketServiceRepository;
        this.roundServiceRepository = roundServiceRepository;
        this.tournamentServiceRepository = tournamentServiceRepository;
        this.ratingUpdateService = ratingUpdateService;
    }

    @Transactional
    public List<Bracket> findAllBracketsByRoundId(UUID roundId) {
        return bracketServiceRepository.findByRoundId(roundId).orElse(null);
    }

    @Transactional
    public Bracket findBracketById(UUID id) {
        return bracketServiceRepository.findById(id).orElseThrow(() ->
                new BracketNotFoundException("Bracket with id " + id + " not found"));
    }

    @Transactional
    public Bracket findBracketByRoundIdAndSeqId(UUID id, int seqId) {
        return bracketServiceRepository.findByRoundIdAndSeqId(id, seqId).orElseThrow(() ->
                new BracketNotFoundException("Bracket with round id " + id + " and seq id " + seqId + " not found"));
    }

    @Transactional
    public Bracket findBracketByRoundIdAndPlayer(UUID roundId, String player) {
        return bracketServiceRepository.findByRoundIdAndPlayer1OrPlayer2(roundId, player).orElseThrow(() ->
                new BracketNotFoundException(
                        "Bracket with round id " + roundId + " and player " + player + " not found"
                ));
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
        bracketToUpdate.setPlayer1Score(bracket.getPlayer1Score());
        bracketToUpdate.setPlayer2Score(bracket.getPlayer2Score());
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

        // Round bye
        if (bracket.getPlayer1() == null) { // Player 1 absent
            bracket.setWinner(bracket.getPlayer2());
        } else if (bracket.getPlayer2() == null) { // Player 2 absent
            bracket.setWinner(bracket.getPlayer1());
        } else { // Both players present (default)
            String winner = bracket.getPlayer1Score() > bracket.getPlayer2Score() ? bracket.getPlayer1() : bracket.getPlayer2();
            bracket.setWinner(winner);
            //TODO: refactor this to handle bye cases; issue now is that for updating to happen, we need both players ("relative updating")
            ratingUpdateService.updateRatings(bracket);
        }
        bracket.setStatus(Status.COMPLETED);

        return bracketServiceRepository.save(bracket);
    }

    public Bracket removePlayerFromBracket(Bracket bracket, String player) {
        logger.info("Removing player from bracket: {}", bracket.getId());
        if (bracket.getPlayer1().equals(player)) {
            clearPlayer1(bracket);
        } else if (bracket.getPlayer2().equals(player)) {
            clearPlayer2(bracket);
        } else {
        throw new UserNotFoundException("User " + player + " not found in bracket " + bracket.getId());
    }
        return bracketServiceRepository.save(bracket);

    }

    @Transactional
    public void deleteBracketById(UUID id) {
        if (!bracketServiceRepository.existsById(id)) {
            throw new BracketNotFoundException("Bracket with id " + id + " not found");
        }
        bracketServiceRepository.deleteById(id);
    }

//    helper functions
    private void clearPlayer1(Bracket bracket) {
        bracket.setPlayer1(null);
        bracket.setPlayer1Score(0);
        bracket.setPlayer1WinProbability(0.0);
        bracket.setPlayer2WinProbability(100.0);
    }

    private void clearPlayer2(Bracket bracket) {
        bracket.setPlayer2(null);
        bracket.setPlayer2Score(0);
        bracket.setPlayer2WinProbability(0.0);
        bracket.setPlayer1WinProbability(100.0);
    }

}
