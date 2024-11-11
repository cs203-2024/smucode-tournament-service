package com.cs203.smucode.models.events;

import com.cs203.smucode.constants.NotificationCategory;
import com.cs203.smucode.constants.NotificationType;
import com.cs203.smucode.models.Bracket;

import java.util.ArrayList;
import java.util.List;

/**
 * Event triggered when there is suspicious activity in a bracket
 * This event sends a notification to organiser of tournament.
 */
public class SuspiciousActivityEvent extends Event {
    private final Bracket bracket;
    public SuspiciousActivityEvent(Bracket bracket,
                                   String message,
                                   NotificationType type,
                                   NotificationCategory category) {
        super(bracket.getTournament().getId(), bracket.getTournament().getName(), message, type, category);
        this.bracket = bracket;
    }

    /**
     * Sets the recipients for this event to be tournament organiser.
     *
     * @throws IllegalStateException if the tournament has no participants.
     */
    @Override
    public void setRecipients() {
        String organiser = bracket.getTournament().getOrganiser();
        if (organiser == null) {
            throw new IllegalArgumentException("Tournament" + bracket.getTournament().getId() + " has no organiser");
        }

        List<String> eventRecipients = new ArrayList<>();
        eventRecipients.add(organiser);
        recipients = eventRecipients;
    }

}
