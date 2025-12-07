package com.example.wishlist.controllers;

import com.example.wishlist.UncheckedException.UserAlreadyExistsException;
import com.example.wishlist.service.User.UserRegistrationRequest;
import com.example.wishlist.service.User.UserResponseDto;
import com.example.wishlist.service.User.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api")
@CrossOrigin(origins = "*")
@Tag(name = "Authentication API", description = "User registration and authentication operations")
public class AuthController {

    private final UserService userService;

    public AuthController(UserService userService) {
        this.userService = userService;
    }

    @PostMapping("/register")
    @Operation(summary = "Register a new user", description = "Creates a new user account with provided credentials")
    @ApiResponse(responseCode = "200", description = "User successfully registered",
            content = @Content(schema = @Schema(implementation = UserResponseDto.class)))
    @ApiResponse(responseCode = "400", description = "User already exists",
            content = @Content(schema = @Schema(example = "{\"error\": \"User already exists\"}")))

    public ResponseEntity<?> register(@RequestBody UserRegistrationRequest request) {
        try {
            UserResponseDto response = userService.register(request);
            return ResponseEntity.ok(response);
        } catch (UserAlreadyExistsException e) {
            return ResponseEntity.badRequest()
                    .body(Map.of("error", e.getMessage()));
        }
    }
}
