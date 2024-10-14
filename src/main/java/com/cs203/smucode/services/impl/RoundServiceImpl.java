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

    public List<Round> findAllRoundsByTournamentId(UUID tournamentId) {
        return roundServiceRepository.findByTournamentId(tournamentId);
    }

    public Round findRoundById(UUID id) {
        return roundServiceRepository.findById(id).orElse(null);
    }

    public Round findRoundByTournamentIdAndSeqId(UUID tournamentId, int seqId) {
        return roundServiceRepository.findByTournamentIdAndSeqId(tournamentId, seqId);
    }

    public Round findRoundByTournamentIdAndName(UUID tournamentId, String name) {
        return roundServiceRepository.findByTournamentIdAndName(tournamentId, name);
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
        return roundServiceRepository.findById(id).map(existingRound -> {
            existingRound.setName(round.getName());
            existingRound.setStartDate(round.getStartDate());
            existingRound.setEndDate(round.getEndDate());
            existingRound.setStatus(round.getStatus());

            return roundServiceRepository.save(existingRound);
        }).orElseThrow(() -> new RoundNotFoundException("Round not found with id: " + id));
    }

    public void deleteRoundById(UUID id) { roundServiceRepository.deleteById(id); }

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
