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
        // пример: если пользователь не найден, редиректим на products
        String username = authentication.getName();
        User user = userRepository.findByUsername(username).orElse(null);
        if (user == null) {
            return "redirect:/products";
        }

        model.addAttribute("title", "Главная страница");
        model.addAttribute("currentUser", user);
        return "home"; // home.html
    }

    @GetMapping("/products")
    public String productsPage(Model model) {
        model.addAttribute("title", "Products");
        return "products"; // products.html в templates
    }



}
