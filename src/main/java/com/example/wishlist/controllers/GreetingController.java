package com.example.wishlist.controllers;

import com.example.wishlist.models.User;
import com.example.wishlist.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class GreetingController {

    private final UserRepository userRepository;

    @Autowired
    public GreetingController(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @GetMapping("/")
    public String greeting(Model model) {
        Iterable<User> users = userRepository.findAll();
        model.addAttribute("title", "Главная страница");
        model.addAttribute("users", users);
        return "home"; // обращается к файлу home.html в templates
    }
}
