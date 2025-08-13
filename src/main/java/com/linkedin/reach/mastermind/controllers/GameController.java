package com.linkedin.reach.mastermind.controllers;

import com.linkedin.reach.mastermind.model.Game;
import com.linkedin.reach.mastermind.model.Guess;
import com.linkedin.reach.mastermind.services.GameStore;
import com.linkedin.reach.mastermind.services.AnswerGenerator;
import com.linkedin.reach.mastermind.services.GuessCheckService;
import com.linkedin.reach.mastermind.services.InputValidator;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
public class GameController {
    private final GameStore store;
    private final AnswerGenerator answerGenerator;
    private final InputValidator inputValidator;
    private final GuessCheckService guessCheckService;

    public GameController(GameStore store, AnswerGenerator answerGenerator, InputValidator inputValidator, GuessCheckService guessCheckService){
        this.store = store;
        this.answerGenerator = answerGenerator;
        this.inputValidator = inputValidator;
        this.guessCheckService = guessCheckService;
    }

    @GetMapping("/")
    public String home(Model model) {
        return "index";
    }

    @PostMapping("/initiateGame")
    public String initiateGame(){
        Game newGame = new Game(answerGenerator.generate());
        store.setCurrent(newGame);
        return "redirect:/game";
    }

    @GetMapping("/game")
    public String game(Model model){
        Game currentGame = store.getCurrent();
        model.addAttribute("answer", currentGame.getAnswer());
        model.addAttribute("guessNumber", currentGame.getGuessHistory().size());
        model.addAttribute("maxAttempt", currentGame.getMaxAttempts());
        model.addAttribute("currentGame", currentGame);
        return "game";
    }

    @PostMapping("/guess")
    public String guess(RedirectAttributes ra, @RequestParam String input)  {
        Game currentGame = store.getCurrent();
        if (!inputValidator.validate(input)) {
            ra.addFlashAttribute("showError", true);
            return "redirect:/game";
        }

        if(currentGame == null){
            return "redirect:/";
        }
        currentGame.getGuessHistory().add(new Guess(input, guessCheckService.countCorrectNumbers(input, currentGame.getAnswer()),
                guessCheckService.countCorrectLocations(input, currentGame.getAnswer())));

        return "redirect:/game";
    }
}
