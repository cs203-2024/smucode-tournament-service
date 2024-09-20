package com.cs203.smucode.repositories;

import com.cs203.smucode.models.Round;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface RoundServiceRepository extends MongoRepository<Round, String> {

    List<Round> findByTournamentId(String tournamentId);
}
