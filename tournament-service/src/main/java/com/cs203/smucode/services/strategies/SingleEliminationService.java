package com.cs203.smucode.services.strategies;

import com.cs203.smucode.models.Tournament;
import com.cs203.smucode.repositories.SingleEliminationRepository;
import com.cs203.smucode.services.ITournamentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

@Service("SingleElimination")
public class SingleEliminationService implements ITournamentService {
    private final SingleEliminationRepository singleEliminationRepository;

    @Autowired
    public SingleEliminationService(SingleEliminationRepository singleEliminationRepository) {
        this.singleEliminationRepository = singleEliminationRepository;
    }

    public void createTournament(Tournament tournament) {
        return;
    }
}