package com.lostedin.authenticator.view.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/app")
public class ViewController {

    @GetMapping("/auth")
    public String authPage(Model model) {
        // You can pass dynamic attributes if needed later
        model.addAttribute("title", "Login");
        return "login-page"; // Resolves to src/main/resources/templates/login-page.html
    }

    // Redirected form /auth if 2FA enabled in user settings
    @GetMapping("/auth/2fa")
    protected String auth2faPage(Model model){
        model.addAttribute("title", "Two‑Factor Authentication");
        model.addAttribute("qrSize", 256);
        return "totp"; // Resolves to src/main/resources/templates/totp.html
    }
    
    @GetMapping("/register")
    protected String registerPage(Model model){
        model.addAttribute("title", "Register");
        return "register-page";
    }

    @GetMapping("/settings")
    protected String settingsPage(Model model){
        model.addAttribute("title", "User Settings");
        return "settings"; // Resolves to src/main/resources/templates/settings.html
    }
}
