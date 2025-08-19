package com.linkedin.reach.mastermind.services;

import com.linkedin.reach.mastermind.models.Game;
import com.linkedin.reach.mastermind.models.Guess;
import org.springframework.stereotype.Component;
import org.springframework.ui.Model;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;

@Component
public class RenderingService {

    public void renderGameManager(Model model, GameManager gameManager) {
        model.addAttribute("gameManager", gameManager);
        model.addAttribute("currentGame", gameManager.getCurrent());
    }

    public void renderRedirectAttributesForGame(RedirectAttributes ra, Game currentGame) {
        List<Guess> guesses = currentGame.getGuessHistory();
        Guess recentGuess = guesses.get(guesses.size() - 1);

        String message;

        if(recentGuess.getCorrectNumbers() > 0) {
            message = "You have guessed " + recentGuess.getCorrectNumbers() + " correct numbers and " + recentGuess.getCorrectLocations() + " correct locations.";
            ra.addFlashAttribute("colorStyle", "darkgreen");
        } else {
            message = "All incorrect. Please try again.";
            ra.addFlashAttribute("colorStyle", "darkblue");
        }

        ra.addFlashAttribute("popupMessage", message);
    }

    public void renderRedirectAttributesForError(RedirectAttributes ra, String errorMessage) {
        ra.addFlashAttribute("popupMessage", errorMessage);
        ra.addFlashAttribute("colorStyle", "red");
    }
}
