package com.cs203.smucode.services.impl;

import com.cs203.smucode.exceptions.RoundNotFoundException;
import com.cs203.smucode.models.Bracket;
import com.cs203.smucode.models.Round;
import com.cs203.smucode.repositories.RoundServiceRepository;
import com.cs203.smucode.services.BracketService;
import com.cs203.smucode.services.RoundService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Service
public class RoundServiceImpl implements RoundService {

    private final RoundServiceRepository roundServiceRepository;
    private final BracketService bracketService;

    @Autowired
    public RoundServiceImpl(RoundServiceRepository roundServiceRepository,
                            BracketService bracketService) {
        this.roundServiceRepository = roundServiceRepository;
        this.bracketService = bracketService;
    }

    public Round findRoundById(UUID id) {
        return roundServiceRepository.findById(id).orElseThrow(() ->
                new RoundNotFoundException("Round with id " + id + " not found"));
    }

    public Round findRoundByTournamentIdAndSeqId(UUID tournamentId, int seqId) {
        return roundServiceRepository.findByTournamentIdAndSeqId(tournamentId, seqId).orElseThrow(() ->
                new RoundNotFoundException("Round with tournament id " + tournamentId + " and seq id " + seqId + " not found"));
    }

    public Round findRoundByTournamentIdAndName(UUID tournamentId, String name) {
        return roundServiceRepository.findByTournamentIdAndName(tournamentId, name).orElseThrow(() ->
                new RoundNotFoundException("Round with tournament id " + tournamentId + " and name " + name + " not found"));
    }

    public List<Round> findAllRoundsByTournamentId(UUID tournamentId) {
        return roundServiceRepository.findByTournamentId(tournamentId).orElse(null);
    }

    private List<String> mockUsers = List.of("user1", "user2", "user3", "user4");
    private int index = 0;

    public Round createRound(Round round) {
        roundServiceRepository.save(round);
        int bracketCount = getBracketCountFromRoundName(round.getName());
        try {
            for (int i = 0; i < bracketCount; i++) {
                Bracket bracket = new Bracket();
                bracket.setRound(round);
//                TODO: mock data
                bracket.setPlayer1(mockUsers.get(index));
                index++;
                bracket.setPlayer2(mockUsers.get(index));
                index++;
                bracketService.createBracket(bracket);
            }
        }
        catch (Exception e) {
            System.out.println(e.getMessage());
        }

        return round;
    }

    public Round updateRound(UUID id, Round round) {
        Optional<Round> roundOptional = roundServiceRepository.findById(id);

        if (roundOptional.isEmpty()) {
            throw new RoundNotFoundException("Round with id " + id + " not found");
        }

        Round roundToUpdate = roundOptional.get();
        roundToUpdate.setName(round.getName());
        roundToUpdate.setStartDate(round.getStartDate());
        roundToUpdate.setEndDate(round.getEndDate());
        roundToUpdate.setStatus(round.getStatus());

        return roundServiceRepository.save(roundToUpdate);
    }

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
