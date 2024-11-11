package com.cs203.smucode.models.events;

import com.cs203.smucode.constants.NotificationCategory;
import com.cs203.smucode.constants.NotificationType;
import com.cs203.smucode.models.Tournament;

import java.util.Set;

/**
 * Event triggered when a player's registration is rejected in a tournament.
 * This event sends a notification to users who have signed up but are not chosen participants in the tournament.
 */
public class RegistrationRejectedEvent extends Event {
    private final Tournament tournament;
    public RegistrationRejectedEvent(
            Tournament tournament,
            String message,
            NotificationType type,
            NotificationCategory category) {
        super(tournament.getId(), tournament.getName(), message, type, category);
        this.tournament = tournament;
    }

    /**
     * Sets the recipients for this event to be the users who signed up for the tournament but are not chosen participants.
     *
     * @throws IllegalStateException if tournament signups or participants are empty
     */
    @Override
    public void setRecipients() {
        Set<String> signups = tournament.getSignups();
        Set<String> participants = tournament.getParticipants();

        if (signups.isEmpty() || participants.isEmpty()) {
            throw new IllegalStateException("Tournament " + tournament.getId() + " has no signups or participants.");
        }

        recipients = signups.stream()
                .filter(user -> !participants.contains(user))
                .toList();
    }

}
