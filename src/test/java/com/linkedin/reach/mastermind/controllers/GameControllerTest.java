package com.linkedin.reach.mastermind.controllers;

import com.linkedin.reach.mastermind.models.Game;
import com.linkedin.reach.mastermind.models.Guess;
import com.linkedin.reach.mastermind.services.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.ui.ConcurrentModel;
import org.springframework.ui.Model;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import org.springframework.web.servlet.mvc.support.RedirectAttributesModelMap;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class GameControllerTest {

    private GameController controller;

    private GameManager gameManager;
    private PublicApiAnswerGenerator publicApiAnswerGenerator;
    private InputValidator inputValidator;
    private ComputeService computeService;
    private RenderingService renderingService;

    @BeforeEach
    void setUp() {
        gameManager = mock(GameManager.class);
        publicApiAnswerGenerator = mock(PublicApiAnswerGenerator.class);
        inputValidator = mock(InputValidator.class);
        computeService = mock(ComputeService.class);
        renderingService = mock(RenderingService.class);

        controller = new GameController(
                gameManager,
                publicApiAnswerGenerator,
                inputValidator,
                computeService,
                renderingService
        );
    }

    @Test
    void test_home_rendersIndex() {
        Model model = new ConcurrentModel();

        String view = controller.home(model);

        assertEquals("index", view);
        verify(renderingService).renderGame(model, gameManager);
    }

    @Test
    void test_initiateGame_success() {
        when(publicApiAnswerGenerator.generate()).thenReturn("1234");

        ArgumentCaptor<Game> captor = ArgumentCaptor.forClass(Game.class);
        doAnswer(inv -> {
            Game created = inv.getArgument(0, Game.class);
            when(gameManager.getCurrent()).thenReturn(created);
            return null;
        }).when(gameManager).setCurrent(captor.capture());

        String result = controller.initiateGame();

        assertEquals("redirect:/game", result);
        verify(publicApiAnswerGenerator).generate();
        verify(gameManager).setCurrent(any(Game.class));
        verify(gameManager, atLeastOnce()).getCurrent(); // to call start()
    }

    @Test
    void test_initiateGame_failure() {
        when(publicApiAnswerGenerator.generate()).thenThrow(new RuntimeException("testException"));

        String result = controller.initiateGame();

        assertEquals("redirect:/", result);
    }

    @Test
    void test_game_noCurrent_redirectHome() {
        when(gameManager.getCurrent()).thenReturn(null);
        String view = controller.game(new ConcurrentModel(), new RedirectAttributesModelMap());
        assertEquals("redirect:/", view);
    }

    @Test
    void test_game_finished_redirectResult() {
        Game g = mock(Game.class);
        when(gameManager.getCurrent()).thenReturn(g);
        when(g.isFinished()).thenReturn(true);

        String view = controller.game(new ConcurrentModel(), new RedirectAttributesModelMap());

        assertEquals("redirect:/result", view);
    }

    @Test
    void test_game_active_rendersGame() {
        Game g = mock(Game.class);
        when(gameManager.getCurrent()).thenReturn(g);
        when(g.isFinished()).thenReturn(false);
        Model model = new ConcurrentModel();

        String view = controller.game(model, new RedirectAttributesModelMap());

        assertEquals("game", view);
        verify(renderingService).renderGame(model, gameManager);
    }

    @Test
    void test_guess_noCurrent_redirectHome() {
        when(gameManager.getCurrent()).thenReturn(null);
        String view = controller.guess(new RedirectAttributesModelMap(), "0123");
        assertEquals("redirect:/", view);
    }

    @Test
    void test_guess_finished_redirectHome() {
        Game g = mock(Game.class);
        when(gameManager.getCurrent()).thenReturn(g);
        when(g.isFinished()).thenReturn(true);

        String view = controller.guess(new RedirectAttributesModelMap(), "0123");

        assertEquals("redirect:/", view);
    }

    @Test
    void test_guess_invalid_setsErrorFlashAndRedirects() {
        Game g = mock(Game.class);
        when(gameManager.getCurrent()).thenReturn(g);
        when(g.isFinished()).thenReturn(false);
        when(inputValidator.validate("9988")).thenReturn(false);

        RedirectAttributes ra = new RedirectAttributesModelMap();
        String view = controller.guess(ra, "9988");

        assertEquals("redirect:/game", view);
        verify(renderingService).renderRedirectAttributesForError(ra, "Each digit must be 0 ~ 7 !!!");
        verifyNoInteractions(computeService);
        verify(gameManager, never()).checkGameOverStatus(any());
    }

    @Test
    void test_guess_valid_flowCompletes() {
        Game game = new Game("0123");
        when(gameManager.getCurrent()).thenReturn(game);
        when(inputValidator.validate("0123")).thenReturn(true);
        when(computeService.countCorrectNumbers("0123", "0123")).thenReturn(4);
        when(computeService.countCorrectLocations("0123", "0123")).thenReturn(4);

        RedirectAttributes ra = new RedirectAttributesModelMap();
        String view = controller.guess(ra, "0123");

        assertEquals("redirect:/game", view);
        assertEquals(1, game.getGuessHistory().size());
        Guess added = game.getGuessHistory().get(0);
        assertEquals("0123", added.getInput());
        assertEquals(4, added.getCorrectNumbers());
        assertEquals(4, added.getCorrectLocations());

        verify(gameManager).checkGameOverStatus(game);
        verify(renderingService).renderRedirectAttributesForInstantFeedback(ra, game);
    }

    @Test
    void test_result_noCurrentGame_redirectHome() {
        when(gameManager.getCurrent()).thenReturn(null);
        String view = controller.result(new ConcurrentModel());
        assertEquals("redirect:/", view);
    }

    @Test
    void test_result_rendersResult() {
        Game g = new Game("0123");
        when(gameManager.getCurrent()).thenReturn(g);
        Model model = new ConcurrentModel();

        String view = controller.result(model);

        assertEquals("result", view);
        verify(renderingService).renderGame(model, gameManager);
    }

    @Test
    void restart_clearsAndRedirects() {
        String view = controller.reset();
        assertEquals("redirect:/", view);
        verify(gameManager).clear();
    }
}
