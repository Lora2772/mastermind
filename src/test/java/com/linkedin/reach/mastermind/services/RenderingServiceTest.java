package com.linkedin.reach.mastermind.services;

import com.linkedin.reach.mastermind.models.Game;
import com.linkedin.reach.mastermind.models.Guess;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.ui.ConcurrentModel;
import org.springframework.ui.Model;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import org.springframework.web.servlet.mvc.support.RedirectAttributesModelMap;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class RenderingServiceTest {

    private RenderingService renderingService;

    @Mock
    private GameManager mockGameManager;

    @Mock
    private Game mockGame;

    @Mock
    private Guess mockPrevGuess;

    @Mock
    private Guess mockRecentGuess;

    @BeforeEach
    void setUp() {
        renderingService = new RenderingService();
    }

    @Test
    void test_renderGame_addsAttributes() {
        Model model = new ConcurrentModel();
        when(mockGameManager.getCurrent()).thenReturn(mockGame);

        renderingService.renderGame(model, mockGameManager);

        assertSame(mockGameManager, model.getAttribute("gameManager"));
        assertSame(mockGame, model.getAttribute("currentGame"));
    }

    @Test
    void test_renderRedirectAttributesForInstantFeedback_success() {
        RedirectAttributes ra = new RedirectAttributesModelMap();
        when(mockRecentGuess.getCorrectNumbers()).thenReturn(3);
        when(mockRecentGuess.getCorrectLocations()).thenReturn(1);
        when(mockGame.getGuessHistory()).thenReturn(List.of(mockPrevGuess, mockRecentGuess));

        renderingService.renderRedirectAttributesForInstantFeedback(ra, mockGame);

        assertEquals("You have guessed 3 correct numbers and 1 correct locations.",
                ra.getFlashAttributes().get("popupMessage"));
        assertEquals("darkgreen", ra.getFlashAttributes().get("colorStyle"));
    }

    @Test
    void test_renderRedirectAttributesForInstantFeedback_allIncorrect() {
        RedirectAttributes ra = new RedirectAttributesModelMap();
        when(mockRecentGuess.getCorrectNumbers()).thenReturn(0);
        when(mockGame.getGuessHistory()).thenReturn(List.of(mockPrevGuess, mockRecentGuess));

        renderingService.renderRedirectAttributesForInstantFeedback(ra, mockGame);

        assertEquals("All incorrect. Please try again.", ra.getFlashAttributes().get("popupMessage"));
        assertEquals("darkblue", ra.getFlashAttributes().get("colorStyle"));
    }

    @Test
    void test_renderRedirectAttributesForError_setsErrorFlash() {
        RedirectAttributes ra = new RedirectAttributesModelMap();

        renderingService.renderRedirectAttributesForError(ra, "Something went wrong");

        assertEquals("Something went wrong", ra.getFlashAttributes().get("popupMessage"));
        assertEquals("red", ra.getFlashAttributes().get("colorStyle"));
    }
}
