package com.linkedin.reach.mastermind.controllers;

import com.linkedin.reach.mastermind.models.Game;
import com.linkedin.reach.mastermind.models.Guess;
import com.linkedin.reach.mastermind.services.*;
import jakarta.servlet.http.HttpSession;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;


@Controller
public class GameController {
    private final GameManager gameManager;
    private final PublicApiAnswerGenerator publicApiAnswerGenerator;
    private final InputValidator inputValidator;
    private final ComputeService computeService;
    private final RenderingService renderingService;

    public GameController(GameManager gameManager, PublicApiAnswerGenerator publicApiAnswerGenerator, InputValidator inputValidator, ComputeService computeService, RenderingService renderingService) {
        this.gameManager = gameManager;
        this.publicApiAnswerGenerator = publicApiAnswerGenerator;
        this.inputValidator = inputValidator;
        this.computeService = computeService;
        this.renderingService = renderingService;
    }

    @GetMapping("/")
    public String home(Model model, HttpSession session) {
        String gameId = (String) session.getAttribute("gameId");
        if(gameId == null) {
            renderingService.renderGame(model, gameManager);
        } else {
            renderingService.renderGame(model, gameManager, gameManager.findByGameId(gameId));
        }

        return "index";
    }

    @GetMapping("/difficultyLevel")
    public String difficultyLevel(Model model) {
        renderingService.renderGame(model, gameManager);
        return "difficultyLevel";
    }

    @PostMapping("/initiateGame")
    public String initiateGame(@RequestParam String level, HttpSession session){
        try {
            Game newGame = new Game(publicApiAnswerGenerator.generate());
            String gameId = newGame.getGameId();
            if(level.equals("easy")){
                newGame.setMaxAttempts(10);
            } else if(level.equals("medium")){
                newGame.setMaxAttempts(5);
            } else if(level.equals("hard")){
                newGame.setMaxAttempts(1);
            }
            newGame.start();
            gameManager.save(newGame);
            session.setAttribute("gameId", gameId);
            return "redirect:/" + gameId + "/game";
        } catch (RuntimeException e) {
            return "redirect:/";
        }
    }

    @GetMapping("/{gameId}/game")
    public String game(@PathVariable String gameId, Model model, RedirectAttributes ra){
        Game currentGame = gameManager.findByGameId(gameId);
        if (currentGame == null) return "redirect:/";
        if (currentGame.isFinished()) return "redirect:/" + gameId + "/result";

        renderingService.renderGame(model, gameManager, currentGame);
        return "game";
    }

    @PostMapping("/{gameId}/guess")
    public String guess(@PathVariable String gameId, RedirectAttributes ra, @RequestParam String input)  {
        Game currentGame = gameManager.findByGameId(gameId);

        if (currentGame != null && !currentGame.isFinished()) {
            if (!inputValidator.validate(input)) {
                renderingService.renderRedirectAttributesForError(ra, "Each digit must be 0 ~ 7 !!!");
                return "redirect:/" + gameId + "/game";
            }

            int correctNumbers = computeService.countCorrectNumbers(currentGame.getAnswer(), input);
            int correctLocations = computeService.countCorrectLocations(currentGame.getAnswer(), input);
            currentGame.getGuessHistory().add(new Guess(input, correctNumbers, correctLocations));

            gameManager.checkGameOverStatus(currentGame);
            renderingService.renderRedirectAttributesForInstantFeedback(ra, currentGame);
            gameManager.save(currentGame);
            return "redirect:/" + gameId + "/game";
        }
        return "redirect:/";
    }

    @GetMapping("/{gameId}/result")
    public String result(@PathVariable String gameId, Model model){
        Game currentGame = gameManager.findByGameId(gameId);
        if (currentGame == null) return "redirect:/";

        renderingService.renderGame(model, gameManager, currentGame);
        return "result";
    }

    @PostMapping("/{gameId}/restart")
    public String reset(@PathVariable String gameId, HttpSession session) {
        gameManager.deleteByGameId(gameId);
        session.removeAttribute("gameId");
        return "redirect:/";
    }
}
