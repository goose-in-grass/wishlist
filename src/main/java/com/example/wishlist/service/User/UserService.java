package com.example.wishlist.service.User;

import com.example.wishlist.UncheckedException.UserAlreadyExistsException;
import com.example.wishlist.models.User;
import com.example.wishlist.repository.UserRepository;
import com.example.wishlist.service.rabbit.WishlistEventProducer;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.EventListener;

@Service
public class UserService{
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final WishlistEventProducer eventProducer;

    public UserService(UserRepository userRepository, PasswordEncoder passwordEncoder, WishlistEventProducer eventProducer) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.eventProducer = eventProducer;
    }

    public UserResponseDto register(UserRegistrationRequest request) {
        if (userRepository.existsByUsername(request.getUsername())) {
            throw new UserAlreadyExistsException("Username is already taken");
        }

        if (userRepository.existsByEmail(request.getEmail())) {
            throw new UserAlreadyExistsException("Email is already taken");
        }

        User user = new User();
        user.setUsername(request.getUsername());
        user.setEmail(request.getEmail());
        user.setPassword(passwordEncoder.encode(request.getPassword()));

        User savedUser = userRepository.save(user);

        // Отправляем событие
        eventProducer.sendEvent("CREATE_USER", savedUser.getId(), savedUser.getUsername(), 1L);

        // Создаём DTO с ID из savedUser
        return new UserResponseDto(
                savedUser.getId(),
                savedUser.getUsername(),
                savedUser.getEmail()
        );
    }


}