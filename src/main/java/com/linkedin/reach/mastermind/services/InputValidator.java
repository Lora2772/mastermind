package com.linkedin.reach.mastermind.services;

import org.springframework.stereotype.Service;

@Service
public class InputValidator {
    public boolean validate(String input){
        if (input == null) return false;
        if (input.length() != 4) return false;

        for (int i = 0; i < input.length(); i++) {
            char ch = input.charAt(i);
            if (ch < '0' || ch > '7') {
                return false;
            }
        }
        return true;
    }
}
