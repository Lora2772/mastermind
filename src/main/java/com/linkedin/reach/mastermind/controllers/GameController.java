package com.linkedin.reach.mastermind.controllers;

import com.linkedin.reach.mastermind.models.Game;
import com.linkedin.reach.mastermind.models.Guess;
import com.linkedin.reach.mastermind.services.*;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
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
    public String home(Model model) {
        renderingService.renderGameManager(model, gameManager);
        return "index";
    }

    @PostMapping("/initiateGame")
    public String initiateGame(){
        try {
            Game newGame = new Game(publicApiAnswerGenerator.generate());
            gameManager.setCurrent(newGame);
            gameManager.getCurrent().start();
            return "redirect:/game";
        } catch (RuntimeException e) {
            return "redirect:/";
        }
    }

    @GetMapping("/game")
    public String game(Model model, RedirectAttributes ra){
        Game currentGame = gameManager.getCurrent();
        if (currentGame == null) return "redirect:/";
        if (currentGame.isFinished()) return "redirect:/result";

        renderingService.renderGameManager(model, gameManager);
        return "game";
    }

    @PostMapping("/guess")
    public String guess(RedirectAttributes ra, @RequestParam String input)  {
        Game currentGame = gameManager.getCurrent();

        if (currentGame != null && !currentGame.isFinished()) {
            if (!inputValidator.validate(input)) {
                ra.addFlashAttribute("showError", true);
                renderingService.renderRedirectAttributesForError(ra, "Each digit must be 0 ~ 7 !!!");
                return "redirect:/game";
            }

            int correctNumbers = computeService.countCorrectNumbers(input, currentGame.getAnswer());
            int correctLocations = computeService.countCorrectLocations(input, currentGame.getAnswer());
            currentGame.getGuessHistory().add(new Guess(input, correctNumbers, correctLocations));

            gameManager.checkGameStatus(currentGame);
            renderingService.renderRedirectAttributesForGame(ra, currentGame);

            return "redirect:/game";
        }
        return "redirect:/";
    }

    @GetMapping("/result")
    public String result(Model model){
        Game currentGame = gameManager.getCurrent();
        if (currentGame == null) return "redirect:/";

        renderingService.renderGameManager(model, gameManager);
        return "result";
    }

    @PostMapping("/restart")
    public String reset() {
        gameManager.clear();
        return "redirect:/";
    }
}
