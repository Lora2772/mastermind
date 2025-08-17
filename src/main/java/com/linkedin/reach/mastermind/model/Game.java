package com.linkedin.reach.mastermind.model;

import java.time.Duration;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;

public class Game {
    private String answer;
    private final int maxAttempts = 10;
//    private List<Guess> guessHistory;
    private List<HumanPlayer> players;
    private int activePlayerIndex;
    private HumanPlayer winner;
    private boolean won;
    private boolean finished;
    private long startTime;
    private long endTime;

    public Game(String answer){
        this.answer = answer;
//        this.guessHistory = new ArrayList<>();
        this.players = new ArrayList<>();
    }

    public void setWinner(HumanPlayer winner) {
        this.winner = winner;
    }

    public HumanPlayer getWinner() {
        return winner;
    }

    public List<HumanPlayer> getPlayers() {
        return players;
    }

    public void addPlayer(HumanPlayer player) {
        players.add(player);
    }

    public void removeLastPlayer() {
        if (players.size() > 1) {
            players.remove(players.size() - 1);
        }
    }



    public HumanPlayer getCurrentPlayer() {
        return players.get(activePlayerIndex);
    }

    public HumanPlayer getNextPlayer() {
        activePlayerIndex = (++activePlayerIndex) % players.size();
        return players.get(activePlayerIndex);
    }

    public HumanPlayer peekNextPlayer() {
        return players.get((activePlayerIndex + 1) % players.size());
    }

    public String getAnswer(){
        return answer;
    }
    public int getMaxAttempts() {
        return maxAttempts;
    }
//    public List<Guess> getGuessHistory() {
//        return guessHistory;
//    }
//    public int getAttempts() { return guessHistory.size(); }
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
