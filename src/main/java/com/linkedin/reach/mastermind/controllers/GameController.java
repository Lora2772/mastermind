package com.linkedin.reach.mastermind.controllers;

import com.linkedin.reach.mastermind.model.Game;
import com.linkedin.reach.mastermind.services.GameStore;
import com.linkedin.reach.mastermind.services.AnswerGenerator;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;

@Controller
public class GameController {
    private GameStore store;
    private AnswerGenerator answerGenerator;

    public GameController(GameStore store, AnswerGenerator answerGenerator){
        this.store = store;
        this.answerGenerator = answerGenerator;
    }

    @GetMapping("/")
    public String home(Model model) {
        model.addAttribute("message");
        return "index";
    }

    @PostMapping("/game")
    public String game(Model model){
        Game newGame = new Game(answerGenerator.generate());
        store.setCurrent(newGame);
        model.addAttribute("answer", answerGenerator.generate());
        return "game";
    }

}
