package com.cs203.smucode.controllers;

import com.cs203.smucode.dto.BracketDTO;
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

    @Autowired
    public BracketRestController(BracketService bracketService) {
        this.bracketService = bracketService;
    }

    @GetMapping("/round/{id}")
    public List<BracketDTO> getAllBracketsByRoundId(@PathVariable UUID id) {
        return bracketService.findAllBracketsByRoundId(id);
    }

    @GetMapping("/{id}")
    public Bracket getBracketById(@PathVariable UUID id) {
        return bracketService.findBracketById(id);
    }

    @ResponseStatus(HttpStatus.CREATED)
    @PostMapping("/")
    public Bracket createBracket(@Valid @RequestBody Bracket bracket) {
        return bracketService.createBracket(bracket);
    }

    @PutMapping("{id}")
    public Bracket updateBracket(@PathVariable UUID id, @Valid @RequestBody Bracket bracket) {
        return bracketService.updateBracket(id, bracket);
    }

    @DeleteMapping("{id}")
    public void deleteBracket(@PathVariable UUID id) {
        bracketService.deleteBracketById(id);
    }
}
