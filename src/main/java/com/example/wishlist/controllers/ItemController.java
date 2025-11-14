package com.example.wishlist.controllers;

import com.example.wishlist.dto.ItemDTO;
import com.example.wishlist.service.Item.ItemService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;


@RestController
@RequestMapping("/api/items")
public class ItemController {

    private final ItemService itemService;

    public ItemController(ItemService itemService) {
        this.itemService = itemService;
    }

    // 1. GET /api/items — список всех с сортировкой
    @GetMapping
    public ResponseEntity<List<ItemDTO>> getAll(
            @RequestParam(required = false, defaultValue = "date") String sortBy,
            @RequestParam(required = false, defaultValue = "asc") String direction
    ) {
        return ResponseEntity.ok(itemService.findAllSorted(sortBy, direction));
    }


    // 2. POST /api/items — создание
    @PostMapping
    public ResponseEntity<ItemDTO> create(@RequestBody ItemDTO itemDTO) {
        ItemDTO created = itemService.create(itemDTO);
        return ResponseEntity.status(HttpStatus.CREATED).body(created);
    }

    // 3. PUT /api/items/{id} — обновление
    @PutMapping("/{id}")
    public ResponseEntity<ItemDTO> update(@PathVariable Long id, @RequestBody ItemDTO itemDTO) {
        ItemDTO updated = itemService.update(id, itemDTO);
        return ResponseEntity.ok(updated);
    }

    // 4. DELETE /api/items/{id} — удаление
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        itemService.delete(id);
        return ResponseEntity.noContent().build();
    }
}




