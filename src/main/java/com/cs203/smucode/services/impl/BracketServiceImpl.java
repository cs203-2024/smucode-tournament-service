package com.cs203.smucode.services.impl;

import com.cs203.smucode.dto.BracketDTO;
import com.cs203.smucode.exceptions.BracketNotFoundException;
import com.cs203.smucode.mappers.BracketMapper;
import com.cs203.smucode.mappers.UserMapper;
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
    private BracketMapper bracketMapper;

    @Autowired
    public BracketServiceImpl(BracketServiceRepository bracketServiceRepository,
                              BracketMapper bracketMapper) {
        this.bracketServiceRepository = bracketServiceRepository;
        this.bracketMapper = bracketMapper;
    }

    public List<BracketDTO> findAllBracketsByRoundId(UUID roundId) {
        List<Bracket> brackets = bracketServiceRepository.findByRoundId(roundId);
        return bracketMapper.bracketsToBracketDTOs(brackets);
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
