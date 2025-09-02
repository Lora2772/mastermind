package com.linkedin.reach.mastermind.repository;

import com.linkedin.reach.mastermind.models.Game;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface GameRepository extends MongoRepository<Game, String> {
    Game findByGameId(String gameId);
    Game deleteByGameId(String gameId);
}
