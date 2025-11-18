package com.example.wishlist.service.User;

import lombok.Setter;

import java.time.LocalDateTime;

public class UserResponseDto {
    private Long id;
    @Setter
    private String username;
    private String email;
    private LocalDateTime createdAt;

    public UserResponseDto(Long id, String username, String email, LocalDateTime createdAt) {
        this.id = id;
        this.username = username;
        this.email = email;
        this.createdAt = createdAt;
    }

    public UserResponseDto() {

    }


    // геттеры

    public Long getId() {
        return null;
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

    public void setEmail(String mail) {
    }

    public void setId(Long id) {
        this.id = id;
    }

}

