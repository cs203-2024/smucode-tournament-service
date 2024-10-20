package com.cs203.smucode.repositories;

import com.cs203.smucode.models.Bracket;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface BracketServiceRepository extends JpaRepository<Bracket, UUID> {

    Optional<List<Bracket>> findByRoundId(UUID roundId);

    Optional<Bracket> findByRoundIdAndSeqId(UUID roundId, int seqId);
}
