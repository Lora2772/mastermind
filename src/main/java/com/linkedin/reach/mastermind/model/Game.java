package com.linkedin.reach.mastermind.model;

import java.util.ArrayList;
import java.util.List;

public class Game {
    private String answer;
    private final int maxAttempts = 10;
    private List<Guess> guessHistory;
    private int attempts;
    private boolean won;

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
    public int getAttempts() { return guessHistory.size(); }
    public boolean getWon(){ return won; }
    public void setWon(boolean won){ this.won = won; }
}
