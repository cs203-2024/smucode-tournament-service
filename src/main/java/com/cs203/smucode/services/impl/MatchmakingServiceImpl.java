package com.cs203.smucode.services.impl;

import com.cs203.smucode.constants.Status;
import com.cs203.smucode.dto.UserDTO;
import com.cs203.smucode.models.Bracket;
import com.cs203.smucode.models.Round;
import com.cs203.smucode.models.Tournament;
import com.cs203.smucode.services.*;
import jakarta.transaction.Transactional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.context.request.NativeWebRequest;

import java.util.*;
import java.util.stream.Collectors;

@Service
public class MatchmakingServiceImpl implements MatchmakingService {

    private static final Logger logger = LoggerFactory.getLogger(MatchmakingServiceImpl.class);
    private final TournamentService tournamentService;
    private final RoundService roundService;
    private final BracketService bracketService;
    private final UserService userService;

    @Autowired
    public MatchmakingServiceImpl(RoundService roundService,
                                  BracketService bracketService,
                                  TournamentService tournamentService,
                                  UserService userService, NativeWebRequest nativeWebRequest) {
        this.roundService = roundService;
        this.bracketService = bracketService;
        this.tournamentService = tournamentService;
        this.userService = userService;
    }

//    @Override
    @Transactional
    public void runMatchmaking(Tournament tournament) {

        // if tournament already started early return
        if (tournament.getStatus() != Status.UPCOMING) {
            throw new IllegalStateException("Tournament already started");
        }

        //Get the signups for the tourney
        //TODO: adjust accordingly when signup implementation is clear
        List<String> signupUsernames = tournament.getSignups().stream().toList();

        List<UserDTO> signups = userService.getUsers(signupUsernames);

        //Select participants according to the selection metric
        List<UserDTO> selectedPlayers = selectParticipants(signups, tournament.getCapacity(), "best");

        //Pair the selected players into brackets (order of brackets matters)
        List<Bracket> bracketPairs = pairPlayers(selectedPlayers, true);

        //Save the brackets
        updateBrackets(tournament, bracketPairs);

        tournament.setStatus(Status.ONGOING);
        tournamentService.updateTournament(tournament.getId(), tournament);
    }

    public List<UserDTO> selectParticipants(List<UserDTO> signups, int tournamentCapacity, String selectionType) {
        //Sort players by skill index (descending)
        List<UserDTO> sortedSignups = signups.stream()
                .sorted(Comparator.comparingDouble(UserDTO::skillIndex))
                .toList();

        int totalSignups = sortedSignups.size();

        return switch (selectionType) {
            case "best" ->
                //select the top players
                    sortedSignups.stream()
                            .sorted(Comparator.comparingDouble(UserDTO::skillIndex).reversed())
                            .limit(tournamentCapacity)
                            .collect(Collectors.toList());
            case "worst" ->
                //select the bottom players
                    sortedSignups.stream()
                            .limit(tournamentCapacity)
                            .collect(Collectors.toList());
            case "neutral" -> {
                //select players from the middle
                int startIndex = (totalSignups - tournamentCapacity) / 2;
                yield sortedSignups.stream()
                        .skip(startIndex)
                        .limit(tournamentCapacity)
                        .collect(Collectors.toList());
            }
            default -> throw new IllegalArgumentException("Invalid selection type: " + selectionType);
        };
    }

    public List<Bracket> pairPlayers(List<UserDTO> players, boolean shuffle) {
        //Step 1: Sort players by skillIndex in descending order
        players.sort(Comparator.comparingDouble(UserDTO::skillIndex).reversed());

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

            //Create a new bracket
            Bracket bracket = new Bracket();
            bracket.setPlayer1(player1.username());
            bracket.setPlayer2(player2.username());

            //Add the bracket to the list
            brackets.add(bracket);
        }

        return brackets;
    }

    public void updateBrackets(Tournament tournament, List<Bracket> bracketPairs) {

        if (tournament.getRounds().isEmpty()) {
            throw new IllegalStateException("Rounds have not been pre-created in the tournament.");
        }

        logger.info("tournament: {}", tournament);
        Round firstRound = roundService.findRoundByTournamentIdAndSeqId(tournament.getId(), 1);
        logger.info("first round: {}", firstRound.toString());

        if (firstRound.getBrackets() == null || firstRound.getBrackets().isEmpty()) {
            throw new IllegalStateException("Brackets have not been pre-created in the tournament.");
        }

        for (int i = 0; i < bracketPairs.size(); i++) {
            Bracket bracketToUpdate = bracketService.findBracketByRoundIdAndSeqId(firstRound.getId(), i+1);
            logger.info("bracket to update: {}", bracketToUpdate);

            Bracket newBracket = bracketPairs.get(i);
            logger.info("new bracket: {}", newBracket.toString());
            bracketToUpdate.setStatus(Status.ONGOING); // change bracket status to ongoing
            bracketToUpdate.setPlayer1(newBracket.getPlayer1());
            bracketToUpdate.setPlayer2(newBracket.getPlayer2());
            logger.info("updated bracket : {}", bracketToUpdate);

            bracketService.updateBracket(bracketToUpdate.getId(), bracketToUpdate);
        }

    }
}
