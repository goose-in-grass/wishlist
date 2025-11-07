package com.example.wishlist.service.Item;

import com.example.wishlist.dto.ItemDTO;
import com.example.wishlist.models.Item;
import com.example.wishlist.repository.ItemRepository;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class ItemService {

    private final ItemRepository itemRepository;

    public ItemService(ItemRepository itemRepository) {
        this.itemRepository = itemRepository;
    }

    // Кэшируем только список всех объектов
    @Cacheable(value = "items")
    public List<ItemDTO> findAll() {
        System.out.println(">>> Берем из БД, не из Redis");
        return itemRepository.findAll()
                .stream()
                .map(this::toDTO)
                .toList();
    }

    // При создании — очищаем кэш, чтобы при следующем GET данные обновились
    @CacheEvict(value = "items", allEntries = true)
    public ItemDTO create(ItemDTO dto) {
        if (itemRepository.existsByTitle(dto.getTitle())) {
            throw new IllegalArgumentException("Item with title '" + dto.getTitle() + "' already exists");
        }

        Item item = new Item();
        item.setTitle(dto.getTitle());
        item.setDescription(dto.getDescription());
        item.setCreatedAt(LocalDateTime.now());

        Item saved = itemRepository.save(item);
        return toDTO(saved);
    }

    @CacheEvict(value = "items", allEntries = true)
    public ItemDTO update(Long id, ItemDTO itemDTO) {
        Item existing = itemRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Item with id " + id + " not found"));

        if (!existing.getTitle().equals(itemDTO.getTitle()) &&
                itemRepository.existsByTitle(itemDTO.getTitle())) {
            throw new IllegalArgumentException("Item with title '" + itemDTO.getTitle() + "' already exists");
        }

        existing.setTitle(itemDTO.getTitle());
        existing.setDescription(itemDTO.getDescription());

        Item saved = itemRepository.save(existing);
        return toDTO(saved);
    }

    @CacheEvict(value = "items", allEntries = true)
    public void delete(Long id) {
        if (!itemRepository.existsById(id)) {
            throw new IllegalArgumentException("Item with id " + id + " not found");
        }
        itemRepository.deleteById(id);
    }

    private ItemDTO toDTO(Item item) {
        return new ItemDTO(item.getId(), item.getTitle(), item.getDescription(), item.getCreatedAt());
    }
}
/*
Что теперь происходит:
Первый запрос GET /api/items:
Redis пуст → findAll() идёт в БД → возвращает список и сохраняет его в кэше.
Следующие GET /api/items:
Всё берётся напрямую из Redis, БД даже не трогается.
Любой POST, PUT или DELETE:
Удаляет весь кэш items.
При следующем GET список снова тянется из БД, и новый кэш обновляется свежими данными.
 */