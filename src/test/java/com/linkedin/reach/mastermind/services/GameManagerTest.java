package com.linkedin.reach.mastermind.services;

import com.linkedin.reach.mastermind.models.Game;
import com.linkedin.reach.mastermind.models.Guess;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class GameManagerTest {

    private GameManager gameManager;

    @Mock
    private Game mockGame;

    @Mock
    private Guess mockGuessLow;

    @Mock
    private Guess mockGuessWin;

    @BeforeEach
    void setUp() {
        gameManager = new GameManager();
    }

    @Test
    void test_hasGame_falseWhenNoCurrent() {
        assertFalse(gameManager.hasGame());
        assertNull(gameManager.getCurrent());
    }

    @Test
    void test_setAndGetCurrent() {
        gameManager.setCurrent(mockGame);
        assertTrue(gameManager.hasGame());
        assertSame(mockGame, gameManager.getCurrent());
    }

    @Test
    void test_clear_resetsCurrent() {
        gameManager.setCurrent(mockGame);
        gameManager.clear();
        assertFalse(gameManager.hasGame());
        assertNull(gameManager.getCurrent());
    }

    @Test
    void test_checkGameOverStatus_winByCorrectLocations() {
        when(mockGuessWin.getCorrectLocations()).thenReturn(4);
        when(mockGame.getGuessHistory()).thenReturn(List.of(mockGuessLow, mockGuessWin));

        gameManager.checkGameOverStatus(mockGame);

        verify(mockGame, times(1)).setWon(true);
        verify(mockGame, times(1)).setFinished(true);
        verify(mockGame, times(1)).end();
        verify(mockGame, never()).getAttempts();
        verify(mockGame, never()).getMaxAttempts();
    }

    @Test
    void test_checkGameOverStatus_lossByAttempts() {
        when(mockGuessLow.getCorrectLocations()).thenReturn(2);
        when(mockGame.getGuessHistory()).thenReturn(List.of(mockGuessWin, mockGuessLow));
        when(mockGame.getAttempts()).thenReturn(10);
        when(mockGame.getMaxAttempts()).thenReturn(10);

        gameManager.checkGameOverStatus(mockGame);

        verify(mockGame, never()).setWon(true);
        verify(mockGame, times(1)).setFinished(true);
        verify(mockGame, times(1)).end();
    }

    @Test
    void test_checkGameOverStatus_notOver() {
        when(mockGuessLow.getCorrectLocations()).thenReturn(1);
        when(mockGame.getGuessHistory()).thenReturn(List.of(mockGuessWin, mockGuessLow));
        when(mockGame.getAttempts()).thenReturn(3);
        when(mockGame.getMaxAttempts()).thenReturn(10);

        gameManager.checkGameOverStatus(mockGame);

        verify(mockGame, never()).setWon(true);
        verify(mockGame, never()).setFinished(true);
        verify(mockGame, never()).end();
    }
}
