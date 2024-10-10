package com.cs203.smucode.services.impl;

import com.cs203.smucode.constants.Status;
import com.cs203.smucode.exceptions.BracketNotFoundException;
import com.cs203.smucode.models.Bracket;
import com.cs203.smucode.models.PlayerInfo;
import com.cs203.smucode.models.Round;
import com.cs203.smucode.repositories.BracketServiceRepository;
import com.cs203.smucode.repositories.RoundServiceRepository;
import com.cs203.smucode.services.BracketService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
public class BracketServiceImpl implements BracketService {

    private BracketServiceRepository bracketServiceRepository;
    private RoundServiceRepository roundServiceRepository;

    @Autowired
    public BracketServiceImpl(BracketServiceRepository bracketServiceRepository,
                              RoundServiceRepository roundServiceRepository) {
        this.bracketServiceRepository = bracketServiceRepository;
        this.roundServiceRepository = roundServiceRepository;
    }

    public List<Bracket> findAllBracketsByRoundId(UUID roundId) {
        return bracketServiceRepository.findByRoundId(roundId);
    }

    public Bracket findBracketById(UUID id) {
        return bracketServiceRepository.findById(id).orElse(null);
    }

    public Bracket createBracket(Bracket bracket) {
        return bracketServiceRepository.save(bracket);
    }

    public Bracket updateBracket(UUID id, Bracket bracket) {

        Bracket exisitingBracket = bracketServiceRepository.findById(id)
                .orElseThrow(() -> new BracketNotFoundException("Bracket not found with id: " + id));

//        update status of parent round
        Round parentRound = exisitingBracket.getRound();
        if (parentRound.getStatus() == Status.UPCOMING) {
            parentRound.setStatus(Status.ONGOING);
            roundServiceRepository.save(parentRound);
        }

//        exisitingBracket.setRound(bracket.getRound());
        exisitingBracket.setStatus(bracket.getStatus());
        exisitingBracket.setWinner(bracket.getWinner());

////        TODO: uncomment when connection established with user microservice
////        for (UUID playerId : playerIds) {
////            if (!userServiceClientImpl.userExists(playerId)) {
////                throw new UserNotFoundException("User not found with id: " + playerId);
////            }
////        }
//

        exisitingBracket.setPlayers(bracket.getPlayers().stream()
                .map(playerInfo -> new PlayerInfo(playerInfo.getPlayerId(), playerInfo.getScore()))
                .collect(Collectors.toList()));
        System.out.println("hello");
        System.out.println(bracket.getPlayers());
        System.out.println(exisitingBracket.getPlayers());
        bracketServiceRepository.save(exisitingBracket);

        return bracket;
    }

//    public Bracket updateBracketPlayers(UUID bracketId, List<UUID> playerIds) {
//
//        Bracket exisitigBracket = bracketServiceRepository.findById(bracketId)
//                .orElseThrow(() -> new BracketNotFoundException("Bracket not found with id: " + bracketId));
//
////        TODO: uncomment when connection established with user microservice
////        for (UUID playerId : playerIds) {
////            if (!userServiceClientImpl.userExists(playerId)) {
////                throw new UserNotFoundException("User not found with id: " + playerId);
////            }
////        }
//
//        exisitigBracket.setPlayerIds(playerIds);
//        return bracketServiceRepository.save(exisitigBracket);
//    }

    public void deleteBracketById(UUID id) {bracketServiceRepository.deleteById(id);}

}
