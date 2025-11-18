package com.example.wishlist.repository;

import com.example.wishlist.models.Item;
import com.example.wishlist.models.User;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;


public interface ItemRepository extends JpaRepository<Item, Long> {
    boolean existsByTitle(String title);
    List<Item> findByUser(User user, Sort sort);
    Optional<Item> findByIdAndUser(Long id, User user);
    boolean existsByTitleAndUser(String title, User user);
}
