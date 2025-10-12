package com.example.wishlist.controllers;


import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class mainController {

    @GetMapping("/")
    public String home(Model model) {
        model.addAttribute("title", "Главная страница");
        return "home"; // home.html в папке templates
    }

    @GetMapping("/register")
    public String registerPage(Model model) {
        model.addAttribute("title", "Регистрация");
        return "register"; // register.html в папке templates
    }
}
