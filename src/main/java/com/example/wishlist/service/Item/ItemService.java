package com.example.wishlist.service.Item;

import com.example.wishlist.dto.ItemDTO;
import com.example.wishlist.models.Item;
import com.example.wishlist.models.User;
import com.example.wishlist.repository.ItemRepository;
import com.example.wishlist.service.rabbit.WishlistEventProducer;
import org.springframework.boot.autoconfigure.data.web.SpringDataWebProperties;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class ItemService {

    private final ItemRepository itemRepository;
    private final WishlistEventProducer eventProducer;

    public ItemService(ItemRepository itemRepository, WishlistEventProducer eventProducer) {
        this.itemRepository = itemRepository;
        this.eventProducer = eventProducer;
    }

    @Cacheable(value = "items")
    public List<ItemDTO> findAllSorted(User user, String sortBy, String direction) {
        Sort sort = buildSort(sortBy, direction);

        return itemRepository.findByUser(user, sort)
                .stream()
                .map(this::toDTO)
                .toList();
    }

    @CacheEvict(value = "items", allEntries = true)
    public ItemDTO create(User user, ItemDTO dto) {
        if (itemRepository.existsByTitleAndUser(dto.getTitle(), user)) {
            throw new IllegalArgumentException("Item with title '" + dto.getTitle() + "' already exists for this user");
        }

        Item item = new Item();
        item.setTitle(dto.getTitle());
        item.setDescription(dto.getDescription());
        item.setCreatedAt(LocalDateTime.now());
        item.setUser(user);  // ⚡ привязка к пользователю

        Item saved = itemRepository.save(item);
        eventProducer.sendEvent("CREATE", saved.getId(), saved.getTitle(), user.getId());

        return toDTO(saved);
    }

    @CacheEvict(value = "items", allEntries = true)
    public ItemDTO update(User user, Long id, ItemDTO itemDTO) {
        Item existing = itemRepository.findByIdAndUser(id, user)
                .orElseThrow(() -> new IllegalArgumentException("Item not found or does not belong to user"));

        if (!existing.getTitle().equals(itemDTO.getTitle()) &&
                itemRepository.existsByTitleAndUser(itemDTO.getTitle(), user)) {
            throw new IllegalArgumentException("Item with title '" + itemDTO.getTitle() + "' already exists for this user");
        }

        existing.setTitle(itemDTO.getTitle());
        existing.setDescription(itemDTO.getDescription());

        Item saved = itemRepository.save(existing);
        eventProducer.sendEvent("UPDATE", saved.getId(), saved.getTitle(), user.getId());

        return toDTO(saved);
    }

    @CacheEvict(value = "items", allEntries = true)
    public void delete(User user, Long id) {
        Item existing = itemRepository.findByIdAndUser(id, user)
                .orElseThrow(() -> new IllegalArgumentException("Item not found or does not belong to user"));

        itemRepository.delete(existing);
        eventProducer.sendEvent("DELETE", id, "", user.getId());
    }

    private Sort buildSort(String sortBy, String direction) {
        String sortField = "date".equalsIgnoreCase(sortBy) ? "createdAt" : "title";
        Sort.Direction dir = "desc".equalsIgnoreCase(direction) ? Sort.Direction.DESC : Sort.Direction.ASC;
        return Sort.by(dir, sortField);
    }

    private ItemDTO toDTO(Item item) {
        return new ItemDTO(item.getId(), item.getTitle(), item.getDescription(), item.getCreatedAt());
    }
}

