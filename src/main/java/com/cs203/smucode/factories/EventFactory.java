package com.cs203.smucode.factories;

import com.cs203.smucode.constants.NotificationCategory;
import com.cs203.smucode.constants.NotificationType;
import com.cs203.smucode.models.Bracket;
import com.cs203.smucode.models.Round;
import com.cs203.smucode.models.Tournament;
import com.cs203.smucode.models.events.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

/**
 * Factory class for creating various types of events in the tournament service.
 * This class constructs event instances with specific types, recipients, and messages,
 * and uses TournamentService to retrieve relevant recipients for notifications.
 */
@Component
public class EventFactory {
    private static final Logger logger = LoggerFactory.getLogger(EventFactory.class);

    /**
     * Creates a SignupClosedEvent for a specific tournament.
     *
     * @param tournament The tournament which signup closed.
     * @param message The message to include in the event.
     * @return A SignupClosedEvent with set recipients and notification type.
     */
    // TODO: Find way to overcome duplicate SignupClosed event
    public Event createSignupClosedEvent(Tournament tournament, String message) {
        SignupClosedEvent event = new SignupClosedEvent(
                tournament,
                message,
                NotificationType.SIGNUP_CLOSED,
                NotificationCategory.GENERAL
        );
        event.setRecipients();
        logger.info("Created SignupClosedEvent {} for tournament {}", event, event.getTournamentId());
        return event;
    }

    /**
     * Creates a RegistrationAcceptedEvent for a specific tournament.
     *
     * @param tournament The registered tournament.
     * @param message The message to include in the event.
     * @return A RegistrationAcceptedEvent with set recipients and notification type.
     */
    public Event createRegistrationAcceptedEvent(Tournament tournament,
                                                 String message) {
        RegistrationAcceptedEvent event = new RegistrationAcceptedEvent(
                tournament,
                message,
                NotificationType.REGISTRATION_ACCEPTED,
                NotificationCategory.GENERAL
        );
        event.setRecipients();
        logger.info("Created RegistrationAcceptedEvent {} for tournament {}", event, event.getTournamentId());
        return event;
    }

    /**
     * Creates a RegistrationRejectedEvent for a specific tournament.
     *
     * @param tournament The registered tournament.
     * @param message The message to include in the event.
     * @return A RegistrationRejectedEvent with set recipients and notification type.
     */
    public Event createRegistrationRejectedEvent(Tournament tournament, String message) {
        RegistrationRejectedEvent event = new RegistrationRejectedEvent(
                tournament,
                message,
                NotificationType.REGISTRATION_REJECTED,
                NotificationCategory.GENERAL
        );
        event.setRecipients();
        logger.info("Created RegistrationRejectedEvent {} for tournament {}", event, event.getTournamentId());
        return event;
    }

    /**
     * Creates a RoundStartedEvent for a specific tournament.
     *
     * @param round The round that has started.
     * @param message The message to include in the event.
     * @return A RoundStartedEvent with set recipients and notification type.
     */
    public Event createRoundStartedEvent(Round round, String message) {
        RoundStartedEvent event = new RoundStartedEvent(
                round,
                message,
                NotificationType.ROUND_STARTED,
                NotificationCategory.GENERAL
        );
        event.setRecipients();
        logger.info("Created RoundStartedEvent {} for tournament {}", event, event.getTournamentId());
        return event;

    }

    /**
     * Creates a RoundEndedEvent for a specific tournament.
     *
     * @param round The round which has ended.
     * @param message The message to include in the event.
     * @return A RoundEndedEvent with set recipients and notification type.
     */
    public Event createRoundEndedEvent(Round round, String message) {
        RoundEndedEvent event = new RoundEndedEvent(
                round,
                message,
                NotificationType.ROUND_ENDED,
                NotificationCategory.GENERAL
        );
        event.setRecipients();
        logger.info("Created RoundEndedEvent {} for tournament {}", event, event.getTournamentId());
        return event;

    }

    /**
     * Creates a TournamentStartedEvent for a specific tournament.
     *
     * @param tournament The tournament which has started.
     * @param message The message to include in the event.
     * @return A SignupClosedEvent with set recipients and notification type.
     */
    public Event createTournamentStartedEvent(Tournament tournament, String message) {
        TournamentStartedEvent event = new TournamentStartedEvent(
                tournament,
                message,
                NotificationType.TOURNAMENT_STARTED,
                NotificationCategory.GENERAL
        );
        event.setRecipients();
        logger.info("Created TournamentStartedEvent {} for tournament {}", event, event.getTournamentId());
        return event;

    }

    /**
     * Creates a TournamentEndedEvent for a specific tournament.
     *
     * @param tournament The tournament which has ended.
     * @param message The message to include in the event.
     * @return A TournamentEndedEvent with set recipients and notification type.
     */
    public Event createTournamentEndedEvent(Tournament tournament, String message) {
        TournamentEndedEvent event = new TournamentEndedEvent(
                tournament,
                message,
                NotificationType.TOURNAMENT_ENDED,
                NotificationCategory.GENERAL
        );
        event.setRecipients();
        logger.info("Created TournamentEndedEvent {} for tournament {}", event, event.getTournamentId());
        return event;

    }

    /**
     * Creates a BracketCompletedEvent for a specific tournament.
     *
     * @param bracket The bracket which has completed.
     * @param message The message to include in the event.
     * @return A BracketCompletedEvent with set recipients and notification type.
     */
    public Event createBracketCompletedEvent(Bracket bracket, String message) {
        BracketCompletedEvent event = new BracketCompletedEvent(
                bracket,
                message,
                NotificationType.BRACKET_COMPLETED,
                NotificationCategory.GENERAL
        );
        event.setRecipients();
        logger.info("Created BracketCompletedEvent {} for tournament {}", event, event.getTournamentId());
        return event;

    }

    /**
     * Creates a BracketCompletedEvent for a specific tournament.
     *
     * @param bracket The bracket which has completed.
     * @param message The message to include in the event.
     * @return A BracketCompletedEvent with set recipients and notification type.
     */
    public Event createSuspiciousActivityEvent(Bracket bracket, String message) {
        SuspiciousActivityEvent event = new SuspiciousActivityEvent(
                bracket,
                message,
                NotificationType.BRACKET_COMPLETED,
                NotificationCategory.GENERAL
        );
        event.setRecipients();
        logger.info("Created SuspiciousActivityEvent {} for bracket {} in tournament {}",
                event,
                bracket.getId(),
                event.getTournamentId());
        return event;

    }
}

