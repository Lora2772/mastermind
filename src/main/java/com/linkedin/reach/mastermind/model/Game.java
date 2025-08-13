package com.linkedin.reach.mastermind.model;

import java.util.ArrayList;
import java.util.List;

public class Game {
    private String answer;
    private final int maxAttempts = 10;
    private List<Guess> guessHistory;

    public Game(String answer){
        this.answer = answer;
        this.guessHistory = new ArrayList<>();
    }

    public String getAnswer(){
        return answer;
    }
    public int getMaxAttempts() {
        return maxAttempts;
    }

    public List<Guess> getGuessHistory() {
        return guessHistory;
    }
}
