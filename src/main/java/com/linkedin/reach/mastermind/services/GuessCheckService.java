package com.linkedin.reach.mastermind.services;

import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;

@Service
public class GuessCheckService {
    public int countCorrectNumbers(String answer, String input){
        Map<Character, Integer> answerFreqMap = new HashMap<>();
        for(int i = 0; i < answer.length(); i++){
            answerFreqMap.put(answer.charAt(i), answerFreqMap.getOrDefault(answer.charAt(i), 0) + 1);
        }

        Map<Character, Integer> inputFreqMap = new HashMap<>();
        for(int i = 0; i < input.length(); i++){
            inputFreqMap.put(input.charAt(i), inputFreqMap.getOrDefault(input.charAt(i), 0) + 1);
        }
        int correctNumber = 0;
        for(Map.Entry<Character, Integer> entry : answerFreqMap.entrySet()){
            Character num = entry.getKey();
            if(inputFreqMap.containsKey(num)){
                correctNumber += Math.min(entry.getValue(), inputFreqMap.get(num));
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
