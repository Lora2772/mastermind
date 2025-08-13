package com.linkedin.reach.mastermind.controllers;

import com.linkedin.reach.mastermind.model.Game;
import com.linkedin.reach.mastermind.model.Guess;
import com.linkedin.reach.mastermind.services.GameStore;
import com.linkedin.reach.mastermind.services.AnswerGenerator;
import com.linkedin.reach.mastermind.services.InputValidator;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.server.ResponseStatusException;

@Controller
public class GameController {
    private GameStore store;
    private AnswerGenerator answerGenerator;
    private InputValidator inputValidator;

    public GameController(GameStore store, AnswerGenerator answerGenerator, InputValidator inputValidator){
        this.store = store;
        this.answerGenerator = answerGenerator;
        this.inputValidator = inputValidator;
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
        return "game";
    }

    @PostMapping("/guess")
    public String guess(Model model, @RequestParam String input) throws Exception {
        Game currentGame = store.getCurrent();
        if (!inputValidator.validate(input)) {
            model.addAttribute("showError", true);
            model.addAttribute("answer", currentGame.getAnswer());
            model.addAttribute("guessNumber", currentGame.getGuessHistory().size());
            model.addAttribute("maxAttempt", currentGame.getMaxAttempts());
            return "game";
        }

        if(currentGame == null){
            return "redirect:/";
        }
        currentGame.getGuessHistory().add(new Guess(input));
        return "redirect:/game";
    }
}
