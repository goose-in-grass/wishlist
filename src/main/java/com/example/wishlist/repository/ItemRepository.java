package com.example.wishlist.repository;

import com.example.wishlist.models.Item;
import org.springframework.data.jpa.repository.JpaRepository;


public interface ItemRepository extends JpaRepository<Item, Long> {
    boolean existsByTitle(String title);
}
