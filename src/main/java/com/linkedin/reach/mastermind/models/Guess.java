package com.linkedin.reach.mastermind.models;

public class Guess {
    private String input;
    private int correctNumbers;
    private int correctLocations;

    public Guess(String input, int correctNumber, int correctLocations){
        this.input = input;
        this.correctNumbers = correctNumber;
        this.correctLocations = correctLocations;
    }

    public String getInput() { return input; }
    public int getCorrectNumbers() { return correctNumbers; }
    public int getCorrectLocations() { return correctLocations; }
}
