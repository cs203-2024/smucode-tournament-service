package com.cs203.smucode.models.events;

import com.cs203.smucode.constants.NotificationCategory;
import com.cs203.smucode.constants.NotificationType;
import com.cs203.smucode.models.Round;
import com.cs203.smucode.models.Tournament;

import java.util.ArrayList;
import java.util.Set;

/**
 * Event triggered when a round ends in a tournament.
 * This event sends a notification to all participants of the tournament to inform them about the round's completion.
 */
public class RoundEndedEvent extends Event {
    private Round round;
    public RoundEndedEvent(Round round,
                           String message,
                           NotificationType type,
                           NotificationCategory category) {
        super(round.getTournament().getId(), round.getTournament().getName(), message, type, category);
        this.round = round;
    }

    /**
     * Sets the recipients for this event to be the participants of the tournament.
     *
     * @throws IllegalStateException if the participants list is empty.
     */
    @Override
    public void setRecipients() {
        Tournament tournament = round.getTournament();
        Set<String> participants = tournament.getParticipants();

        if (participants.isEmpty()) {
            throw new IllegalArgumentException("Tournament " + tournamentId + " has no participants");
        }

        recipients = new ArrayList<>(participants);
    }

}
