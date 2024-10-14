package com.cs203.smucode.services.impl;

import com.cs203.smucode.constants.Status;
import com.cs203.smucode.dto.UserDTO;
import com.cs203.smucode.models.Bracket;
import com.cs203.smucode.models.Round;
import com.cs203.smucode.models.Tournament;
import com.cs203.smucode.services.BracketService;
import com.cs203.smucode.services.MatchmakingService;
import com.cs203.smucode.services.RoundService;
import com.cs203.smucode.services.TournamentService;
import com.cs203.smucode.services.UserServiceClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

@Service
public class MatchmakingServiceImpl implements MatchmakingService {

    private static final Logger logger = LoggerFactory.getLogger(MatchmakingServiceImpl.class);
    private final TournamentService tournamentService;
    private final RoundService roundService;
    private final BracketService bracketService;
    private final UserServiceClient userServiceClient;

    @Autowired
    public MatchmakingServiceImpl(RoundService roundService,
                                  BracketService bracketService,
                                  TournamentService tournamentService,
                                  UserServiceClient userServiceClient) {
        this.roundService = roundService;
        this.bracketService = bracketService;
        this.tournamentService = tournamentService;
        this.userServiceClient = userServiceClient;
    }

//    @Override
    public void runMatchmaking(Tournament tournament) {
        //Get the signups for the tourney
        //TODO: adjust accordingly when signup implementation is clear
        List<String> signupUsernames = tournament.getSignups().stream().toList();

        List<UserDTO> signups = List.of(
                new UserDTO("user1", null, "user1@test.com", null, "player", 25, 8.33,0),
                new UserDTO("user2", null, "user2@test.com", null, "player", 25, 8.33,0),
                new UserDTO("user3", null, "user3@test.com", null, "player", 25, 8.33,0),
                new UserDTO("user4", null, "user4@test.com", null, "player", 25, 8.33,0)
        );
//        for (String username : signupUsernames) {
//            signups.add(username);
//        }
//        System.out.println(signups);
//        List<UserDTO> signups = tournament.getTournamentSignups(String.valueOf(tournament.getId()));

        //Select participants according to the selection metric
        List<UserDTO> selectedPlayers = selectParticipants(signups, tournament.getCapacity(), "best");

        //Pair the selected players into brackets
        List<Bracket> brackets = pairPlayers(selectedPlayers, tournament, true);

        //Save the brackets
        for (Bracket bracket : brackets) {
            bracketService.createBracket(bracket);
            logger.info(bracket.getPlayer1());
            logger.info(bracket.getPlayer2());
        }

        //Update tournament status to ongoing
        tournament.setStatus(Status.ONGOING);
        //TODO: readjust when "save" is implemented
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

    public List<Bracket> pairPlayers(List<UserDTO> players, Tournament tournament, boolean shuffle) {
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

        List<Round> rounds = tournament.getRounds();
        if (rounds != null && !rounds.isEmpty()) {
            //fetch the first round, which might be null
            Round firstRound = rounds.get(0);

            //if the first round is null, create a new Round
            if (firstRound == null) {
                firstRound = new Round();
                firstRound.setTournament(tournament);
                firstRound.setName("Round 1");
                firstRound.setStartDate(tournament.getStartDate());
                firstRound.setEndDate(tournament.getEndDate());
                firstRound.setStatus(Status.ONGOING);

                rounds.set(0, firstRound);
            } else {
                firstRound.setTournament(tournament);
                firstRound.setName("Round 1");
                firstRound.setStartDate(tournament.getStartDate());
                firstRound.setEndDate(tournament.getEndDate());
                firstRound.setStatus(Status.ONGOING);
            }

            //roundService.updateRound(firstRound)? if necessary
        } else {
            throw new IllegalStateException("Rounds have not been pre-created in the tournament.");
        }

        for (int i = 0; i < halfSize; i++) {
            UserDTO player1 = fixedSeeds.get(i);
            UserDTO player2 = variableSeeds.get(i);

            //Create a new bracket
            Bracket bracket = new Bracket();
//            bracket.setPlayers(Arrays.asList(
//                    new PlayerInfo(player1.username(), 0),
//                    new PlayerInfo(player2.username(), 0)
//            ));
            bracket.setPlayer1(player1.username());
            bracket.setPlayer2(player2.username());

            //Set the round
            bracket.setRound(rounds.get(0));

            //Set the status of the bracket
            bracket.setStatus(Status.ONGOING);

            //Add the bracket to the list
            brackets.add(bracket);
        }

        return brackets;
    }
}
