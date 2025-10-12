package com.example.wishlist.controllers;

import com.example.wishlist.UncheckedException.UserAlreadyExistsException;
import com.example.wishlist.service.UserRegistrationRequest;
import com.example.wishlist.service.UserResponseDto;
import com.example.wishlist.service.UserService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController // <-- важно!
@RequestMapping("/api")
@CrossOrigin(origins = "*")
public class AuthController {

    private final UserService userService;

    public AuthController(UserService userService) {
        this.userService = userService;
    }

    @PostMapping("/register")
    public ResponseEntity<UserResponseDto> register(@RequestBody UserRegistrationRequest request) {
        try {
            UserResponseDto response = userService.register(request);
            return ResponseEntity.ok(response);
        } catch (UserAlreadyExistsException e) {
            return ResponseEntity
                    .badRequest()
                    .body(new UserResponseDto(null, e.getMessage(), null, null));
        }
    }


}
