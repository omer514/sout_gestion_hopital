package com.gestionhopital.gestionhopital.web;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class SecurityController {

    // On définit explicitement la route /login demandée par SecurityConfig
    @GetMapping("/login")
    public String login() {
        return "login"; // Affiche templates/login.html
    }

    // Redirection automatique de la racine vers le dashboard
    @GetMapping("/")
    public String index() {
        return "redirect:/dashboard";
    }

    @GetMapping("/dashboard")
    public String dashboard() {
        return "home"; // Affiche templates/home.html
    }

    @GetMapping("/notAuthorized")
    public String notAuthorized() {
        return "notAuthorized";
    }
}