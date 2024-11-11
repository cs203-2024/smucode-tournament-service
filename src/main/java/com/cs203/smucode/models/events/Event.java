package com.cs203.smucode.models.events;

import com.cs203.smucode.constants.NotificationCategory;
import com.cs203.smucode.constants.NotificationType;
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
     */
    protected abstract void setRecipients();

}
