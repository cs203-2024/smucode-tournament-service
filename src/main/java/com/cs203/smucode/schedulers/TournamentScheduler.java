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

@Component
public class TournamentScheduler {

    private static final Logger logger = LoggerFactory.getLogger(TournamentScheduler.class);
    private final TournamentService tournamentService;
    private final MatchmakingService matchmakingService;

    @Autowired
    public TournamentScheduler(
            TournamentService tournamentService,
            MatchmakingService matchmakingService,
            TournamentMapper tournamentMapper ) {
        this.tournamentService = tournamentService;
        this.matchmakingService = matchmakingService;
    }

    @Scheduled(cron = "0/30 * * * * ?") //Runs every 30 minutes
    public void scheduleMatchmaking() {
        LocalDateTime now = LocalDateTime.now();
        List<Tournament> tournaments = tournamentService.findTournamentsBySignUpDeadline(now);

        for (Tournament tournament : tournaments) {

            logger.info("current tournament: {}", tournament);

            // Do not start tournament if tournament does not have enough signups
            if (tournament.getSignups().size() < tournament.getCapacity()) {
                logger.info("Tournament with id {} does not have enough signups", tournament.getId());
                logger.info("Required: {}, Has: {}", tournament.getCapacity(), tournament.getSignups().size());
                continue;
            }

            matchmakingService.runMatchmaking(tournament);
        }

//        Tournament tournament = tournamentService.findTournamentById(UUID.fromString("e2454bc0-58ac-4a81-81c4-544d159cc8b6"));
//        if (tournament.getSignups().size() < tournament.getCapacity()) {
//            logger.info("Tounrnament signups: {}", tournament.getSignups());
//            logger.info("Tournament with id {} does not have enough signups", tournament.getId());
//            logger.info("Required: {}, Has: {}", tournament.getCapacity(), tournament.getSignups().size());
//            return;
//        }
//        matchmakingService.runMatchmaking(tournament);

    }
}
