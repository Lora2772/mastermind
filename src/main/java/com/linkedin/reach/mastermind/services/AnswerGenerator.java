package com.linkedin.reach.mastermind.services;

import org.springframework.stereotype.Service;

import java.util.Random;

@Service
public class AnswerGenerator {

    public String generate(){
        Random random = new Random();
        StringBuilder sb = new StringBuilder();
        for(int i = 0; i < 4; i++){
            sb.append(String.valueOf(random.nextInt(8)));
        }
        return new String(sb);
    }

}
