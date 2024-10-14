package com.cs203.smucode.schedulers;

import com.cs203.smucode.constants.Status;
import com.cs203.smucode.models.Tournament;
import com.cs203.smucode.services.MatchmakingService;
import com.cs203.smucode.services.TournamentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.List;

@Component
public class TournamentScheduler {

    private final TournamentService tournamentService;
    private final MatchmakingService matchmakingService;

    @Autowired
    public TournamentScheduler(TournamentService tournamentService, MatchmakingService matchmakingService) {
        this.tournamentService = tournamentService;
        this.matchmakingService = matchmakingService;
    }

    @Scheduled(cron = "0/30 * * * * ?") //Runs every 30 minutes
    public void scheduleMatchmaking() {
        LocalDateTime now = LocalDateTime.now();
        List<Tournament> tournaments = tournamentService.findTournamentsBySignUpDeadline(now, Status.UPCOMING);

        for (Tournament tournament : tournaments) {
            matchmakingService.runMatchmaking(tournament);
        }
    }
}
