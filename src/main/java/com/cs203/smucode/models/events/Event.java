package com.cs203.smucode.models.events;

import com.cs203.smucode.constants.NotificationCategory;
import com.cs203.smucode.constants.NotificationType;
import com.cs203.smucode.consumers.NotificationServiceConsumer;
import com.cs203.smucode.mappers.NotificationMapper;
import com.cs203.smucode.services.TournamentService;
import lombok.Data;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

/**
 * Abstract class representing an event associated with a tournament.
 * This class is used as a base for all event types, where each event
 * can have different recipients associated with it.
 */
@Data
public abstract class Event {
    private static final Logger logger = LoggerFactory.getLogger(Event.class);

    protected UUID tournamentId;
    protected String tournamentName;
    protected String message;
    protected NotificationType type;
    protected NotificationCategory category;
    protected List<String> recipients;

    /**
     * Constructs an Event instance with the specified tournament details.
     *
     * @param tournamentId   the unique identifier of the tournament.
     * @param tournamentName the name of the tournament.
     * @param message        the message to be sent with the event.
     * @param type           the type of notification (e.g. ROUND_END, TOURNAMENT_END).
     * @param category       the category of the notification (e.g. GENERAL, ALERT).
     */
    public Event(UUID tournamentId,
                 String tournamentName,
                 String message,
                 NotificationType type,
                 NotificationCategory category) {
        this.tournamentId = tournamentId;
        this.tournamentName = tournamentName;
        this.message = message;
        this.type = type;
        this.category = category;
        this.recipients = new ArrayList<>();
    }

    /**
     * Abstract method that sets the recipients of this event.
     * Subclasses should implement this method to specify how recipients
     * are determined based on tournament data.
     *
     * @param tournamentService the service used to retrieve tournament details.
     */
    protected abstract void setRecipients(TournamentService tournamentService);

    /**
     * Publishes the event by sending notifications to the recipients.
     * This method uses the provided notification service consumer and mapper
     * to convert the event into a notification and stream it to the appropriate
     * recipients.
     *
     * @param notificationServiceConsumer the consumer responsible for sending notifications.
     * @param notificationMapper          the mapper that converts the event into a notification DTO.
     * @return the current event instance after it has been published.
     */
    public Event publish(NotificationServiceConsumer notificationServiceConsumer,
                         NotificationMapper notificationMapper) {
        if (recipients.isEmpty()) {
            logger.warn("No recipients found for event: {}", this);
            return null;
        }

        logger.info("Publishing event: {}", this);
        notificationServiceConsumer.streamNotifications(notificationMapper.eventToNotificationDTO(this));
        return this;
    }

}
