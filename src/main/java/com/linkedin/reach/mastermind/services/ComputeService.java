package com.linkedin.reach.mastermind.services;

import org.springframework.stereotype.Service;


@Service
public class ComputeService {
    public int countCorrectNumbers(String answer, String input){
        int correctNumber = 0;
        int[] answerFreq = new int[8];
        for(int i = 0; i < answer.length(); i++){
            answerFreq[answer.charAt(i) - '0']++;
        }
        int[] inputFreq = new int[8];
        for(int i = 0; i < input.length(); i++){
            inputFreq[input.charAt(i) - '0']++;
        }
        for(int i = 0; i <= 7; i++){
            if(answerFreq[i] > 0 && inputFreq[i] > 0){
                correctNumber += Math.min(answerFreq[i], inputFreq[i]);
            }
        }
        return correctNumber;
    }

    public int countCorrectLocations(String answer, String input){
        int correctLocation = 0;
        for(int i = 0; i < answer.length(); i++){
            if(answer.charAt(i) == input.charAt(i)){
                correctLocation++;
            }
        }
        return correctLocation;
    }
}
