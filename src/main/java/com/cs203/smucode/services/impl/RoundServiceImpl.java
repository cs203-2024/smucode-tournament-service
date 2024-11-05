package com.cs203.smucode.services.impl;

import com.cs203.smucode.constants.Status;
import com.cs203.smucode.exceptions.RoundNotFoundException;
import com.cs203.smucode.exceptions.UserNotFoundException;
import com.cs203.smucode.models.Bracket;
import com.cs203.smucode.models.Round;
import com.cs203.smucode.models.PredictionResult;
import com.cs203.smucode.models.Tournament;
import com.cs203.smucode.repositories.RoundServiceRepository;
import com.cs203.smucode.services.BracketService;
import com.cs203.smucode.services.PredictionService;
import com.cs203.smucode.services.RoundService;
import com.cs203.smucode.services.TournamentService;
import jakarta.transaction.Transactional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Service
public class RoundServiceImpl implements RoundService {

    private static final Logger logger = LoggerFactory.getLogger(RoundServiceImpl.class);

    private final RoundServiceRepository roundServiceRepository;
    private final BracketService bracketService;
    private final PredictionService predictionService;


    @Autowired
    public RoundServiceImpl(RoundServiceRepository roundServiceRepository,
                            BracketService bracketService,
                            PredictionService predictionService) {
        this.roundServiceRepository = roundServiceRepository;
        this.bracketService = bracketService;
        this.predictionService = predictionService;
    }

    @Transactional
    public Round findRoundById(UUID id) {
        return roundServiceRepository.findById(id).orElseThrow(() ->
                new RoundNotFoundException("Round with id " + id + " not found"));
    }

    @Transactional
    public Round findRoundByTournamentIdAndSeqId(UUID tournamentId, int seqId) {
        return roundServiceRepository.findByTournamentIdAndSeqId(tournamentId, seqId).orElseThrow(() ->
                new RoundNotFoundException("Round with tournament id " + tournamentId + " and seq id " + seqId + " not found"));
    }

    @Transactional
    public Round findRoundByTournamentIdAndName(UUID tournamentId, String name) {
        return roundServiceRepository.findByTournamentIdAndName(tournamentId, name).orElseThrow(() ->
                new RoundNotFoundException("Round with tournament id " + tournamentId + " and name " + name + " not found"));
    }

    @Transactional
    public List<Round> findAllRoundsByTournamentId(UUID tournamentId) {
        return roundServiceRepository.findByTournamentId(tournamentId).orElse(null);
    }

    @Transactional
    public Round createRound(Round round) {
        roundServiceRepository.save(round);
        int bracketCount = getBracketCountFromRoundName(round.getName());
        // generate empty brackets
        try {
            for (int i = 0; i < bracketCount; i++) {
                Bracket bracket = new Bracket();
                bracket.setTournament(round.getTournament());
                bracket.setRound(round);
                bracket.setStatus(Status.UPCOMING);
                // TODO: move this to be handled at DB level
                bracket.setPlayer1Score(0);
                bracket.setPlayer2Score(0);
                bracketService.createBracket(bracket);
            }
        }
        catch (Exception e) {
            System.out.println(e.getMessage());
        }

        return round;
    }

    @Transactional
    public Round updateRound(UUID id, Round round) {
        Optional<Round> roundOptional = roundServiceRepository.findById(id);

        if (roundOptional.isEmpty()) {
            throw new RoundNotFoundException("Round with id " + id + " not found");
        }

        Round roundToUpdate = roundOptional.get();

        if (round.getName() != null) { roundToUpdate.setName(round.getName()); }
        if (round.getStartDate() != null) { roundToUpdate.setStartDate(round.getStartDate()); }
        if (round.getEndDate() != null) { roundToUpdate.setEndDate(round.getEndDate()); }
        if (round.getStatus() != null) { roundToUpdate.setStatus(round.getStatus()); }

        return roundServiceRepository.save(roundToUpdate);
    }

    @Transactional
    public Round populateNextRound(UUID currRoundId, UUID nextRoundId) {
        Optional <Round> roundOptional = roundServiceRepository.findById(nextRoundId);

        if (roundOptional.isEmpty()) {
            throw new RoundNotFoundException("Next round with id " + nextRoundId + " not found");
        }

        Round nextRound = roundOptional.get();

        for (int i = 1; i <= nextRound.getBrackets().size(); i++) {

            Bracket oldBracket = bracketService.findBracketByRoundIdAndSeqId(nextRoundId, i);
            Bracket newBracket = new Bracket();

            // TODO: round progression validation (whether previous round has finished - null winner)
            String player1 = bracketService.findBracketByRoundIdAndSeqId(currRoundId, i*2 - 1).getWinner();
            String player2 = bracketService.findBracketByRoundIdAndSeqId(currRoundId, i*2).getWinner();

            // Include prediction
            // TODO: handle byes? (player1 || player2 == null)
            if (player1 != null && player2 != null) {
                PredictionResult prediction = predictionService.predictMatch(player1, player2);
                newBracket.setPlayer1(player1);
                newBracket.setPlayer2(player2);
                newBracket.setPlayer1WinProbability(prediction.getPlayer1WinProbability());
                newBracket.setPlayer2WinProbability(prediction.getPlayer2WinProbability());
                newBracket.setStatus(Status.ONGOING);
            }

            bracketService.updateBracket(oldBracket.getId(), newBracket);

        }

        return nextRound;

    }

    @Transactional
    public Round removePlayerFromOngoingRound(UUID roundId, String username) {
        logger.info("Removing player from round: {}", roundId);
        Bracket bracketWithPlayer = bracketService.findBracketByRoundIdAndPlayer(roundId, username);
        bracketService.removePlayerFromBracket(bracketWithPlayer, username);
        return findRoundById(roundId);
    }

    @Transactional
    public void deleteRoundById(UUID id) {
        if (!roundServiceRepository.existsById(id)) {
            throw new RoundNotFoundException("Round with id " + id + " not found");
        }
        roundServiceRepository.deleteById(id);
    }

//    helper functions
    int getBracketCountFromRoundName(String roundName) {
        Pattern pattern = Pattern.compile("\\d+"); // regex pattern to match 1 or more digits
        Matcher matcher = pattern.matcher(roundName); // searches regex pattern against roundName

        if (matcher.find()) {
            return Integer.parseInt(matcher.group()) / 2; // retrieves first match
        }
        return 0;
    }
}
