package com.cs203.smucode.services;

import com.cs203.smucode.factories.EventFactory;
import com.cs203.smucode.models.events.Event;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
public interface EventService {
    void handleSignupClosedEvent(UUID tournamentId, String tournamentName, String message);

    void handleRegistrationAcceptedEvent(UUID tournamentId, String tournamentName, String message);

    void handleRegistrationRejectedEvent(UUID tournamentId, String tournamentName, String message);

    void handleRoundEndEvent(UUID tournamentId, String tournamentName, String message);

    void handleTournamentEndEvent(UUID tournamentId, String tournamentName, String message);

    void handleBracketCompletedEvent(UUID tournamentId, String tournamentName, String message);

}
