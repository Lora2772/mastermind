package com.linkedin.reach.mastermind.services;

import com.linkedin.reach.mastermind.models.Game;
import com.linkedin.reach.mastermind.models.Guess;
import com.linkedin.reach.mastermind.repository.GameRepository;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class GameManager {
    private Game current;
    private GameRepository gameRepository;

    public GameManager(GameRepository gameRepository) {
        this.gameRepository = gameRepository;
    }

    public Game findByGameId(String gameId) {
        return gameRepository.findByGameId(gameId);
    }
    public Game deleteByGameId(String gameId) {
        return gameRepository.deleteByGameId(gameId);
    }
    public Game save(Game game) {
        return gameRepository.save(game);
    }

    public Game getCurrent(){
        return current;
    }
    public void setCurrent(Game current){
        this.current = current;
    }
    public void clear(){
        this.current = null;
    }
    public boolean hasGame() {
        return current != null;
    }

    public void checkGameOverStatus(Game current) {
        List<Guess> guesses = current.getGuessHistory();
        Guess recentGuess = guesses.get(guesses.size() - 1);

        int correctLocations = recentGuess.getCorrectLocations();

        if (correctLocations >= 4){
            current.setWon(true);
            current.setFinished(true);
            current.end();
        } else if (current.getAttempts() >= current.getMaxAttempts()) {
            current.setFinished(true);
            current.end();
        }
    }
}
