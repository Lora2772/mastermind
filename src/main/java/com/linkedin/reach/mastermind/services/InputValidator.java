package com.linkedin.reach.mastermind.services;

import org.springframework.stereotype.Service;

@Service
public class InputValidator {
    public boolean validate(String input){
        for(int i = 0; i < input.length(); i++){
            if(input.charAt(i) > '7'){
                return false;
            }
        }
        return true;
    }
}
