package com.cs203.smucode.controllers;

import com.cs203.smucode.dto.BracketDTO;
import com.cs203.smucode.dto.BracketScoreDTO;
import com.cs203.smucode.dto.RoundDTO;
import com.cs203.smucode.exceptions.UserNotFoundException;
import com.cs203.smucode.mappers.BracketMapper;
import com.cs203.smucode.models.Bracket;
import com.cs203.smucode.services.BracketService;
import com.cs203.smucode.services.impl.UserServiceClientImpl;
import jakarta.validation.Valid;
import org.apache.catalina.User;
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
    private final UserServiceClientImpl userServiceClientImpl;

    @Autowired
    public BracketRestController(BracketService bracketService,
                                 BracketMapper bracketMapper,
                                 UserServiceClientImpl userServiceClientImpl) {
        this.bracketService = bracketService;
        this.bracketMapper = bracketMapper;
        this.userServiceClientImpl = userServiceClientImpl;
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

    @PutMapping("/{bracketId}/updateScore")
    public BracketDTO updateBracketScore(@PathVariable UUID bracketId, @Valid @RequestBody BracketScoreDTO bracketDTO) {
        List<UUID> playerIds = bracketDTO.getPlayerIds();

//        TODO: uncomment when connection established with user microservice
//        for (UUID playerId : playerIds) {
//            if (!userServiceClientImpl.userExists(playerId)) {
//                throw new UserNotFoundException("User not found with id: " + playerId);
//            }
//        }

        Bracket bracket = bracketService.findBracketById(bracketId);
        bracket.setPlayerIds(playerIds);
        return bracketMapper.bracketToBracketDTO(bracketService.updateBracket(bracketId, bracket));
    }

    @DeleteMapping("{id}")
    public void deleteBracket(@PathVariable UUID id) {
        bracketService.deleteBracketById(id);
    }
}
