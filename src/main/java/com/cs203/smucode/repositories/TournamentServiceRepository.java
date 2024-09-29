package com.cs203.smucode.repositories;

import com.cs203.smucode.models.Tournament;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;


@Repository
public interface TournamentServiceRepository extends JpaRepository<Tournament, UUID> {
}