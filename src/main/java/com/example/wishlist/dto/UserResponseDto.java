package com.example.wishlist.dto;

import java.time.LocalDateTime;

public class UserResponseDto {
    private Long id;
    private String username;
    private String email;
    private LocalDateTime createdAt;

    public UserResponseDto(Long id, String username, String email, LocalDateTime createdAt) {
        this.id = id;
        this.username = username;
        this.email = email;
        this.createdAt = createdAt;
    }

    // геттеры

    public Long getId() {
        return id;
    }

    public String getUsername() {
        return username;
    }

    public String getEmail() {
        return email;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }
}

