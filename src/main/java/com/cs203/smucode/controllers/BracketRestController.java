package com.cs203.smucode.controllers;

import com.cs203.smucode.dto.BracketDTO;
import com.cs203.smucode.mappers.BracketMapper;
import com.cs203.smucode.models.Bracket;
import com.cs203.smucode.services.BracketService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/brackets")
public class BracketRestController {

    private BracketService bracketService;
    private BracketMapper bracketMapper;

    @Autowired
    public BracketRestController(BracketService bracketService,
                                 BracketMapper bracketMapper) {
        this.bracketService = bracketService;
        this.bracketMapper = bracketMapper;
    }

    @GetMapping("/round/{id}")
    public List<BracketDTO> getAllBracketsByRoundId(@PathVariable UUID id) {
        List<Bracket> brackets = bracketService.findAllBracketsByRoundId(id);
        return bracketMapper.bracketsToBracketDTOs(brackets);
    }

    @GetMapping("/{id}")
    public BracketDTO getBracketById(@PathVariable UUID id) {
        Bracket bracket = bracketService.findBracketById(id);
        return bracketMapper.bracketToBracketDTO(bracket);
    }

    @ResponseStatus(HttpStatus.CREATED)
    @PostMapping("/")
    public BracketDTO createBracket(@Valid @RequestBody BracketDTO bracketDTO) {
        Bracket bracket = bracketMapper.bracketDTOToBracket(bracketDTO);
        bracketService.createBracket(bracket);
        return bracketDTO;
    }

    @PutMapping("{id}")
    public BracketDTO updateBracket(@PathVariable UUID id, @Valid @RequestBody BracketDTO bracketDTO) {
        Bracket bracket = bracketMapper.bracketDTOToBracket(bracketDTO);
        bracketService.updateBracket(id, bracket);
        return bracketDTO;
    }

    @DeleteMapping("{id}")
    public void deleteBracket(@PathVariable UUID id) {
        bracketService.deleteBracketById(id);
    }
}
