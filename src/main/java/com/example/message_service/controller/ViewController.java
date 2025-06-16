package com.example.message_service.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class ViewController {
    @GetMapping("api/login")
    public String loginPage() {
        return "login";
    }

    @GetMapping("api/register")
    public String registerPage() {
        return "register";
    }

    @GetMapping("api/profile")
    public String profilePage() {
        return "profile";
    }
}
