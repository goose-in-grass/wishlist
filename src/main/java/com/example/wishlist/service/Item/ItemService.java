package com.example.wishlist.service.Item;

import com.example.wishlist.models.Item;
import com.example.wishlist.repository.ItemRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class ItemService {

    private final ItemRepository itemRepository;

    public ItemService(ItemRepository itemRepository) {
        this.itemRepository = itemRepository;
    }

    public List<ItemDTO> findAll() {
        return itemRepository.findAll()
                .stream()
                .map(this::toDTO)
                .toList();
    }

    public  ItemDTO create(ItemDTO dto) {
        // Проверка на уникальность
        if (itemRepository.existsByTitle(dto.getTitle())) {
            throw new IllegalArgumentException("Item with title '" + dto.getTitle() + "' already exists");
        }

        // Создание новой сущности
        Item item = new Item();
        item.setTitle(dto.getTitle());
        item.setDescription(dto.getDescription());
        item.setCreatedAt(LocalDateTime.now());

        Item saved = itemRepository.save(item);
        return toDTO(saved);
    }

    private ItemDTO toDTO(Item item) {
        return new ItemDTO(item.getId(), item.getTitle(), item.getDescription(), item.getCreatedAt());
    }

    public ItemDTO update(Long id, ItemDTO itemDTO) {
        // Ищем существующий объект
        Item existing = itemRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Item with id " + id + " not found"));

        // Можно добавить проверку уникальности title, если нужно
        if (!existing.getTitle().equals(itemDTO.getTitle()) &&
                itemRepository.existsByTitle(itemDTO.getTitle())) {
            throw new IllegalArgumentException("Item with title '" + itemDTO.getTitle() + "' already exists");
        }

        // Обновляем поля
        existing.setTitle(itemDTO.getTitle());
        existing.setDescription(itemDTO.getDescription());
        // createdAt не трогаем, это дата создания

        Item saved = itemRepository.save(existing);
        return toDTO(saved);
    }

    public void delete(Long id) {
        // Проверка на существование, чтобы не было исключения из JPA
        if (!itemRepository.existsById(id)) {
            throw new IllegalArgumentException("Item with id " + id + " not found");
        }

        itemRepository.deleteById(id);
    }

}

