package com.linkedin.reach.mastermind.services;

import com.linkedin.reach.mastermind.models.Game;
import org.springframework.stereotype.Component;

@Component
public class GameManager {
    private Game current;
    public Game getCurrent(){
        return current;
    }
    public void setCurrent(Game current){
        this.current = current;
    }
    public void clear(){
        this.current = null;
    }
    public boolean hasGame() {
        return current != null;
    }
}
