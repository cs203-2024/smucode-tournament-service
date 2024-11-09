package com.cs203.smucode.factories;

import com.cs203.smucode.constants.NotificationCategory;
import com.cs203.smucode.constants.NotificationType;
import com.cs203.smucode.consumers.NotificationServiceConsumer;
import com.cs203.smucode.mappers.NotificationMapper;
import com.cs203.smucode.models.events.Event;
import com.cs203.smucode.models.events.RegistrationAcceptedEvent;
import com.cs203.smucode.models.events.RegistrationRejectedEvent;
import com.cs203.smucode.models.events.SignupClosedEvent;
import com.cs203.smucode.services.TournamentService;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
public class EventFactory {
    private final TournamentService tournamentService;
    private final NotificationServiceConsumer notificationServiceConsumer;
    private final NotificationMapper notificationMapper;

    public EventFactory(TournamentService tournamentService,
                        NotificationServiceConsumer notificationServiceConsumer,
                        NotificationMapper notificationMapper) {
        this.tournamentService = tournamentService;
        this.notificationServiceConsumer = notificationServiceConsumer;
        this.notificationMapper = notificationMapper;
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
        event.publish(notificationServiceConsumer, notificationMapper);
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
        event.publish(notificationServiceConsumer, notificationMapper);
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
        event.publish(notificationServiceConsumer, notificationMapper);
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
        event.publish(notificationServiceConsumer, notificationMapper);
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
        event.publish(notificationServiceConsumer, notificationMapper);
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
        event.publish(notificationServiceConsumer, notificationMapper);
        return event;

    }
}
