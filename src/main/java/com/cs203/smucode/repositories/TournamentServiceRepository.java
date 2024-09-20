package com.cs203.smucode.repositories;

import com.cs203.smucode.models.Tournament;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface TournamentServiceRepository extends MongoRepository<Tournament, String> {
    List<Tournament> findBySignUpDeadlineBeforeAndStatus(LocalDateTime dateTime, String status);

}