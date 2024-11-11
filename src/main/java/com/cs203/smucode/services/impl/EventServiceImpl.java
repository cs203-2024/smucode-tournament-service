package com.cs203.smucode.services.impl;

import com.cs203.smucode.consumers.NotificationServiceConsumer;
import com.cs203.smucode.factories.EventFactory;
import com.cs203.smucode.mappers.NotificationMapper;
import com.cs203.smucode.models.events.Event;
import com.cs203.smucode.services.EventService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.UUID;

/**
 * Implementation of the EventService interface.
 * Handles the creation and publication of tournament-related events
 * by using EventFactory to create events and NotificationServiceConsumer to publish them.
 */
@Service
public class EventServiceImpl implements EventService {
    private static final Logger logger = LoggerFactory.getLogger(EventServiceImpl.class);

    private final EventFactory eventFactory;
    private final NotificationServiceConsumer notificationServiceConsumer;
    private final NotificationMapper notificationMapper;

    /**
     * Constructor for EventServiceImpl.
     *
     * @param eventFactory Factory to create specific types of events.
     * @param notificationServiceConsumer Consumer responsible for streaming notifications.
     * @param notificationMapper Mapper to transform events into notification DTOs.
     */
    @Autowired
    public EventServiceImpl(EventFactory eventFactory,
                            NotificationServiceConsumer notificationServiceConsumer,
                            NotificationMapper notificationMapper) {
        this.eventFactory = eventFactory;
        this.notificationServiceConsumer = notificationServiceConsumer;
        this.notificationMapper = notificationMapper;
    }

    /**
     * Handles the SignupClosed event by creating and publishing it.
     *
     * @param tournamentId The ID of the tournament.
     * @param tournamentName The name of the tournament.
     * @param message The message to include in the event.
     */
    public void handleSignupClosedEvent(UUID tournamentId, String tournamentName, String message) {
        Event event = eventFactory.createSignupClosedEvent(tournamentId, tournamentName, message);
        publishEvent(event);
    }

    /**
     * Handles the RegistrationAccepted event by creating and publishing it.
     *
     * @param tournamentId The ID of the tournament.
     * @param tournamentName The name of the tournament.
     * @param message The message to include in the event.
     */
    public void handleRegistrationAcceptedEvent(UUID tournamentId, String tournamentName, String message) {
        Event event = eventFactory.createRegistrationAcceptedEvent(tournamentId, tournamentName, message);
        publishEvent(event);
    }

    /**
     * Handles the RegistrationRejected event by creating and publishing it.
     *
     * @param tournamentId The ID of the tournament.
     * @param tournamentName The name of the tournament.
     * @param message The message to include in the event.
     */
    public void handleRegistrationRejectedEvent(UUID tournamentId, String tournamentName, String message) {
        Event event = eventFactory.createRegistrationRejectedEvent(tournamentId, tournamentName, message);
        publishEvent(event);
    }

    /**
     * Handles the BracketCompleted event by creating and publishing it.
     *
     * @param tournamentId The ID of the tournament.
     * @param tournamentName The name of the tournament.
     * @param message The message to include in the event.
     */
    public void handleBracketCompletedEvent(UUID tournamentId, String tournamentName, String message) {
        Event event = eventFactory.createBracketCompletedEvent(tournamentId, tournamentName, message);
        publishEvent(event);
    }

    /**
     * Handles the RoundStarted event by creating and publishing it.
     *
     * @param tournamentId The ID of the tournament.
     * @param tournamentName The name of the tournament.
     * @param message The message to include in the event.
     */
    public void handleRoundStartedEvent(UUID tournamentId, String tournamentName, String message) {
        Event event = eventFactory.createRoundStartedEvent(tournamentId, tournamentName, message);
        publishEvent(event);
    }

    /**
     * Handles the RoundEnded event by creating and publishing it.
     *
     * @param tournamentId The ID of the tournament.
     * @param tournamentName The name of the tournament.
     * @param message The message to include in the event.
     */
    public void handleRoundEndedEvent(UUID tournamentId, String tournamentName, String message) {
        Event event = eventFactory.createRoundEndedEvent(tournamentId, tournamentName, message);
        publishEvent(event);
    }

    /**
     * Handles the TournamentStarted event by creating and publishing it.
     *
     * @param tournamentId The ID of the tournament.
     * @param tournamentName The name of the tournament.
     * @param message The message to include in the event.
     */
    public void handleTournamentStartedEvent(UUID tournamentId, String tournamentName, String message) {
        Event event = eventFactory.createTournamentStartedEvent(tournamentId, tournamentName, message);
        publishEvent(event);
    }

    /**
     * Handles the TournamentEnded event by creating and publishing it.
     *
     * @param tournamentId The ID of the tournament.
     * @param tournamentName The name of the tournament.
     * @param message The message to include in the event.
     */
    public void handleTournamentEndedEvent(UUID tournamentId, String tournamentName, String message) {
        Event event = eventFactory.createTournamentEndedEvent(tournamentId, tournamentName, message);
        publishEvent(event);
    }

//    Helper Methods
    /**
     * Publishes the event by sending notifications to the recipients.
     * This method uses notification service consumer and mapper
     * to convert the event into a notification and stream it to the appropriate
     * recipients.
     *
     * @param event to be published
     */
    private void publishEvent(Event event) {

        if (event.getRecipients().isEmpty()) {
            logger.warn("No recipients found for event: {}", this);
            return;
        }

        notificationServiceConsumer.streamNotifications(notificationMapper.eventToNotificationDTO(event));
        logger.info("Published event {}", event);
    }
}
