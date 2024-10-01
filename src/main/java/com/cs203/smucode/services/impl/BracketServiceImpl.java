package com.cs203.smucode.services.impl;

import com.cs203.smucode.models.Bracket;
import com.cs203.smucode.repositories.BracketServiceRepository;
import com.cs203.smucode.services.BracketService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
public class BracketServiceImpl implements BracketService {

    private BracketServiceRepository bracketServiceRepository;

    @Autowired
    public BracketServiceImpl(BracketServiceRepository bracketServiceRepository) {
        this.bracketServiceRepository = bracketServiceRepository;
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
//        return bracketServiceRepository.findById(id).map(existingBracket -> {
//            existingBracket.setPlayer1(bracket.getPlayer1());
//            existingBracket.setPlayer2(bracket.getPlayer2());
//            existingBracket.setRoundId(bracket.getRoundId());
//
//            return bracketServiceRepository.save(existingBracket);
//        }).orElseThrow(() -> new BracketNotFoundException("Round not found with id: " + id));
        return null;
    }

    public void deleteBracketById(UUID id) {bracketServiceRepository.deleteById(id);}




}
