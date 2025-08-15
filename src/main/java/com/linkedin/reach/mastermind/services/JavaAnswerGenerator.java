package com.linkedin.reach.mastermind.services;

import org.springframework.stereotype.Service;

import java.util.Random;

@Service
public class JavaAnswerGenerator {
    public String generate(){
        Random random = new Random();
        StringBuilder sb = new StringBuilder(4);
        for(int i = 0; i < 4; i++){
            sb.append(random.nextInt(8));
        }
        return new String(sb);
    }
}
