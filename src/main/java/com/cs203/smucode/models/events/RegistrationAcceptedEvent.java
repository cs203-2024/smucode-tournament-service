package com.cs203.smucode.models.events;

import com.cs203.smucode.constants.NotificationCategory;
import com.cs203.smucode.constants.NotificationType;
import com.cs203.smucode.models.Tournament;

import java.util.ArrayList;

/**
 * Event triggered when a player's registration is accepted in a tournament.
 * This event sends a notification to all participants of the tournament.
 */
public class RegistrationAcceptedEvent extends Event {
    private final Tournament tournament;
    public RegistrationAcceptedEvent(
            Tournament tournament,
            String message,
            NotificationType type,
            NotificationCategory category) {
        super(tournament.getId(), tournament.getName(), message, type, category);
        this.tournament = tournament;
    }

    /**
     * Sets the recipients for this event to be the participants of the tournament.
     *
     * @throws IllegalStateException if the tournament has no participants.
     */
    @Override
    public void setRecipients() {
        if (tournament.getParticipants().isEmpty()) {
            throw new IllegalArgumentException("Tournament " + tournamentId + " has no participants");
        }

        recipients = new ArrayList<>(tournament.getParticipants());
    }

}
