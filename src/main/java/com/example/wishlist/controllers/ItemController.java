package com.example.wishlist.controllers;

import com.example.wishlist.dto.ItemDTO;
import com.example.wishlist.models.User;
import com.example.wishlist.service.Item.ItemService;
import com.example.wishlist.service.User.SecurityUtils;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;


@RestController
@RequestMapping("/api/items")
public class ItemController {

    private final ItemService itemService;
    private final SecurityUtils securityUtils;

    public ItemController(ItemService itemService, SecurityUtils securityUtils) {
        this.itemService = itemService;
        this.securityUtils = securityUtils;
    }

    @GetMapping
    public ResponseEntity<List<ItemDTO>> getAll(Authentication auth,
                                                @RequestParam(defaultValue = "date") String sortBy,
                                                @RequestParam(defaultValue = "asc") String direction) {
        User user = securityUtils.getCurrentUser(auth);
        return ResponseEntity.ok(itemService.findAllSorted(user, sortBy, direction));
    }

    @PostMapping
    public ResponseEntity<ItemDTO> create(Authentication auth, @RequestBody ItemDTO itemDTO) {
        User user = securityUtils.getCurrentUser(auth);
        ItemDTO created = itemService.create(user, itemDTO);
        return ResponseEntity.status(HttpStatus.CREATED).body(created);
    }

    @PutMapping("/{id}")
    public ResponseEntity<ItemDTO> update(Authentication auth,
                                          @PathVariable Long id,
                                          @RequestBody ItemDTO itemDTO) {
        User user = securityUtils.getCurrentUser(auth);
        ItemDTO updated = itemService.update(user, id, itemDTO);
        return ResponseEntity.ok(updated);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(Authentication auth, @PathVariable Long id) {
        User user = securityUtils.getCurrentUser(auth);
        itemService.delete(user, id);
        return ResponseEntity.noContent().build();
    }
}

