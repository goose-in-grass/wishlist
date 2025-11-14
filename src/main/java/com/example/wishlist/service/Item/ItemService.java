package com.example.wishlist.service.Item;

import com.example.wishlist.dto.ItemDTO;
import com.example.wishlist.models.Item;
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

    // Кэшируем только список всех объектов
    @Cacheable(value = "items")
    public List<ItemDTO> findAllSorted(String sortBy, String direction) {
        System.out.println(">>> Берем из БД, не из Redis");

        Sort sort = buildSort(sortBy, direction);

        return itemRepository.findAll(sort)
                .stream()
                .map(this::toDTO)
                .toList();
    }

    private Sort buildSort(String sortBy, String direction) {
        String sortField;

        switch (sortBy) {
            case "alpha":
                sortField = "title";
                break;
            case "date":
            default:
                sortField = "createdAt";
        }

        Sort.Direction dir =
                "desc".equalsIgnoreCase(direction) ? Sort.Direction.DESC : Sort.Direction.ASC;

        return Sort.by(dir, sortField);
    }



    // При создании — очищаем кэш и отправляем событие
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

        // Отправляем событие в RabbitMQ
        eventProducer.sendEvent("CREATE", saved.getId(), saved.getTitle());

        return toDTO(saved);
    }

    // При обновлении — очищаем кэш и отправляем событие
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

        // Отправляем событие в RabbitMQ
        eventProducer.sendEvent("UPDATE", saved.getId(), saved.getTitle());

        return toDTO(saved);
    }

    // При удалении — очищаем кэш и отправляем событие
    @CacheEvict(value = "items", allEntries = true)
    public void delete(Long id) {
        if (!itemRepository.existsById(id)) {
            throw new IllegalArgumentException("Item with id " + id + " not found");
        }
        itemRepository.deleteById(id);

        // Отправляем событие в RabbitMQ
        eventProducer.sendEvent("DELETE", id, "");
    }

    private ItemDTO toDTO(Item item) {
        return new ItemDTO(item.getId(), item.getTitle(), item.getDescription(), item.getCreatedAt());
    }
}
