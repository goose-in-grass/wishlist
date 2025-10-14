package com.example.wishlist.controllers;


import com.example.wishlist.models.User;
import com.example.wishlist.repository.UserRepository;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class WelcomController {
    private final UserRepository userRepository;

    public WelcomController(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @GetMapping("/register")
    public String registerPage(Model model) {
        model.addAttribute("title", "Регистрация");
        return "register"; // register.html в папке templates
    }

    @GetMapping("/login")
    public String loginPage(Model model) {
        model.addAttribute("title", "Вход");
        return "login"; // register.html в папке templates
    }


    @GetMapping("/home")
    public String home(Model model, Authentication authentication) {
        String username = authentication.getName();
        User user = userRepository.findByUsername(username).orElse(null);

        model.addAttribute("title", "Главная страница");
        model.addAttribute("currentUser", user);

        return "home";
    }

    //TODO: Добавить окно логина с переходом либо на регистрацию, либо на главную страницу

}
