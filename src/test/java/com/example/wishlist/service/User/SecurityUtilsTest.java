package com.example.wishlist.service.User;

import com.example.wishlist.models.User;
import com.example.wishlist.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.security.core.Authentication;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class SecurityUtilsTest {

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private SecurityUtils securityUtils;

    @Mock
    private Authentication authentication;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void getCurrentUser_WhenAuthenticationIsNull_ReturnsNull() {
        User result = securityUtils.getCurrentUser(null);
        assertNull(result);
    }

    @Test
    void getCurrentUser_WhenAuthenticationIsNotAuthenticated_ReturnsNull() {
        when(authentication.isAuthenticated()).thenReturn(false);
        User result = securityUtils.getCurrentUser(authentication);
        assertNull(result);
    }

    @Test
    void getCurrentUser_WhenUserNotFound_ThrowsRuntimeException() {
        when(authentication.isAuthenticated()).thenReturn(true);
        when(authentication.getName()).thenReturn("testuser");
        when(userRepository.findByUsername("testuser")).thenReturn(Optional.empty());

        assertThrows(RuntimeException.class, () -> {
            securityUtils.getCurrentUser(authentication);
        });
    }

    @Test
    void getCurrentUser_WhenUserFound_ReturnsUser() {
        User expectedUser = new User();
        expectedUser.setUsername("testuser");

        when(authentication.isAuthenticated()).thenReturn(true);
        when(authentication.getName()).thenReturn("testuser");
        when(userRepository.findByUsername("testuser")).thenReturn(Optional.of(expectedUser));

        User result = securityUtils.getCurrentUser(authentication);
        assertNotNull(result);
        assertEquals("testuser", result.getUsername());
    }
}
