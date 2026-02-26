package com.gestionhopital.gestionhopital.web;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class SecurityController {

    @GetMapping("/login")
    public String login() {
        return "login"; 
    }

    @GetMapping("/")
    public String index() {
        return "redirect:/dashboard";
    }


    @GetMapping("/notAuthorized")
    public String notAuthorized() {
        return "notAuthorized";
    }
}