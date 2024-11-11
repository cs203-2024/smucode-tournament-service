package com.cs203.smucode.services.impl;

import com.cs203.smucode.consumers.NotificationServiceConsumer;
import com.cs203.smucode.factories.EventFactory;
import com.cs203.smucode.mappers.NotificationMapper;
import com.cs203.smucode.models.Bracket;
import com.cs203.smucode.models.Round;
import com.cs203.smucode.models.Tournament;
import com.cs203.smucode.models.events.Event;
import com.cs203.smucode.services.EventService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;


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
     * @param tournament The tournament which signup has closed.
     * @param message The message to include in the event.
     */
    public void handleSignupClosedEvent(Tournament tournament, String message) {
        Event event = eventFactory.createSignupClosedEvent(tournament, message);
        publishEvent(event);
    }

    /**
     * Handles the RegistrationAccepted event by creating and publishing it.
     *
     * @param tournament The registered tournament;
     * @param message The message to include in the event.
     */
    public void handleRegistrationAcceptedEvent(Tournament tournament, String message) {
        Event event = eventFactory.createRegistrationAcceptedEvent(tournament, message);
        publishEvent(event);
    }

    /**
     * Handles the RegistrationRejected event by creating and publishing it.
     *
     * @param tournament The registered tournament.
     * @param message The message to include in the event.
     */
    public void handleRegistrationRejectedEvent(Tournament tournament, String message) {
        Event event = eventFactory.createRegistrationRejectedEvent(tournament, message);
        publishEvent(event);
    }

    /**
     * Handles the BracketCompleted event by creating and publishing it.
     *
     * @param bracket The bracket which has completed.
     * @param message The message to include in the event.
     */
    public void handleBracketCompletedEvent(Bracket bracket, String message) {
        Event event = eventFactory.createBracketCompletedEvent(bracket, message);
        publishEvent(event);
    }

    /**
     * Handles the RoundStarted event by creating and publishing it.
     *
     * @param round The round which has started.
     * @param message The message to include in the event.
     */
    public void handleRoundStartedEvent(Round round, String message) {
        Event event = eventFactory.createRoundStartedEvent(round, message);
        publishEvent(event);
    }

    /**
     * Handles the RoundEnded event by creating and publishing it.
     *
     * @param round The round which has ended.
     * @param message The message to include in the event.
     */
    public void handleRoundEndedEvent(Round round, String message) {
        Event event = eventFactory.createRoundEndedEvent(round, message);
        publishEvent(event);
    }

    /**
     * Handles the TournamentStarted event by creating and publishing it.
     *
     * @param tournament The tournament which has started.
     * @param message The message to include in the event.
     */
    public void handleTournamentStartedEvent(Tournament tournament, String message) {
        Event event = eventFactory.createTournamentStartedEvent(tournament, message);
        publishEvent(event);
    }

    /**
     * Handles the TournamentEnded event by creating and publishing it.
     *
     * @param tournament The tournament which has ended.
     * @param message The message to include in the event.
     */
    public void handleTournamentEndedEvent(Tournament tournament, String message) {
        Event event = eventFactory.createTournamentEndedEvent(tournament, message);
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
