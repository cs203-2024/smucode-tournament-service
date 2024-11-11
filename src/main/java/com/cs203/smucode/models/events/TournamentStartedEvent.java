package com.cs203.smucode.models.events;

import com.cs203.smucode.constants.NotificationCategory;
import com.cs203.smucode.constants.NotificationType;
import com.cs203.smucode.models.Tournament;

import java.util.ArrayList;
import java.util.List;

/**
 * Event triggered when a tournament starts.
 * This event sends notifications to both the tournament organiser and the participants.
 */
public class TournamentStartedEvent extends Event {
    private final Tournament tournament;
    public TournamentStartedEvent(Tournament tournament,
                                  String message,
                                  NotificationType type,
                                  NotificationCategory category) {
        super(tournament.getId(), tournament.getName(), message, type, category);
        this.tournament = tournament;
    }

    /**
     * Sets the recipients for this event to be the tournament organiser and participants.
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
        eventRecipients.add(tournament.getOrganiser());
        eventRecipients.addAll(tournament.getParticipants());
        recipients = eventRecipients;
    }

}
