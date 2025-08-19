package com.linkedin.reach.mastermind.services;

import com.linkedin.reach.mastermind.models.Game;
import com.linkedin.reach.mastermind.models.Guess;
import org.springframework.stereotype.Component;
import org.springframework.ui.Model;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;

@Component
public class RenderingService {

    public void renderGame(Model model, Game currentGame) {
        model.addAttribute("currentGame", currentGame);
    }

    public void renderRedirectAttributes(RedirectAttributes ra, Game currentGame) {
        List<Guess> guesses = currentGame.getGuessHistory();
        Guess recentGuess = guesses.get(guesses.size() - 1);

        ra.addFlashAttribute("correctNumbers", recentGuess.getCorrectNumbers());
        ra.addFlashAttribute("correctLocations", recentGuess.getCorrectLocations());
        if(recentGuess.getCorrectNumbers() > 0){
            ra.addFlashAttribute("showBoth", true);
        } else {
            ra.addFlashAttribute("showMessage", true);
        }
    }
}
