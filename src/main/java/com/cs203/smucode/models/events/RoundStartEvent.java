package com.cs203.smucode.models.events;

import com.cs203.smucode.constants.NotificationCategory;
import com.cs203.smucode.constants.NotificationType;
import com.cs203.smucode.models.Tournament;
import com.cs203.smucode.services.TournamentService;

import java.util.ArrayList;
import java.util.Set;
import java.util.UUID;

/**
 * Event triggered when a round starts in a tournament.
 * This event sends a notification to all participants of the tournament to inform them about the start of a new round.
 */
public class RoundStartEvent extends Event {
    public RoundStartEvent(UUID tournamentId,
                           String tournamentName,
                           String message,
                           NotificationType type,
                           NotificationCategory category) {
        super(tournamentId, tournamentName, message, type, category);
    }

    /**
     * Sets the recipients for this event to be the participants of the tournament.
     * It fetches the tournament using the provided tournament service and retrieves all participants.
     *
     * @param tournamentService the service used to retrieve tournament details.
     * @throws IllegalStateException if the participants list is empty.
     */
    @Override
    public void setRecipients(TournamentService tournamentService) {
        Tournament tournament = tournamentService.findTournamentById(tournamentId);
        Set<String> participants = tournament.getParticipants();

        if (participants.isEmpty()) {
            throw new IllegalArgumentException("Tournament " + tournamentId + " has no participants");
        }

        recipients = new ArrayList<>(participants);
    }

}
