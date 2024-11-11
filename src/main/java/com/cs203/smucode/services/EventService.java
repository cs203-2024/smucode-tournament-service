package com.cs203.smucode.services;

import org.springframework.stereotype.Service;

import java.util.UUID;

/**
 * Service interface for handling tournament-related events.
 * Defines methods for various event types that the tournament application may generate,
 * including signup closed, registration status, round start/end, and tournament events.
 */
@Service
public interface EventService {
    void handleSignupClosedEvent(UUID tournamentId, String tournamentName, String message);

    void handleRegistrationAcceptedEvent(UUID tournamentId, String tournamentName, String message);

    void handleRegistrationRejectedEvent(UUID tournamentId, String tournamentName, String message);

    void handleRoundEndedEvent(UUID tournamentId, String tournamentName, String message);

    void handleTournamentEndedEvent(UUID tournamentId, String tournamentName, String message);

    void handleBracketCompletedEvent(UUID tournamentId, String tournamentName, String message);

}
