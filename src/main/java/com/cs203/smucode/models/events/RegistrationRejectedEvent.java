package com.cs203.smucode.models.events;

import com.cs203.smucode.constants.NotificationCategory;
import com.cs203.smucode.constants.NotificationType;
import com.cs203.smucode.models.Tournament;
import com.cs203.smucode.services.TournamentService;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.UUID;

/**
 * Event triggered when a player's registration is rejected in a tournament.
 * This event sends a notification to users who have signed up but are not chosen participants in the tournament.
 */
public class RegistrationRejectedEvent extends Event {
    public RegistrationRejectedEvent(UUID tournamentId,
                                     String tournamentName,
                                     String message,
                                     NotificationType type,
                                     NotificationCategory category) {
        super(tournamentId, tournamentName, message, type, category);
    }

    /**
     * Sets the recipients for this event to be the users who signed up for the tournament but are not chosen participants.
     * It fetches the tournament using the provided tournament service and filters the signups to exclude participants.
     *
     * @param tournamentService the service used to retrieve tournament details.
     * @throws IllegalStateException if tournament signups or participants are empty
     */
    @Override
    public void setRecipients(TournamentService tournamentService) {
        Tournament tournament = tournamentService.findTournamentById(tournamentId);
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
