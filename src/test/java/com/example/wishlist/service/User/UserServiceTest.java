package com.example.wishlist.service.User;

import com.example.wishlist.UncheckedException.UserAlreadyExistsException;
import com.example.wishlist.models.User;
import com.example.wishlist.repository.UserRepository;
import com.example.wishlist.service.rabbit.WishlistEventProducer;
import org.junit.jupiter.api.Test;
import org.springframework.security.crypto.password.PasswordEncoder;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

public class UserServiceTest {

    private final UserRepository userRepository = mock(UserRepository.class);
    private final PasswordEncoder passwordEncoder = mock(PasswordEncoder.class);
    private final WishlistEventProducer eventProducer = mock(WishlistEventProducer.class);
    private final UserService userService = new UserService(userRepository, passwordEncoder, eventProducer);

    @Test
    void register_success() {
        UserRegistrationRequest request = new UserRegistrationRequest();
        request.setUsername("John");
        request.setEmail("john@example.com");
        request.setPassword("1234");

        // Моки для проверки уникальности
        when(userRepository.existsByUsername("John")).thenReturn(false);
        when(userRepository.existsByEmail("john@example.com")).thenReturn(false);

        // Мокируем поведение save
        User savedUser = new User();
        savedUser.setId(1L);
        savedUser.setUsername("John");
        savedUser.setEmail("john@example.com");
        when(userRepository.save(any(User.class))).thenReturn(savedUser);

        // Мокируем passwordEncoder
        when(passwordEncoder.encode("1234")).thenReturn("encoded1234");

        UserResponseDto response = userService.register(request);

        assertEquals(1L, response.getId());
        assertEquals("John", response.getUsername());
        assertEquals("john@example.com", response.getEmail());

        // Проверяем, что событие отправилось
        verify(eventProducer).sendEvent("CREATE_USER", 1L, "John", 1L);
    }

    @Test
    void register_usernameTaken() {
        when(userRepository.existsByUsername("John")).thenReturn(true);

        UserRegistrationRequest request = new UserRegistrationRequest();
        request.setUsername("John");
        request.setEmail("john@example.com");
        request.setPassword("1234");

        assertThrows(UserAlreadyExistsException.class, () -> userService.register(request));
    }

    @Test
    void register_emailTaken() {
        when(userRepository.existsByUsername("John")).thenReturn(false);
        when(userRepository.existsByEmail("john@example.com")).thenReturn(true);

        UserRegistrationRequest request = new UserRegistrationRequest();
        request.setUsername("John");
        request.setEmail("john@example.com");
        request.setPassword("1234");

        assertThrows(UserAlreadyExistsException.class, () -> userService.register(request));
    }
}
