package com.linkedin.reach.mastermind.controllers;

import com.linkedin.reach.mastermind.models.Game;
import com.linkedin.reach.mastermind.services.ComputeService;
import com.linkedin.reach.mastermind.services.GameManager;
import com.linkedin.reach.mastermind.services.InputValidator;
import com.linkedin.reach.mastermind.services.PublicApiAnswerGenerator;
import com.linkedin.reach.mastermind.services.RenderingService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.mock.web.MockHttpSession;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.web.servlet.View;
import org.springframework.web.servlet.view.AbstractView;
import org.thymeleaf.spring6.view.ThymeleafViewResolver;

import java.util.ArrayList;
import java.util.Locale;
import java.util.Map;

import static org.hamcrest.Matchers.notNullValue;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(GameController.class)
@AutoConfigureMockMvc(addFilters = false)
class GameControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean private GameManager gameManager;
    @MockitoBean private PublicApiAnswerGenerator publicApiAnswerGenerator;
    @MockitoBean private InputValidator inputValidator;
    @MockitoBean private ComputeService computeService;
    @MockitoBean private RenderingService renderingService;
    @MockitoBean private ThymeleafViewResolver thymeleafViewResolver;

    private static final String GAME_ID = "g-123";

    @BeforeEach
    void stubViewResolver() throws Exception {
        View v = new AbstractView() {
            @Override
            protected void renderMergedOutputModel(Map<String, Object> model, jakarta.servlet.http.HttpServletRequest request, jakarta.servlet.http.HttpServletResponse response) {}
        };
        when(thymeleafViewResolver.resolveViewName(anyString(), any(Locale.class))).thenReturn(v);
    }

    private Game mockGame(String id, boolean finished) {
        Game g = mock(Game.class);
        when(g.getGameId()).thenReturn(id);
        when(g.isFinished()).thenReturn(finished);
        return g;
    }

    @Test
    void getGame_notFinished_shouldRenderGame() throws Exception {
        Game current = mockGame(GAME_ID, false);
        when(gameManager.findByGameId(GAME_ID)).thenReturn(current);

        mockMvc.perform(get("/{gameId}/game", GAME_ID))
                .andExpect(status().isOk())
                .andExpect(view().name("game"));

        verify(renderingService).renderGame(any(), eq(gameManager), eq(current));
    }

    @Test
    void getGame_finished_shouldRedirectResult() throws Exception {
        Game finished = mockGame(GAME_ID, true);
        when(gameManager.findByGameId(GAME_ID)).thenReturn(finished);

        mockMvc.perform(get("/{gameId}/game", GAME_ID))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/" + GAME_ID + "/result"));
    }

    @Test
    void getGame_notFound_shouldRedirectHome() throws Exception {
        when(gameManager.findByGameId(GAME_ID)).thenReturn(null);

        mockMvc.perform(get("/{gameId}/game", GAME_ID))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/"));
    }

    @Test
    void postGuess_invalidInput_shouldRedirectBackWithFlash() throws Exception {
        Game current = mockGame(GAME_ID, false);
        when(gameManager.findByGameId(GAME_ID)).thenReturn(current);
        when(inputValidator.validate("abcd")).thenReturn(false);

        mockMvc.perform(post("/{gameId}/guess", GAME_ID).param("input", "abcd"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/" + GAME_ID + "/game"));

        verify(renderingService).renderRedirectAttributesForError(any(), eq("Each digit must be 0 ~ 7 !!!"));
        verifyNoInteractions(computeService);
    }

    @Test
    void postGuess_validInput_shouldComputeSaveAndRedirectBack() throws Exception {
        Game current = mockGame(GAME_ID, false);
        when(gameManager.findByGameId(GAME_ID)).thenReturn(current);
        when(inputValidator.validate("0123")).thenReturn(true);
        when(current.getAnswer()).thenReturn("0123");
        when(current.getGuessHistory()).thenReturn(new ArrayList<>());
        when(computeService.countCorrectNumbers("0123", "0123")).thenReturn(4);
        when(computeService.countCorrectLocations("0123", "0123")).thenReturn(4);

        mockMvc.perform(post("/{gameId}/guess", GAME_ID).param("input", "0123"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/" + GAME_ID + "/game"));

        verify(computeService).countCorrectNumbers("0123", "0123");
        verify(computeService).countCorrectLocations("0123", "0123");
        verify(gameManager).checkGameOverStatus(current);
        verify(renderingService).renderRedirectAttributesForInstantFeedback(any(), eq(current));
        verify(gameManager).save(current);
    }

    @Test
    void postGuess_finishedGame_shouldRedirectHome() throws Exception {
        Game current = mockGame(GAME_ID, true);
        when(gameManager.findByGameId(GAME_ID)).thenReturn(current);

        mockMvc.perform(post("/{gameId}/guess", GAME_ID).param("input", "0123"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/"));
    }

    @Test
    void initiateGame_shouldCreate_setSession_andRedirect() throws Exception {
        when(publicApiAnswerGenerator.generate()).thenReturn("0123");
        MockHttpSession session = new MockHttpSession();

        mockMvc.perform(post("/initiateGame").param("level", "easy").session(session))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrlPattern("/*/game"))
                .andExpect(request().sessionAttribute("gameId", notNullValue()));

        ArgumentCaptor<Game> captor = ArgumentCaptor.forClass(Game.class);
        verify(gameManager).save(captor.capture());
    }

    @Test
    void result_existingGame_shouldRenderResultView() throws Exception {
        Game current = mockGame(GAME_ID, false);
        when(gameManager.findByGameId(GAME_ID)).thenReturn(current);

        mockMvc.perform(get("/{gameId}/result", GAME_ID))
                .andExpect(status().isOk())
                .andExpect(view().name("result"));

        verify(renderingService).renderGame(any(), eq(gameManager), eq(current));
    }

    @Test
    void result_missingGame_shouldRedirectHome() throws Exception {
        when(gameManager.findByGameId(GAME_ID)).thenReturn(null);

        mockMvc.perform(get("/{gameId}/result", GAME_ID))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/"));
    }

    @Test
    void restart_shouldDeleteGame_clearSession_andRedirectHome() throws Exception {
        MockHttpSession session = new MockHttpSession();
        session.setAttribute("gameId", GAME_ID);

        mockMvc.perform(post("/{gameId}/restart", GAME_ID).session(session))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/"))
                .andExpect(request().sessionAttributeDoesNotExist("gameId"));

        verify(gameManager).deleteByGameId(GAME_ID);
    }
}
