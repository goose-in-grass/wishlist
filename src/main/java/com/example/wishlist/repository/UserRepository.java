package com.example.wishlist.repository;

import com.example.wishlist.models.User;
import org.springframework.data.repository.CrudRepository;

import java.util.Optional;

//Указали в <> с чем работаем и тип данных для PK
public interface UserRepository extends CrudRepository<User, Long> {
    boolean existsByUsername(String username);
    boolean existsByEmail(String email);
    Optional<User> findByUsername(String username);

}
