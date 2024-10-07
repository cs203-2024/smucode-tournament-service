package com.cs203.smucode.repositories;

import com.cs203.smucode.models.Round;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface RoundServiceRepository extends JpaRepository<Round, UUID> {

    List<Round> findByTournamentId(UUID tournamentId);
}
