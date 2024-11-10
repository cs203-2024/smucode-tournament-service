package com.cs203.smucode.models.events;

import com.cs203.smucode.constants.NotificationCategory;
import com.cs203.smucode.constants.NotificationType;
import com.cs203.smucode.models.Tournament;
import com.cs203.smucode.services.TournamentService;

import java.util.ArrayList;
import java.util.UUID;

/**
 * Event triggered when a bracket is completed.
 * This event sends a notification to all participants of the tournament.
 */
public class BracketCompletedEvent extends Event {
    public BracketCompletedEvent(UUID tournamentId,
                                 String tournamentName,
                                 String message,
                                 NotificationType type,
                                 NotificationCategory category) {
        super(tournamentId, tournamentName, message, type, category);
    }

    /**
     * Sets the recipients for this event to be the participants of the tournament.
     * It fetches the tournament using the provided tournament service and adds all
     * participants to the recipient list.
     *
     * @param tournamentService the service used to retrieve tournament details.
     * @throws IllegalStateException if the tournament is not found or has no participants.
     */
    @Override
    public void setRecipients(TournamentService tournamentService) {
        Tournament tournament = tournamentService.findTournamentById(tournamentId);

        if (tournament.getParticipants().isEmpty()) {
            throw new IllegalArgumentException("Tournament " + tournamentId + " has no participants");
        }

        recipients = new ArrayList<>(tournament.getParticipants());
    }

}
