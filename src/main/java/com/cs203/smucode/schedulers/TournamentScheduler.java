package com.cs203.smucode.schedulers;

import com.cs203.smucode.mappers.TournamentMapper;
import com.cs203.smucode.models.Tournament;
import com.cs203.smucode.services.MatchmakingService;
import com.cs203.smucode.services.TournamentService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.List;

/**
 * @author jeremaine
 * @version 1.0
 * @since 2024-09-20
 *
 * This class is used to manage tournament events.
 */

@Component
public class TournamentScheduler {

    private static final Logger logger = LoggerFactory.getLogger(TournamentScheduler.class);
    private final TournamentService tournamentService;
    private final MatchmakingService matchmakingService;

    @Autowired
    public TournamentScheduler(
            TournamentService tournamentService,
            MatchmakingService matchmakingService) {
        this.tournamentService = tournamentService;
        this.matchmakingService = matchmakingService;
    }

    @Scheduled(cron = "0/30 * * * * ?") //Runs every 30 minutes
    public void scheduleMatchmaking() {
        LocalDateTime now = LocalDateTime.now();
        List<Tournament> tournaments = tournamentService.findTournamentsWithSignUpBefore(now);
        logger.info("Tournaments to undergo matchmaking:  {}", tournaments.size());

        for (Tournament tournament : tournaments) {

            logger.info("Current tournament undergoing matchmaking: {}", tournament);

            // Do not start tournament if tournament does not have enough signups
            if (tournament.getSignups().size() < tournament.getCapacity()) {
                logger.info("Tournament with id {} does not have enough signups - Required: {}, Has: {}",
                        tournament.getId(), tournament.getCapacity(), tournament.getSignups());
                continue;
            }

            matchmakingService.runMatchmaking(tournament);
        }

    }
}
