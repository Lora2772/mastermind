package com.linkedin.reach.mastermind.services;

import com.linkedin.reach.mastermind.models.Game;
import com.linkedin.reach.mastermind.models.Guess;
import com.linkedin.reach.mastermind.repository.GameRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class GameManagerTest {

    private GameRepository gameRepository;
    private GameManager gameManager;

    @BeforeEach
    void setUp() {
        gameRepository = mock(GameRepository.class);
        gameManager = new GameManager(gameRepository);
    }

    @Test
    void delegate_find_save_delete() {
        Game g = mock(Game.class);
        when(gameRepository.findByGameId("id")).thenReturn(g);
        assertSame(g, gameManager.findByGameId("id"));
        gameManager.save(g);
        verify(gameRepository).save(g);
        gameManager.deleteByGameId("id");
        verify(gameRepository).deleteByGameId("id");
    }

    @Test
    void current_set_get_clear_hasGame() {
        Game g = mock(Game.class);
        assertFalse(gameManager.hasGame());
        gameManager.setCurrent(g);
        assertTrue(gameManager.hasGame());
        assertSame(g, gameManager.getCurrent());
        gameManager.clear();
        assertFalse(gameManager.hasGame());
        assertNull(gameManager.getCurrent());
    }

    @Test
    void checkGameOver_won_whenCorrectLocationsAtLeast4() {
        Game current = mock(Game.class);
        Guess guess = mock(Guess.class);
        when(guess.getCorrectLocations()).thenReturn(4);
        ArrayList<Guess> history = new ArrayList<>();
        history.add(guess);
        when(current.getGuessHistory()).thenReturn(history);
        gameManager.checkGameOverStatus(current);
        verify(current).setWon(true);
        verify(current).setFinished(true);
        verify(current).end();
    }

    @Test
    void checkGameOver_finished_whenAttemptsReached() {
        Game current = mock(Game.class);
        Guess guess = mock(Guess.class);
        when(guess.getCorrectLocations()).thenReturn(2);
        ArrayList<Guess> history = new ArrayList<>();
        history.add(guess);
        when(current.getGuessHistory()).thenReturn(history);
        when(current.getAttempts()).thenReturn(5);
        when(current.getMaxAttempts()).thenReturn(5);
        gameManager.checkGameOverStatus(current);
        verify(current, never()).setWon(true);
        verify(current).setFinished(true);
        verify(current).end();
    }

    @Test
    void checkGameOver_continue_whenNotWonAndAttemptsRemaining() {
        Game current = mock(Game.class);
        Guess guess = mock(Guess.class);
        when(guess.getCorrectLocations()).thenReturn(1);
        ArrayList<Guess> history = new ArrayList<>();
        history.add(guess);
        when(current.getGuessHistory()).thenReturn(history);
        when(current.getAttempts()).thenReturn(1);
        when(current.getMaxAttempts()).thenReturn(5);
        gameManager.checkGameOverStatus(current);
        verify(current, never()).setWon(true);
        verify(current, never()).setFinished(true);
        verify(current, never()).end();
    }
}
