package com.cs203.smucode.services.impl;

import com.cs203.smucode.models.UserDTO;
import com.cs203.smucode.models.Match;
import com.cs203.smucode.services.MatchmakingService;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

@Service
public class MatchmakingServiceImpl implements MatchmakingService {

    @Override
    public List<Match> selectAndPairPlayers(List<UserDTO> signups, int tournamentCapacity, String selectionType, String tournamentId) {
        List<UserDTO> selectedPlayers = selectParticipants(signups, tournamentCapacity, selectionType);
        return pairPlayers(selectedPlayers, tournamentId);
    }

    private List<User> selectParticipants(List<User> signups, int tournamentCapacity, String selectionType) {
        // Sort players by skill index (ascending)
        List<User> sortedSignups = signups.stream()
                .sorted(Comparator.comparingDouble(User::getSkillIndex))
                .collect(Collectors.toList());

        int totalSignups = sortedSignups.size();

        switch (selectionType) {
            case "best":
                // Select top players (descending order)
                return sortedSignups.stream()
                        .sorted(Comparator.comparingDouble(User::getSkillIndex).reversed())
                        .limit(tournamentCapacity)
                        .collect(Collectors.toList());

            case "worst":
                // Select bottom players (already in ascending order)
                return sortedSignups.stream()
                        .limit(tournamentCapacity)
                        .collect(Collectors.toList());

            case "neutral":
                // Calculate the start index to skip extreme ends
                int startIndex = (totalSignups - tournamentCapacity) / 2;
                return sortedSignups.stream()
                        .skip(startIndex)
                        .limit(tournamentCapacity)
                        .collect(Collectors.toList());
            default:
                throw new IllegalArgumentException("Invalid selection type: " + selectionType);
        }
    }

    private List<Match> pairPlayers(List<User> players, Long tournamentId) {
        // Step 1: Rank players by skillIndex in descending order
        players.sort(Comparator.comparingDouble(User::getSkillIndex).reversed());

        // Step 2: Determine top seeds
        int fixedSeedCount = players.size() / 2; // Number of top seeds to fix
        List<User> topSeeds = players.subList(0, fixedSeedCount);
        List<User> otherPlayers = new ArrayList<>(players.subList(fixedSeedCount, players.size()));

        // Step 3: Assign seeds
        Map<Integer, User> seededPlayers = new HashMap<>();
        int seedNumber = 1;

        // Assign fixed seeds
        for (User player : topSeeds) {
            seededPlayers.put(seedNumber++, player);
        }

        // Shuffle and assign seeds to remaining players
        Collections.shuffle(otherPlayers);
        for (User player : otherPlayers) {
            seededPlayers.put(seedNumber++, player);
        }

        // Step 4: Generate seeding matrix
        int totalPlayers = players.size();
        int[] seedingMatrix = generateSeedingMatrix(totalPlayers, fixedSeedCount);

        // Map positions to seeds
        Map<Integer, Integer> positionToSeed = new HashMap<>();
        for (int i = 0; i < seedingMatrix.length; i++) {
            positionToSeed.put(i + 1, seedingMatrix[i]);
        }

        // Step 5: Pair players according to bracket positions
        List<Match> matches = new ArrayList<>();
        int totalMatches = totalPlayers / 2;

        for (int i = 0; i < totalMatches; i++) {
            int position1 = i * 2 + 1;
            int position2 = position1 + 1;

            int seed1 = positionToSeed.get(position1);
            int seed2 = positionToSeed.get(position2);

            User player1 = seededPlayers.get(seed1);
            User player2 = seededPlayers.get(seed2);

            Match match = new Match();
            match.setTournamentId(tournamentId);
            match.setPlayer1Username(player1.getUsername());
            match.setPlayer2Username(player2.getUsername());
            match.setStatus("scheduled");
            matches.add(match);
        }

        // Handle odd number of players
        if (totalPlayers % 2 != 0) {
            int lastPosition = totalPlayers;
            int seed = positionToSeed.get(lastPosition);
            User player = seededPlayers.get(seed);

            Match match = new Match();
            match.setTournamentId(tournamentId);
            match.setPlayer1Username(player.getUsername());
            match.setPlayer2Username(null); // Bye
            match.setStatus("bye");
            matches.add(match);
        }

        return matches;
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
                // Assign variable seeds randomly
                seeds[i] = variableSeeds.get(variableIndex++);
            }
        }

        return seeds;
    }

}
