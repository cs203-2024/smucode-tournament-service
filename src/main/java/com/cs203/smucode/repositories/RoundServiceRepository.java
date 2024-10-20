package com.cs203.smucode.repositories;

import com.cs203.smucode.models.Round;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface RoundServiceRepository extends JpaRepository<Round, UUID> {

    Optional<List<Round>> findByTournamentId(UUID tournamentId);

    Optional<Round> findByTournamentIdAndSeqId(UUID tournamentId, int seqId);

    Optional<Round> findByTournamentIdAndName(UUID tournamentId, String name);
}
