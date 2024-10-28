package com.cs203.smucode.services;

import com.cs203.smucode.constants.Status;
import com.cs203.smucode.models.Tournament;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

public interface TournamentService {
//    reason for having mapping within controller and not service:
//    services may use one another - would have to map everytime

    Tournament findTournamentById(UUID id);

    List<Tournament> findAllTournamentsByOrganiser(String organiser);

    List<Tournament> findAllTournamentsByStatus(Status status);

    List<Tournament> findAllTournamentsByParticipant(String participant);

    List<Tournament> findTournamentsBySignUpDeadline(LocalDateTime dateTime);

    List<Tournament> findAllEligibleTournamentsForUser(String username);

    Tournament createTournament(Tournament tournament);

    Tournament updateTournament(UUID id, Tournament tournament);

    Tournament addTournamentSignup(UUID id, String signups);

    Tournament endBracket(UUID bracketId);

    Tournament endRound(UUID roundId);

    Tournament deleteTournamentSignup(UUID id, String signups);

    void deleteTournamentById(UUID id);
}