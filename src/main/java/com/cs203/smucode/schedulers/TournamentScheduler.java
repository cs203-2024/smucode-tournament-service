package com.cs203.smucode.schedulers;

import com.cs203.smucode.models.Tournament;
import com.cs203.smucode.services.TournamentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.context.annotation.Configuration;

import java.time.LocalDateTime;
import java.util.List;

@Configuration
public class TournamentScheduler {

    @Autowired
    private TournamentService tournamentService;

    @Scheduled(fixedRate = 30000) // Check every 30secs
    public void runScheduledMatchmaking() {
        LocalDateTime now = LocalDateTime.now();
        List<Tournament> tournaments = tournamentService.findAllTournaments();

        for (Tournament tournament : tournaments) {
            if (tournament.getStatus().equals("pending") && tournament.getStartDate().isBefore(now)) {
                // Start the tournament
                tournamentService.runMatchmaking(tournament.getId());
                tournament.setStatus("in_progress");
                tournamentService.saveTournament(tournament);
            }
        }
    }
}
