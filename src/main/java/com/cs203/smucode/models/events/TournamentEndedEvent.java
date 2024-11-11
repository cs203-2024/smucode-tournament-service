package com.cs203.smucode.models.events;

import com.cs203.smucode.constants.NotificationCategory;
import com.cs203.smucode.constants.NotificationType;
import com.cs203.smucode.models.Tournament;
import com.cs203.smucode.services.TournamentService;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

/**
 * Event triggered when a tournament ends.
 * This event sends a notification to both the tournament organizer and the participants.
 */

public class TournamentEndedEvent extends Event {
    public TournamentEndedEvent(UUID tournamentId,
                                String tournamentName,
                                String message,
                                NotificationType type,
                                NotificationCategory category) {
        super(tournamentId, tournamentName, message, type, category);
    }


    /**
     * Sets the recipients for this event to be the tournament organizer and participants.
     * It fetches the tournament using the provided tournament service and adds the organizer and participants to the recipients list.
     *
     * @param tournamentService the service used to retrieve tournament details.
     * @throws IllegalStateException if the organiser is null.
     */
    @Override
    public void setRecipients(TournamentService tournamentService) {
        Tournament tournament = tournamentService.findTournamentById(tournamentId);
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
