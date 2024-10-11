package com.cs203.smucode.repositories;

import com.cs203.smucode.constants.Status;
import com.cs203.smucode.models.Tournament;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;
import java.util.UUID;


@Repository
public interface TournamentServiceRepository extends JpaRepository<Tournament, UUID> {

    List<Tournament> findByOrganiser(String organiser);

    List<Tournament> findByStatus(Status status);

    Set<Tournament> findByParticipants(String participant);

    List<Tournament> findBySignupEndDateBeforeAndStatus(LocalDateTime dateTime, String status);

}