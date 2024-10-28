package com.cs203.smucode.repositories;

import com.cs203.smucode.constants.Status;
import com.cs203.smucode.models.Tournament;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import javax.swing.text.html.Option;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;


@Repository
public interface TournamentServiceRepository extends JpaRepository<Tournament, UUID> {

    Optional<List<Tournament>> findByOrganiser(String organiser);

    Optional<List<Tournament>> findByStatus(Status status);

    @Query("SELECT t FROM Tournament t JOIN t.participants p WHERE p = :participant")
    Optional<List<Tournament>> findByParticipant(@Param("participant") String participant);

    Optional<List<Tournament>> findBySignupEndDateBeforeAndStatus(LocalDateTime dateTime, Status status);

}