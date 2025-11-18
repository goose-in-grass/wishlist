package com.example.wishlist.controllers;

import com.example.wishlist.models.User;
import com.example.wishlist.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.security.core.Authentication;
import org.springframework.ui.Model;
import org.springframework.web.servlet.ModelAndView;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

class WelcomControllerTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private Model model;

    @Mock
    private Authentication authentication;

    @InjectMocks
    private WelcomController welcomController;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void registerPage_ShouldReturnRegisterView() {
        String viewName = welcomController.registerPage(model);

        assertEquals("register", viewName);
        verify(model).addAttribute("title", "Регистрация");
    }

    @Test
    void loginPage_ShouldReturnLoginView() {
        String viewName = welcomController.loginPage(model);

        assertEquals("login", viewName);
        verify(model).addAttribute("title", "Вход");
    }

    @Test
    void home_WhenUserExists_ShouldReturnHomeView() {
        String username = "testUser";
        User user = new User();
        user.setUsername(username);

        when(authentication.getName()).thenReturn(username);
        when(userRepository.findByUsername(username)).thenReturn(Optional.of(user));

        String viewName = welcomController.home(model, authentication);

        assertEquals("home", viewName);
        verify(model).addAttribute("title", "Главная страница");
        verify(model).addAttribute("currentUser", user);
    }

    @Test
    void home_WhenUserNotFound_ShouldRedirectToProducts() {
        String username = "nonExistentUser";

        when(authentication.getName()).thenReturn(username);
        when(userRepository.findByUsername(username)).thenReturn(Optional.empty());

        String viewName = welcomController.home(model, authentication);

        assertEquals("redirect:/products", viewName);
    }

    @Test
    void productsPage_ShouldReturnProductsView() {
        String viewName = welcomController.productsPage(model);

        assertEquals("products", viewName);
        verify(model).addAttribute("title", "Products");
    }
}
