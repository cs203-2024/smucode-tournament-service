package com.cs203.smucode.factories;

import com.cs203.smucode.constants.NotificationCategory;
import com.cs203.smucode.constants.NotificationType;
import com.cs203.smucode.models.events.Event;
import com.cs203.smucode.models.events.RegistrationAcceptedEvent;
import com.cs203.smucode.models.events.RegistrationRejectedEvent;
import com.cs203.smucode.models.events.SignupClosedEvent;
import com.cs203.smucode.services.TournamentService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
public class EventFactory {
    private static final Logger logger = LoggerFactory.getLogger(EventFactory.class);

    private final TournamentService tournamentService;

    @Autowired
    public EventFactory(@Lazy TournamentService tournamentService) { // TODO: Is @Lazy the best way to resolve circular dependency
        this.tournamentService = tournamentService;
    }

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

    public Event createRoundEndEvent(UUID tournamentId, String tournamentName, String message) {
        RegistrationRejectedEvent event = new RegistrationRejectedEvent(
                tournamentId,
                tournamentName,
                message,
                NotificationType.ROUND_END,
                NotificationCategory.GENERAL
        );
        event.setRecipients(tournamentService);
        logger.info("Created RoundEndEvent {} for tournament {}", event, tournamentId);
        return event;

    }

    public Event createTournamentEndEvent(UUID tournamentId, String tournamentName, String message) {
        RegistrationRejectedEvent event = new RegistrationRejectedEvent(
                tournamentId,
                tournamentName,
                message,
                NotificationType.TOURNAMENT_END,
                NotificationCategory.GENERAL
        );
        event.setRecipients(tournamentService);
        logger.info("Created TournamentEndEvent {} for tournament {}", event, tournamentId);
        return event;

    }

    public Event createBracketCompletedEvent(UUID tournamentId, String tournamentName, String message) {
        RegistrationRejectedEvent event = new RegistrationRejectedEvent(
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

