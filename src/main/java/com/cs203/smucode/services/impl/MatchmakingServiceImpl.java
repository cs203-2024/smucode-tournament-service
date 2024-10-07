package com.cs203.smucode.services.impl;

import com.cs203.smucode.dto.UserDTO;
import com.cs203.smucode.models.Bracket;
import com.cs203.smucode.models.Tournament;
import com.cs203.smucode.services.BracketService;
import com.cs203.smucode.services.MatchmakingService;
import com.cs203.smucode.services.TournamentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

@Service
public class MatchmakingServiceImpl implements MatchmakingService {
    private final UserServiceClient userServiceClient;
    private final BracketService bracketService;
    private final TournamentService tournamentService;

    @Autowired
    public MatchmakingServiceImpl(UserServiceClient userServiceClient, BracketService bracketService, TournamentService tournamentService) {
        this.userServiceClient = userServiceClient;
        this.bracketService = bracketService;
        this.tournamentService = tournamentService;
    }

    @Override
    public void runMatchmaking(Tournament tournament) {
        //Placeholder for signup collections (not implemented)
        List<UserDTO> signups = userServiceClient.getTournamentSignups(tournament.getId());

        //TODO: Potentially (not needed)/changed
        if (signups.isEmpty() || signups.size() < tournament.getCapacity()) {
            //Case: when no/not enough players signed up
            tournament.setStatus("cancelled");
            tournamentService.updateTournament(tournament.getId(), tournament);
            return;
        }

        //Select participant according to metric
        List<UserDTO> selectedPlayers = selectParticipants(signups, tournament.getCapacity(), "best");

        //Pair the selected players
        List<Bracket> brackets = pairPlayers(selectedPlayers, tournament, true);

        //Save em
        for (Bracket bracket : brackets) {
            bracketService.createBracket(bracket);
        }

        //and finally, update the tournament status
        tournament.setStatus("ongoing");
        tournamentService.updateTournament(tournament.getId(), tournament);
    }

    private List<UserDTO> selectParticipants(List<UserDTO> signups, int tournamentCapacity, String selectionType) {
        // Sort players by skill index (ascending)
        List<UserDTO> sortedSignups = signups.stream()
                .sorted(Comparator.comparingDouble(UserDTO::getSkillIndex))
                .collect(Collectors.toList());

        int totalSignups = sortedSignups.size();

        return switch (selectionType) {
            case "best" -> sortedSignups.stream()
                    .sorted(Comparator.comparingDouble(UserDTO::getSkillIndex).reversed())
                    .limit(tournamentCapacity)
                    .collect(Collectors.toList());
            case "worst" -> sortedSignups.stream()
                    .limit(tournamentCapacity)
                    .collect(Collectors.toList());
            case "neutral" -> {
                int startIndex = (totalSignups - tournamentCapacity) / 2;
                yield sortedSignups.stream()
                        .skip(startIndex)
                        .limit(tournamentCapacity)
                        .collect(Collectors.toList());
            }
            //TODO: custom exception
            default -> throw new IllegalArgumentException("Invalid selection type: " + selectionType);
        };
    }

    public List<Bracket> pairPlayers(List<UserDTO> players, Tournament tournament, boolean shuffle) {
        //Step 1: Sort players by skillIndex (descending)
        players.sort(Comparator.comparingDouble(UserDTO::getSkillIndex).reversed());

        int totalPlayers = players.size();
        int halfSize = totalPlayers / 2;

        //Step 2: Divide players into fixed (top players) and variable seeds (bottom)
        List<UserDTO> fixedSeeds = players.subList(0, halfSize); //Top half
        List<UserDTO> variableSeeds = new ArrayList<>(players.subList(halfSize, totalPlayers)); //Bottom half

        //Step 3: Shuffle variable seeds (if required)
        //shuffling adds more "fairness" and randomness, keeping it (optional) here rn so its easier for me to unit test
        if (shuffle) {
            Collections.shuffle(variableSeeds);
        } else {
            //Reverse variable seeds to match highest vs. lowest when not shuffled
            Collections.reverse(variableSeeds);
        }

        //Step 4: Pair em up
        List<Bracket> brackets = new ArrayList<>();
        for (int i = 0; i < halfSize; i++) {
            UserDTO player1 = fixedSeeds.get(i);
            UserDTO player2 = variableSeeds.get(i);

            Bracket bracket = new Bracket("0", player1, player2, "1");
            brackets.add(bracket);
        }

        return brackets;
    }
}
