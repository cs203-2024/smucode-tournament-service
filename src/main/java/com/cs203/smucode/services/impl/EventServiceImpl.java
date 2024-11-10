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

@Service
public class EventServiceImpl implements EventService {
    private static final Logger logger = LoggerFactory.getLogger(EventServiceImpl.class);

    private final EventFactory eventFactory;
    private final NotificationServiceConsumer notificationServiceConsumer;
    private final NotificationMapper notificationMapper;

    @Autowired
    public EventServiceImpl(EventFactory eventFactory,
                            NotificationServiceConsumer notificationServiceConsumer,
                            NotificationMapper notificationMapper) {
        this.eventFactory = eventFactory;
        this.notificationServiceConsumer = notificationServiceConsumer;
        this.notificationMapper = notificationMapper;
    }

    public void handleSignupClosedEvent(UUID tournamentId, String tournamentName, String message) {
        Event event = eventFactory.createSignupClosedEvent(tournamentId, tournamentName, message);
        publishEvent(event);
    }

    public void handleRegistrationAcceptedEvent(UUID tournamentId, String tournamentName, String message) {
        Event event = eventFactory.createRegistrationAcceptedEvent(tournamentId, tournamentName, message);
        publishEvent(event);
    }

    public void handleRegistrationRejectedEvent(UUID tournamentId, String tournamentName, String message) {
        Event event = eventFactory.createRegistrationRejectedEvent(tournamentId, tournamentName, message);
        publishEvent(event);
    }

    public void handleRoundEndEvent(UUID tournamentId, String tournamentName, String message) {
        Event event = eventFactory.createRoundEndEvent(tournamentId, tournamentName, message);
        publishEvent(event);
    }

    public void handleTournamentEndEvent(UUID tournamentId, String tournamentName, String message) {
        Event event = eventFactory.createTournamentEndEvent(tournamentId, tournamentName, message);
        publishEvent(event);
    }

    public void handleBracketCompletedEvent(UUID tournamentId, String tournamentName, String message) {
        Event event = eventFactory.createBracketCompletedEvent(tournamentId, tournamentName, message);
        publishEvent(event);
    }

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
