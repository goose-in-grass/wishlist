package com.example.wishlist.controllers;

import com.example.wishlist.dto.ItemDTO;
import com.example.wishlist.models.User;
import com.example.wishlist.service.Item.ItemService;
import com.example.wishlist.service.User.SecurityUtils;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;

import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class ItemControllerTest {

    @Mock
    private ItemService itemService;

    @Mock
    private SecurityUtils securityUtils;

    @Mock
    private Authentication authentication;

    @InjectMocks
    private ItemController itemController;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void getAll_shouldReturnItemsSorted() {
        User user = new User();
        List<ItemDTO> items = Arrays.asList(new ItemDTO(), new ItemDTO());
        when(securityUtils.getCurrentUser(authentication)).thenReturn(user);
        when(itemService.findAllSorted(user, "date", "asc")).thenReturn(items);

        ResponseEntity<List<ItemDTO>> response = itemController.getAll(authentication, "date", "asc");

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(items, response.getBody());
        verify(securityUtils).getCurrentUser(authentication);
        verify(itemService).findAllSorted(user, "date", "asc");
    }

    @Test
    void create_shouldCreateAndReturnItem() {
        User user = new User();
        ItemDTO itemDTO = new ItemDTO();
        ItemDTO createdItem = new ItemDTO();
        when(securityUtils.getCurrentUser(authentication)).thenReturn(user);
        when(itemService.create(user, itemDTO)).thenReturn(createdItem);

        ResponseEntity<ItemDTO> response = itemController.create(authentication, itemDTO);

        assertEquals(HttpStatus.CREATED, response.getStatusCode());
        assertEquals(createdItem, response.getBody());
        verify(securityUtils).getCurrentUser(authentication);
        verify(itemService).create(user, itemDTO);
    }

    @Test
    void update_shouldUpdateAndReturnItem() {
        User user = new User();
        Long id = 1L;
        ItemDTO itemDTO = new ItemDTO();
        ItemDTO updatedItem = new ItemDTO();
        when(securityUtils.getCurrentUser(authentication)).thenReturn(user);
        when(itemService.update(user, id, itemDTO)).thenReturn(updatedItem);

        ResponseEntity<ItemDTO> response = itemController.update(authentication, id, itemDTO);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(updatedItem, response.getBody());
        verify(securityUtils).getCurrentUser(authentication);
        verify(itemService).update(user, id, itemDTO);
    }

    @Test
    void delete_shouldDeleteItemAndReturnNoContent() {
        User user = new User();
        Long id = 1L;
        when(securityUtils.getCurrentUser(authentication)).thenReturn(user);

        ResponseEntity<Void> response = itemController.delete(authentication, id);

        assertEquals(HttpStatus.NO_CONTENT, response.getStatusCode());
        assertNull(response.getBody());
        verify(securityUtils).getCurrentUser(authentication);
        verify(itemService).delete(user, id);
    }
}
