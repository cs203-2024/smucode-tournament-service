package com.cs203.smucode.repositories;

import com.cs203.smucode.models.Tournament;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface TournamentServiceRepository extends MongoRepository<Tournament, String> {
}