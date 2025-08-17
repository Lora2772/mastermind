package com.linkedin.reach.mastermind.controllers;

import com.linkedin.reach.mastermind.model.Game;
import com.linkedin.reach.mastermind.model.Guess;
import com.linkedin.reach.mastermind.model.HumanPlayer;
import com.linkedin.reach.mastermind.services.GameStore;
import com.linkedin.reach.mastermind.services.PublicApiAnswerGenerator;
import com.linkedin.reach.mastermind.services.CheckService;
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
    private final PublicApiAnswerGenerator publicApiAnswerGenerator;
    private final InputValidator inputValidator;
    private final CheckService checkService;

    public GameController(GameStore store, PublicApiAnswerGenerator publicApiAnswerGenerator, InputValidator inputValidator, CheckService checkService){
        this.store = store;
        this.publicApiAnswerGenerator = publicApiAnswerGenerator;
        this.inputValidator = inputValidator;
        this.checkService = checkService;
    }

    @GetMapping("/")
    public String home(Model model) {
        model.addAttribute("hasGame", store.hasGame());
        return "index";
    }

    @PostMapping("/initiateGame")
    public String initiateGame(){
        Game newGame = new Game(publicApiAnswerGenerator.generate());
        store.setCurrent(newGame);
        store.getCurrent().start();
        return "redirect:/game";
    }

    @PostMapping("/initiateMultiPlayerGame")
    public String initiateMultiPlayerGame(RedirectAttributes ra, @RequestParam String playerCount){
        Game newGame = new Game(publicApiAnswerGenerator.generate());

        int count = Integer.parseInt(playerCount);

        for (int i = 0; i < count; i++) {
            newGame.addPlayer(new HumanPlayer());
        }

        store.setCurrent(newGame);
        return "redirect:/multiPlayerGame";
    }

    @GetMapping("/multiPlayerGame")
    public String multiPlayerGame(Model model, RedirectAttributes ra){
        Game currentGame = store.getCurrent();
        if (currentGame == null) return "redirect:/";
        if (currentGame.isFinished()) return "redirect:/result";
        model.addAttribute("currentGame", currentGame);
        return "multiPlayerGame";
    }

    @PostMapping("/multiPlayerGuess")
    public String multiPlayerGuess(RedirectAttributes ra, @RequestParam String input)  {

        Game currentGame = store.getCurrent();

        HumanPlayer currentPlayer = currentGame.getCurrentPlayer();
        int correctNumbers = checkService.countCorrectNumbers(input, currentGame.getAnswer());
        int correctLocations = checkService.countCorrectLocations(input, currentGame.getAnswer());
        if (!inputValidator.validate(input)) {
            ra.addFlashAttribute("showError", true);
            return "redirect:/multiPlayerGuess";
        }

        ra.addFlashAttribute("correctNumbers", correctNumbers);
        ra.addFlashAttribute("correctLocations", correctLocations);
        if(correctNumbers > 0){
            ra.addFlashAttribute("showBoth", true);
        } else {
            ra.addFlashAttribute("showMessage", true);
        }
//        currentGame.getGuessHistory().add(new Guess(input, correctNumbers, correctLocations));
        currentPlayer.takeGuess(new Guess(input, correctNumbers, correctLocations));

        if (correctLocations >= 4){
            currentGame.setWon(true);
            currentGame.setFinished(true);
            currentGame.end();
            currentPlayer.setWon(true);
            currentGame.setWinner(currentPlayer);
            return "redirect:/result";
        } else if (currentPlayer.getGuessCount() >= currentGame.getMaxAttempts()) {
            currentPlayer.setLost(true);
            if (currentGame.peekNextPlayer().getLost()) {
                currentGame.setFinished(true);
                currentGame.end();
            }
            return "redirect:/result";
        }

        currentGame.getNextPlayer();

        return "redirect:/multiPlayerGame";
    }

    @GetMapping("/game")
    public String game(Model model, RedirectAttributes ra){
        Game currentGame = store.getCurrent();
        if (currentGame == null) return "redirect:/";
        if (currentGame.isFinished()) return "redirect:/result";
        model.addAttribute("currentGame", currentGame);
        return "game";
    }

    @PostMapping("/guess")
    public String guess(RedirectAttributes ra, @RequestParam String input)  {
        Game currentGame = store.getCurrent();
        int correctNumbers = checkService.countCorrectNumbers(input, currentGame.getAnswer());
        int correctLocations = checkService.countCorrectLocations(input, currentGame.getAnswer());
        if (!inputValidator.validate(input)) {
            ra.addFlashAttribute("showError", true);
            return "redirect:/game";
        }

        ra.addFlashAttribute("correctNumbers", correctNumbers);
        ra.addFlashAttribute("correctLocations", correctLocations);
        if(correctNumbers > 0){
            ra.addFlashAttribute("showBoth", true);
        } else {
            ra.addFlashAttribute("showMessage", true);
        }
//        currentGame.getGuessHistory().add(new Guess(input, correctNumbers, correctLocations));

        if (correctLocations >= 4){
            currentGame.setWon(true);
            currentGame.setFinished(true);
            currentGame.end();
            return "redirect:/result";
//        } else if (currentGame.getAttempts() >= currentGame.getMaxAttempts()) {
//            currentGame.setFinished(true);
//            currentGame.end();
//            return "redirect:/result";
        }

        return "redirect:/game";
    }

    @GetMapping("/result")
    public String result(Model model){
        Game currentGame = store.getCurrent();
        if (currentGame == null) return "redirect:/";

        model.addAttribute("currentGame", currentGame);
        return "result";
    }

    @PostMapping("/restart")
    public String reset() {
        store.clear();
        return "redirect:/";
    }
}
