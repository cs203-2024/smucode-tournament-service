package com.cs203.smucode.factories;

import com.cs203.smucode.constants.NotificationCategory;
import com.cs203.smucode.constants.NotificationType;
import com.cs203.smucode.models.events.*;
import com.cs203.smucode.services.TournamentService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Component;

import java.util.UUID;

/**
 * Factory class for creating various types of events in the tournament service.
 * This class constructs event instances with specific types, recipients, and messages,
 * and uses TournamentService to retrieve relevant recipients for notifications.
 */
@Component
public class EventFactory {
    private static final Logger logger = LoggerFactory.getLogger(EventFactory.class);

    private final TournamentService tournamentService;

    /**
     * Constructor for EventFactory.
     * @param tournamentService The service to retrieve tournament-related data, injected lazily to avoid circular dependencies.
     */
    @Autowired
    public EventFactory(@Lazy TournamentService tournamentService) { // TODO: Is @Lazy the best way to resolve circular dependency
        this.tournamentService = tournamentService;
    }

    /**
     * Creates a SignupClosedEvent for a specific tournament.
     * @param tournamentId The ID of the tournament.
     * @param tournamentName The name of the tournament.
     * @param message The message to include in the event.
     * @return A SignupClosedEvent with set recipients and notification type.
     */
    // TODO: Find way to overcome duplicate SignupClosed event
    public Event createSignupClosedEvent(UUID tournamentId, String tournamentName, String message) {
        SignupClosedEvent event = new SignupClosedEvent(
                tournamentId,
                tournamentName,
                message,
                NotificationType.SIGNUP_CLOSED,
                NotificationCategory.GENERAL
        );
        event.setRecipients(tournamentService);
        logger.info("Created SignupClosedEvent {} for tournament {}", event, tournamentId);
        return event;
    }

    /**
     * Creates a RegistrationAcceptedEvent for a specific tournament.
     * @param tournamentId The ID of the tournament.
     * @param tournamentName The name of the tournament.
     * @param message The message to include in the event.
     * @return A RegistrationAcceptedEvent with set recipients and notification type.
     */
    public Event createRegistrationAcceptedEvent(UUID tournamentId, String tournamentName, String message) {
        RegistrationAcceptedEvent event = new RegistrationAcceptedEvent(
                tournamentId,
                tournamentName,
                message,
                NotificationType.REGISTRATION_ACCEPTED,
                NotificationCategory.GENERAL
        );
        event.setRecipients(tournamentService);
        logger.info("Created RegistrationAcceptedEvent {} for tournament {}", event, tournamentId);
        return event;
    }

    /**
     * Creates a RegistrationRejectedEvent for a specific tournament.
     * @param tournamentId The ID of the tournament.
     * @param tournamentName The name of the tournament.
     * @param message The message to include in the event.
     * @return A RegistrationRejectedEvent with set recipients and notification type.
     */
    public Event createRegistrationRejectedEvent(UUID tournamentId, String tournamentName, String message) {
        RegistrationRejectedEvent event = new RegistrationRejectedEvent(
                tournamentId,
                tournamentName,
                message,
                NotificationType.REGISTRATION_REJECTED,
                NotificationCategory.GENERAL
        );
        event.setRecipients(tournamentService);
        logger.info("Created RegistrationRejectedEvent {} for tournament {}", event, tournamentId);
        return event;
    }

    /**
     * Creates a RoundStartedEvent for a specific tournament.
     * @param tournamentId The ID of the tournament.
     * @param tournamentName The name of the tournament.
     * @param message The message to include in the event.
     * @return A RoundStartedEvent with set recipients and notification type.
     */
    public Event createRoundStartedEvent(UUID tournamentId, String tournamentName, String message) {
        RoundStartedEvent event = new RoundStartedEvent(
                tournamentId,
                tournamentName,
                message,
                NotificationType.ROUND_STARTED,
                NotificationCategory.GENERAL
        );
        event.setRecipients(tournamentService);
        logger.info("Created RoundStartedEvent {} for tournament {}", event, tournamentId);
        return event;

    }

    /**
     * Creates a RoundEndedEvent for a specific tournament.
     * @param tournamentId The ID of the tournament.
     * @param tournamentName The name of the tournament.
     * @param message The message to include in the event.
     * @return A RoundEndedEvent with set recipients and notification type.
     */
    public Event createRoundEndedEvent(UUID tournamentId, String tournamentName, String message) {
        RoundEndedEvent event = new RoundEndedEvent(
                tournamentId,
                tournamentName,
                message,
                NotificationType.ROUND_ENDED,
                NotificationCategory.GENERAL
        );
        event.setRecipients(tournamentService);
        logger.info("Created RoundEndedEvent {} for tournament {}", event, tournamentId);
        return event;

    }

    /**
     * Creates a SignupClosedEvent for a specific tournament.
     * @param tournamentId The ID of the tournament.
     * @param tournamentName The name of the tournament.
     * @param message The message to include in the event.
     * @return A SignupClosedEvent with set recipients and notification type.
     */
    public Event createTournamentStartedEvent(UUID tournamentId, String tournamentName, String message) {
        TournamentStartedEvent event = new TournamentStartedEvent(
                tournamentId,
                tournamentName,
                message,
                NotificationType.TOURNAMENT_STARTED,
                NotificationCategory.GENERAL
        );
        event.setRecipients(tournamentService);
        logger.info("Created TournamentStartedEvent {} for tournament {}", event, tournamentId);
        return event;

    }

    /**
     * Creates a TournamentEndedEvent for a specific tournament.
     * @param tournamentId The ID of the tournament.
     * @param tournamentName The name of the tournament.
     * @param message The message to include in the event.
     * @return A TournamentEndedEvent with set recipients and notification type.
     */
    public Event createTournamentEndedEvent(UUID tournamentId, String tournamentName, String message) {
        TournamentEndedEvent event = new TournamentEndedEvent(
                tournamentId,
                tournamentName,
                message,
                NotificationType.TOURNAMENT_ENDED,
                NotificationCategory.GENERAL
        );
        event.setRecipients(tournamentService);
        logger.info("Created TournamentEndedEvent {} for tournament {}", event, tournamentId);
        return event;

    }

    /**
     * Creates a BracketCompletedEvent for a specific tournament.
     * @param tournamentId The ID of the tournament.
     * @param tournamentName The name of the tournament.
     * @param message The message to include in the event.
     * @return A BracketCompletedEvent with set recipients and notification type.
     */
    public Event createBracketCompletedEvent(UUID tournamentId, String tournamentName, String message) {
        BracketCompletedEvent event = new BracketCompletedEvent(
                tournamentId,
                tournamentName,
                message,
                NotificationType.BRACKET_COMPLETED,
                NotificationCategory.GENERAL
        );
        event.setRecipients(tournamentService);
        logger.info("Created BracketCompletedEvent {} for tournament {}", event, tournamentId);
        return event;

    }
}

