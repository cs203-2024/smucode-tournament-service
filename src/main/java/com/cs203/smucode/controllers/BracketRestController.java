package com.cs203.smucode.controllers;

import com.cs203.smucode.dto.BracketDTO;
import com.cs203.smucode.dto.UpdateBracketScoreDTO;
import com.cs203.smucode.mappers.BracketMapper;
import com.cs203.smucode.models.Bracket;
import com.cs203.smucode.services.BracketService;
//import com.cs203.smucode.services.impl.UserServiceClientImpl;
import io.swagger.v3.oas.annotations.Operation;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/tournaments/brackets")
public class BracketRestController {

    private BracketService bracketService;
    private BracketMapper bracketMapper;

    @Autowired
    public BracketRestController(BracketService bracketService,
                                 BracketMapper bracketMapper) {
        this.bracketService = bracketService;
        this.bracketMapper = bracketMapper;
    }

//    @Operation(summary = "Get all brackets associated to round")
//    @GetMapping("/round/{roundId}")
//    public List<BracketDTO> getAllBracketsByRoundId(@PathVariable UUID roundId) {
//        List<Bracket> brackets = bracketService.findAllBracketsByRoundId(roundId);
//        return bracketMapper.bracketsToBracketDTOs(brackets);
//    }

    @Operation(summary = "Get bracket by bracket ID")
    @GetMapping("/{bracketId}")
    public BracketDTO getBracketById(@PathVariable UUID bracketId) {
        Bracket bracket = bracketService.findBracketById(bracketId);
        return bracketMapper.bracketToBracketDTO(bracket);
    }

//    @Operation(summary = "Create bracket")
//    @ResponseStatus(HttpStatus.CREATED)
//    @PostMapping()
//    public BracketDTO createBracket(@Valid @RequestBody BracketDTO bracketDTO) {
//        Bracket bracket = bracketMapper.bracketDTOToBracket(bracketDTO);
//        bracketService.createBracket(bracket);
//        return bracketDTO;
//    }

//    @Operation(summary = "Update bracket by bracket ID")
//    @PutMapping("{bracketId}")
//    public BracketDTO updateBracket(@PathVariable UUID bracketId, @Valid @RequestBody UpdateBracketScoreDTO bracketDTO) {
//        Bracket bracket = bracketMapper.UpdateBracketScoreDTOToBracket(bracketDTO);
//        bracket = bracketService.updateBracket(bracketId, bracket);
////        TODO: separate api?
////        bracketService.updateBracketPlayers(id, bracketDTO.getPlayerIds());
//        return bracketMapper.bracketToBracketDTO(bracket);
//    }

    @Operation(summary = "Update bracket score")
    @PutMapping("/{bracketId}")
    public BracketDTO updateBracketScore(@PathVariable UUID bracketId, @Valid @RequestBody UpdateBracketScoreDTO bracketDTO) {
        Bracket bracketScore = bracketMapper.updateBracketScoreDTOToBracket(bracketDTO);
        Bracket newBracket = bracketService.updateBracket(bracketId, bracketScore);
        return bracketMapper.bracketToBracketDTO(newBracket);
    }

//    @Operation(summary = "Delete existing bracket by bracket ID")
//    @DeleteMapping("{bracketId}")
//    public void deleteBracket(@PathVariable UUID bracketId) {
//        bracketService.deleteBracketById(bracketId);
//    }
}
