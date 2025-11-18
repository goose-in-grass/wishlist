package com.example.wishlist.controllers;

import com.example.wishlist.UncheckedException.UserAlreadyExistsException;
import com.example.wishlist.service.User.UserRegistrationRequest;
import com.example.wishlist.service.User.UserResponseDto;
import com.example.wishlist.service.User.UserService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.ResponseEntity;

import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class AuthControllerTest {

    @Mock
    private UserService userService;

    @InjectMocks
    private AuthController authController;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void register_Success() throws UserAlreadyExistsException {
        // Arrange
        UserRegistrationRequest request = new UserRegistrationRequest();
        request.setEmail("test@example.com");
        request.setPassword("password123");
        request.setUsername("Test User");

        UserResponseDto expectedResponse = new UserResponseDto();
        expectedResponse.setId(1L);
        expectedResponse.setEmail("test@example.com");
        expectedResponse.setUsername("Test User");

        when(userService.register(request)).thenReturn(expectedResponse);

        // Act
        ResponseEntity<?> response = authController.register(request);

        // Assert
        assertEquals(200, response.getStatusCodeValue());
        assertEquals(expectedResponse, response.getBody());
        verify(userService, times(1)).register(request);
    }

    @Test
    void register_UserAlreadyExists_ReturnsBadRequest() throws UserAlreadyExistsException {
        // Arrange
        UserRegistrationRequest request = new UserRegistrationRequest();
        request.setEmail("existing@example.com");
        request.setPassword("password123");
        request.setUsername("Existing User");

        String errorMessage = "User already exists";
        when(userService.register(request)).thenThrow(new UserAlreadyExistsException(errorMessage));

        // Act
        ResponseEntity<?> response = authController.register(request);

        // Assert
        assertEquals(400, response.getStatusCodeValue());
        assertTrue(response.getBody() instanceof Map);
        Map<String, String> responseBody = (Map<String, String>) response.getBody();
        assertEquals(errorMessage, responseBody.get("error"));
        verify(userService, times(1)).register(request);
    }
}
