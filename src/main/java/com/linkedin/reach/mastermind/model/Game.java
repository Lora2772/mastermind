package com.linkedin.reach.mastermind.model;

public class Game {
    private String answer;
    private final int maxAttempts = 10;

    public Game(String answer){
        this.answer = answer;
    }
}
