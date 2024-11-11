package com.cs203.smucode.services;

import com.cs203.smucode.models.Bracket;
import com.cs203.smucode.models.Round;
import com.cs203.smucode.models.Tournament;
import org.springframework.stereotype.Service;

/**
 * Service interface for handling tournament-related events.
 * Defines methods for various event types that the tournament application may generate,
 * including signup closed, registration status, round start/end, and tournament events.
 */
@Service
public interface EventService {

    void handleRegistrationAcceptedEvent(Tournament tournament, String message);

    void handleRegistrationRejectedEvent(Tournament tournament, String message);

    void handleBracketCompletedEvent(Bracket bracket, String message);

    void handleRoundEndedEvent(Round round, String message);

    void handleTournamentStartedEvent(Tournament tournament, String message);

    void handleTournamentEndedEvent(Tournament tournament, String message);

    void handleSuspiciousActivityEvent(Bracket bracket, String message);

}
