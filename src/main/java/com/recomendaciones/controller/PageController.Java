package com.recomendaciones.controller;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;


@Controller
public class PageController {

    @GetMapping("/login")
    public String showLoginForm(
        @RequestParam(value = "error", required = false) String error,
        @RequestParam(value = "logout", required = false) String logout,
        Model model,
        Authentication authentication) {
        
        if (authentication != null && authentication.isAuthenticated()) {
            return "redirect:/dashboard";
        }
        
        if (error != null) {
            model.addAttribute("error", "Credenciales inválidas");
        }
        
        if (logout != null) {
            model.addAttribute("message", "Sesión cerrada exitosamente");
        }
        
        return "login";
    }

    @GetMapping("/register")
    public String showRegisterForm(Authentication authentication) {
        if (authentication != null && authentication.isAuthenticated()) {
            return "redirect:/dashboard";
        }
        return "register";
    }

    @GetMapping({"/", "/home"})
    public String home() {
        return "home";
    }

    @GetMapping("/dashboard")
    public String dashboard(Model model) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        model.addAttribute("username", auth.getName());
        return "dashboard";
    }
}