package com.cs203.smucode.services.impl;

import com.cs203.smucode.constants.Status;
import com.cs203.smucode.exceptions.BracketNotFoundException;
import com.cs203.smucode.models.Bracket;
import com.cs203.smucode.models.PlayerInfo;
import com.cs203.smucode.models.Round;
import com.cs203.smucode.models.Tournament;
import com.cs203.smucode.repositories.BracketServiceRepository;
import com.cs203.smucode.repositories.RoundServiceRepository;
import com.cs203.smucode.repositories.TournamentServiceRepository;
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
    private TournamentServiceRepository tournamentServiceRepository;

    @Autowired
    public BracketServiceImpl(BracketServiceRepository bracketServiceRepository,
                              RoundServiceRepository roundServiceRepository,
                              TournamentServiceRepository tournamentServiceRepository) {
        this.bracketServiceRepository = bracketServiceRepository;
        this.roundServiceRepository = roundServiceRepository;
        this.tournamentServiceRepository = tournamentServiceRepository;
    }

    public List<Bracket> findAllBracketsByRoundId(UUID roundId) {
        return bracketServiceRepository.findByRoundId(roundId);
    }

    public Bracket findBracketById(UUID id) {
        return bracketServiceRepository.findById(id).orElse(null);
    }

    public Bracket findBracketByRoundIdAndSeqId(UUID id, int seqId) {
        return bracketServiceRepository.findByRoundIdAndSeqId(id, seqId);
    }

    public Bracket createBracket(Bracket bracket) {
        return bracketServiceRepository.save(bracket);
    }

    public Bracket updateBracket(UUID id, Bracket bracket) {

        Bracket existingBracket = bracketServiceRepository.findById(id)
                .orElseThrow(() -> new BracketNotFoundException("Bracket not found with id: " + id));

//        update status of parent round
        Round parentRound = existingBracket.getRound();
        if (parentRound.getStatus() == Status.UPCOMING) {
            parentRound.setStatus(Status.ONGOING);
            roundServiceRepository.save(parentRound);
        }

//        update tournament current round and status
        Tournament tournament = parentRound.getTournament();
        tournament.setCurrentRound(parentRound.getName());
        if (tournament.getStatus() == Status.UPCOMING) {
            tournament.setStatus(Status.ONGOING);
        }
        tournamentServiceRepository.save(tournament);

////        TODO: uncomment when connection established with user microservice
////        for (UUID playerId : playerIds) {
////            if (!userServiceClientImpl.userExists(playerId)) {
////                throw new UserNotFoundException("User not found with id: " + playerId);
////            }
////        }
//

//        update bracket
        if (existingBracket.getStatus() == Status.UPCOMING) { // set status to ongoing if previously upcoming
            existingBracket.setStatus(Status.ONGOING);
        }
        if (bracket.getWinner() != null) {
            existingBracket.setWinner(bracket.getWinner()); // set status to completed if winner is passed
            existingBracket.setStatus(Status.COMPLETED);
        }
//        existingBracket.setPlayers(bracket.getPlayers().stream()
//                .map(playerInfo -> new PlayerInfo(playerInfo.getPlayerId(), playerInfo.getScore()))
//                .collect(Collectors.toList()));
        existingBracket.setPlayer1(bracket.getPlayer1());
        existingBracket.setPlayer2(bracket.getPlayer2());
        bracketServiceRepository.save(existingBracket);

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

    public void deleteBracketById(UUID id) { bracketServiceRepository.deleteById(id); }

}
