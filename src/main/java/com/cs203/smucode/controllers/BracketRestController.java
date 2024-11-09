package com.cs203.smucode.controllers;

import com.cs203.smucode.dto.BracketDTO;
import com.cs203.smucode.dto.TournamentDTO;
import com.cs203.smucode.dto.UpdateBracketScoreDTO;
import com.cs203.smucode.mappers.BracketMapper;
import com.cs203.smucode.models.Bracket;
import com.cs203.smucode.models.Tournament;
import com.cs203.smucode.services.BracketService;
import io.swagger.v3.oas.annotations.Operation;
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/tournaments/brackets")
public class BracketRestController {

    private static final Logger logger = LoggerFactory.getLogger(BracketRestController.class.getName());
    private final BracketService bracketService;
    private final BracketMapper bracketMapper;

    @Autowired
    public BracketRestController(BracketService bracketService,
                                 BracketMapper bracketMapper) {
        this.bracketService = bracketService;
        this.bracketMapper = bracketMapper;
    }

    @Operation(summary = "Get bracket by bracket ID")
    @GetMapping("/{bracketId}")
    public BracketDTO getBracketById(@PathVariable UUID bracketId) {
        Bracket bracket = bracketService.findBracketById(bracketId);
        return bracketMapper.bracketToBracketDTO(bracket);
    }

    @Operation(summary = "Update bracket score")
    @PutMapping("/{bracketId}")
    public BracketDTO updateBracketScore(@PathVariable UUID bracketId,
                                         @Valid @RequestBody UpdateBracketScoreDTO bracketDTO) {
        Bracket bracketScore = bracketMapper.updateBracketScoreDTOToBracket(bracketDTO);
        logger.info("bracket from bracket DTO: {}", bracketScore);
        Bracket newBracket = bracketService.updateBracketScore(bracketId, bracketScore);
        logger.info("new bracket: {}", newBracket);
        return bracketMapper.bracketToBracketDTO(newBracket);
    }

    @Operation(summary = "End current bracket and set winner")
    @PutMapping("/{bracketId}/end")
    public BracketDTO endBracket(@PathVariable UUID bracketId) {
        Bracket bracket = bracketService.endBracket(bracketId);
        return bracketMapper.bracketToBracketDTO(bracket);
    }
}
