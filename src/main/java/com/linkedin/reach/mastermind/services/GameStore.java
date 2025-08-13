package com.linkedin.reach.mastermind.services;

import com.linkedin.reach.mastermind.model.Game;
import org.springframework.stereotype.Component;

@Component
public class GameStore {
    private static Game current;
    public static Game getCurrent(){
        return current;
    }
    public void setCurrent(Game current){
        this.current = current;
    }
}
