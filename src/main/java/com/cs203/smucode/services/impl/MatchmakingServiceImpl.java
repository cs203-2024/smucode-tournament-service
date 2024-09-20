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

        if (signups.isEmpty() || signups.size() < tournament.getCapacity()) {
            //Case: when no/not enough players signed up
            tournament.setStatus("cancelled");
            tournamentService.updateTournament(tournament.getId(), tournament);
            return;
        }

        //Select participant according to metric
        List<UserDTO> selectedPlayers = selectParticipants(signups, tournament.getCapacity(), "best");

        //Pair the selected players
        List<Bracket> brackets = pairPlayers(selectedPlayers, tournament);

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

    private List<Bracket> pairPlayers(List<UserDTO> players, Tournament tournament) {
        //Step 1: Rank players by skillIndex in descending order
        players.sort(Comparator.comparingDouble(UserDTO::getSkillIndex).reversed());

        //Step 2: Determine top (fixed) seeds
        int fixedSeedCount = players.size() / 2; //Half the total seeds
        List<UserDTO> topSeeds = players.subList(0, fixedSeedCount);
        //lower seeds
        List<UserDTO> otherPlayers = new ArrayList<>(players.subList(fixedSeedCount, players.size()));

        //Step 3: Assign seeds
        Map<Integer, UserDTO> seededPlayers = new HashMap<>();
        int seedNumber = 1;

        //fixed seeds first
        for (UserDTO player : topSeeds) {
            seededPlayers.put(seedNumber++, player);
        }

        //Shuffle and assign seeds to remaining players
        Collections.shuffle(otherPlayers);
        for (UserDTO player : otherPlayers) {
            seededPlayers.put(seedNumber++, player);
        }

        //Step 4: Generate seeding matrix
        int totalPlayers = players.size();
        int[] seedingMatrix = generateSeedingMatrix(totalPlayers, fixedSeedCount);

        //Map positions to seeds
        Map<Integer, Integer> positionToSeed = new HashMap<>();
        for (int i = 0; i < seedingMatrix.length; i++) {
            positionToSeed.put(i + 1, seedingMatrix[i]);
        }

        //TODO: handle bracketID & roundID accumulation
        //Step 5: Pair and populate brackets according to the seeding matrix
        List<Bracket> brackets = new ArrayList<>();
        int totalBrackets = totalPlayers / 2;

        for (int i = 0; i < totalBrackets; i++) {
            int position1 = i * 2 + 1;
            int position2 = position1 + 1;

            int seed1 = positionToSeed.get(position1);
            int seed2 = positionToSeed.get(position2);

            UserDTO player1 = seededPlayers.get(seed1);
            UserDTO player2 = seededPlayers.get(seed2);

            Bracket bracket = new Bracket("0",player1, player2, "1");
            brackets.add(bracket);
        }

        //Handle odd number of players: temp solution, giving a bye
        if (totalPlayers % 2 != 0) {
            //last player
            int seed = positionToSeed.get(totalPlayers);
            UserDTO player = seededPlayers.get(seed);

            Bracket bracket = new Bracket("0",player, null, "1");
            brackets.add(bracket);
        }

        return brackets;
    }


    private int[] generateSeedingMatrix(int totalPlayers, int fixedSeedCount) {
        int[] seeds = new int[totalPlayers];
        List<Integer> fixedSeeds = new ArrayList<>();
        for (int i = 1; i <= fixedSeedCount; i++) {
            fixedSeeds.add(i);
        }
        List<Integer> variableSeeds = new ArrayList<>();
        for (int i = fixedSeedCount + 1; i <= totalPlayers; i++) {
            variableSeeds.add(i);
        }

        int highFixedIndex = 0;
        int lowFixedIndex = fixedSeeds.size() - 1;
        int variableIndex = 0;
        boolean useFixed = true;

        for (int i = 0; i < totalPlayers; i++) {
            if (useFixed && highFixedIndex <= lowFixedIndex) {
                seeds[i] = fixedSeeds.get(highFixedIndex++);
                useFixed = false;
            } else if (!useFixed && lowFixedIndex >= highFixedIndex) {
                seeds[i] = fixedSeeds.get(lowFixedIndex--);
                useFixed = true;
            } else {
                //Assign variable seeds randomly
                seeds[i] = variableSeeds.get(variableIndex++);
            }
        }

        return seeds;
    }

}
