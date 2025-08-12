package com.linkedin.reach.mastermind.controllers;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class GameController {

    @GetMapping("/")
    public String home(Model model) {
        model.addAttribute("message", "Hello!");
        return "index";
    }
}
