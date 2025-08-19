package com.linkedin.reach.mastermind.models;

import java.time.Duration;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;

public class Game {
    private String answer;
    private final int maxAttempts = 10;
    private List<Guess> guessHistory;
    private boolean won;
    private boolean finished;
    private long startTime;
    private long endTime;

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
    public void setFinished(boolean finished) { this.finished = finished; }
    public boolean isFinished() { return finished; }
    public void start() {
        this.startTime = System.currentTimeMillis();
    }
    public void end() {
        this.endTime = System.currentTimeMillis();
    }
    public String getTimeSpentInSeconds() {
        if (this.startTime != 0 && this.endTime != 0) {
            Duration d = Duration.of(endTime - startTime, ChronoUnit.MILLIS);
            long secs = d.getSeconds();
            long m = secs / 60, s = secs % 60;
            return (m > 0) ? String.format("%d min %02d sec", m, s)
                    : String.format("%d sec", s);
        }
        return "Invalid";
    }
}
