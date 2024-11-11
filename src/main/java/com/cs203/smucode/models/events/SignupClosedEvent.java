package com.cs203.smucode.models.events;

import com.cs203.smucode.constants.NotificationCategory;
import com.cs203.smucode.constants.NotificationType;
import com.cs203.smucode.models.Tournament;

import java.util.ArrayList;
import java.util.List;

/**
 * Event triggered when the signups for a tournament are closed.
 * This event sends a notification to the tournament organiser to inform them that signups are closed.
 */
public class SignupClosedEvent extends Event {
    private final Tournament tournament;
    public SignupClosedEvent(Tournament tournament,
                             String message,
                             NotificationType type,
                             NotificationCategory category) {
        super(tournament.getId(), tournament.getName(), message, type, category);
        this.tournament = tournament;
    }

    /**
     * Sets the recipients for this event to be the tournament organiser.
     *
     * @throws IllegalStateException if the organiser is null.
     */
    @Override
    public void setRecipients() {
        String organiser = tournament.getOrganiser();

        if (organiser == null || organiser.isEmpty()) {
            throw new IllegalArgumentException("Tournament " + tournament.getId() + " does not have an organiser");
        }

        List<String> eventRecipients = new ArrayList<>();
        eventRecipients.add(organiser);
        recipients = eventRecipients;
    }
}
