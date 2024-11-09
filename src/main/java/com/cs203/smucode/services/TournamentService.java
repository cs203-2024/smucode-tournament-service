package com.cs203.smucode.services;

import com.cs203.smucode.constants.Status;
import com.cs203.smucode.models.Tournament;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

public interface TournamentService {

    Tournament findTournamentById(UUID id);

    List<Tournament> findAllTournamentsByOrganiser(String organiser);

    List<Tournament> findAllTournamentsByStatus(Status status);

    List<Tournament> findAllTournamentsByRegistrant(String registrant);

    List<Tournament> findAllTournamentsByParticipant(String participant);

    List<Tournament> findTournamentsWithSignUpBefore(LocalDateTime dateTime);

    List<Tournament> findTournamentsWithSignUpAfter(LocalDateTime dateTime);

    List<Tournament> findAllEligibleTournamentsForUser(String username);

    Tournament createTournament(Tournament tournament);

    Tournament updateTournament(UUID id, Tournament tournament);

    Tournament addTournamentSignup(UUID id, String signups);

    Tournament endBracket(UUID bracketId);

    Tournament endRound(UUID roundId);

    Tournament deleteTournamentSignup(UUID id, String signup);

    Tournament deleteTournamentParticipant(UUID id, String participant);

    void deleteTournamentById(UUID id);
}