package com.linkedin.reach.mastermind.model;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class HumanPlayer {

    private String playerId;
    private List<Guess> guessList;
    private boolean won;
    private boolean lost;

    public HumanPlayer() {
        this.guessList = new ArrayList<>();
        this.playerId = UUID.randomUUID().toString();
    }

    public boolean getWon() {
        return won;
    }

    public void setWon(boolean won) {
        won = won;
    }

    public boolean getLost() {
        return lost;
    }

    public void setLost(boolean lost) {
        lost = lost;
    }

    public String getPlayerId() {
        return playerId;
    }

    public void takeGuess(Guess guess) {
        guessList.add(guess);
    }

    public List<Guess> getGuessList() {
        return guessList;
    }

    public int getGuessCount() {
        return guessList.size();
    }
}
