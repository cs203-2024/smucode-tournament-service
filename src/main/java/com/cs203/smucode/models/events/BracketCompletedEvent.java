package com.cs203.smucode.models.events;

import com.cs203.smucode.constants.NotificationCategory;
import com.cs203.smucode.constants.NotificationType;
import com.cs203.smucode.models.Bracket;

import java.util.ArrayList;
import java.util.List;

/**
 * Event triggered when a bracket is completed.
 * This event sends a notification to all players within the bracket.
 */
public class BracketCompletedEvent extends Event {
    private final Bracket bracket;
    public BracketCompletedEvent(Bracket bracket,
                                 String message,
                                 NotificationType type,
                                 NotificationCategory category) {
        super(bracket.getTournament().getId(), bracket.getTournament().getName(), message, type, category);
        this.bracket = bracket;
    }

    /**
     * Sets the recipients for this event to be the players within the bracket.
     * TODO: Should it have a separate event for each player?
     * "Bracket against player1 has ended"
     * "Bracket against player2 has ended"
     *
     * @throws IllegalStateException if the tournament has no participants.
     */
    @Override
    public void setRecipients() {
        if (bracket.getPlayer1() == null && bracket.getPlayer2() == null) {
            throw new IllegalArgumentException("Bracket " + bracket.getId() + " has no participants");
        }

        List<String> eventRecipients = new ArrayList<>();
        if (bracket.getPlayer1() != null) { eventRecipients.add(bracket.getPlayer1()); }
        if (bracket.getPlayer2() != null) { eventRecipients.add(bracket.getPlayer2()); }
        recipients = eventRecipients;
    }

}
