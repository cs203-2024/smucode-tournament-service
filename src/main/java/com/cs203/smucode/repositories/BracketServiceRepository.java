package com.cs203.smucode.repositories;

import com.cs203.smucode.models.Bracket;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface BracketServiceRepository extends MongoRepository<Bracket, String> {

    List<Bracket> findByRoundId(String roundId);
}
