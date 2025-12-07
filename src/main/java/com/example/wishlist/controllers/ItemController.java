package com.example.wishlist.controllers;

import com.example.wishlist.dto.ItemDTO;
import com.example.wishlist.models.User;
import com.example.wishlist.service.Item.ItemService;
import com.example.wishlist.service.User.SecurityUtils;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/items")
@Tag(name = "Item API", description = "Operations related to wishlist items")
public class ItemController {

    private final ItemService itemService;
    private final SecurityUtils securityUtils;

    public ItemController(ItemService itemService, SecurityUtils securityUtils) {
        this.itemService = itemService;
        this.securityUtils = securityUtils;
    }

    @GetMapping
    @Operation(summary = "Get all items", description = "Retrieve all items for the current user with sorting options")
    @ApiResponse(responseCode = "200", description = "Successfully retrieved items",
            content = @Content(schema = @Schema(implementation = ItemDTO.class)))
    public ResponseEntity<List<ItemDTO>> getAll(Authentication auth,
                                                @Parameter(description = "Sort by field (date, name, price)")
                                                @RequestParam(defaultValue = "date") String sortBy,
                                                @Parameter(description = "Sort direction (asc or desc)")
                                                @RequestParam(defaultValue = "asc") String direction) {
        User user = securityUtils.getCurrentUser(auth);
        return ResponseEntity.ok(itemService.findAllSorted(user, sortBy, direction));
    }

    @PostMapping
    @Operation(summary = "Create new item", description = "Add a new item to the user's wishlist")
    @ApiResponse(responseCode = "201", description = "Item successfully created",
            content = @Content(schema = @Schema(implementation = ItemDTO.class)))
    public ResponseEntity<ItemDTO> create(Authentication auth,
                                          @RequestBody ItemDTO itemDTO) {
        User user = securityUtils.getCurrentUser(auth);
        ItemDTO created = itemService.create(user, itemDTO);
        return ResponseEntity.status(HttpStatus.CREATED).body(created);
    }

    @PutMapping("/{id}")
    @Operation(summary = "Update item", description = "Update an existing item in the user's wishlist")
    @ApiResponse(responseCode = "200", description = "Item successfully updated",
            content = @Content(schema = @Schema(implementation = ItemDTO.class)))
    @ApiResponse(responseCode = "404", description = "Item not found")
    public ResponseEntity<ItemDTO> update(Authentication auth,
                                          @Parameter(description = "Item ID")
                                          @PathVariable Long id,
                                          @RequestBody ItemDTO itemDTO) {
        User user = securityUtils.getCurrentUser(auth);
        ItemDTO updated = itemService.update(user, id, itemDTO);
        return ResponseEntity.ok(updated);
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Delete item", description = "Remove an item from the user's wishlist")
    @ApiResponse(responseCode = "204", description = "Item successfully deleted")
    @ApiResponse(responseCode = "404", description = "Item not found")
    public ResponseEntity<Void> delete(Authentication auth,
                                       @Parameter(description = "Item ID")
                                       @PathVariable Long id) {
        User user = securityUtils.getCurrentUser(auth);
        itemService.delete(user, id);
        return ResponseEntity.noContent().build();
    }
}
