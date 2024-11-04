package com.cs203.smucode.repositories;

import com.cs203.smucode.models.Bracket;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface BracketServiceRepository extends JpaRepository<Bracket, UUID> {

    Optional<List<Bracket>> findByRoundId(UUID roundId);

    Optional<Bracket> findByRoundIdAndSeqId(UUID roundId, int seqId);

    @Query("SELECT b FROM brackets b WHERE b.round.id = :roundId AND (b.player1 = :player OR b.player2 = :player)")
    Optional<Bracket> findByRoundIdAndPlayer1OrPlayer2(@Param("roundId") UUID roundId, @Param("player") String player);

}
