package com.example.wishlist.service.Item;

import com.example.wishlist.dto.ItemDTO;
import com.example.wishlist.models.Item;
import com.example.wishlist.models.User;
import com.example.wishlist.repository.ItemRepository;
import com.example.wishlist.repository.UserRepository;
import com.example.wishlist.service.User.UserService;
import com.example.wishlist.service.rabbit.WishlistEventProducer;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.BeforeEach;
import org.mockito.ArgumentCaptor;
import org.springframework.data.domain.Sort;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;
import static org.mockito.Mockito.*;

class ItemServiceTest {

    private ItemRepository itemRepository;
    private WishlistEventProducer eventProducer;
    private ItemService itemService;
    private final UserRepository userRepository = mock(UserRepository.class);
    private final PasswordEncoder passwordEncoder = mock(PasswordEncoder.class);
    private final WishlistEventProducer eventProducer1 = mock(WishlistEventProducer.class);
    private final UserService userService = new UserService(userRepository, passwordEncoder, eventProducer1);

    @BeforeEach
    void setup() {
        itemRepository = mock(ItemRepository.class);
        eventProducer = mock(WishlistEventProducer.class);
        itemService = new ItemService(itemRepository, eventProducer);
    }

    @Test
    void buildSortTest() {
        Sort asc = invokeBuildSort("title", "asc");
        assertThat(asc).isEqualTo(Sort.by(Sort.Direction.ASC, "title"));

        Sort desc = invokeBuildSort("date", "desc");
        assertThat(desc).isEqualTo(Sort.by(Sort.Direction.DESC, "createdAt"));
    }

    private Sort invokeBuildSort(String sortBy, String dir) {
        try {
            var method = ItemService.class.getDeclaredMethod("buildSort", String.class, String.class);
            method.setAccessible(true);
            return (Sort) method.invoke(itemService, sortBy, dir);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @Test
    void createTest() {
        User u = new User();
        u.setId(99L);

        ItemDTO dto = new ItemDTO(null, "Phone", "New phone", null);

        when(itemRepository.existsByTitleAndUser("Phone", u)).thenReturn(false);

        Item saved = new Item();
        saved.setId(1L);
        saved.setTitle("Phone");
        saved.setDescription("New phone");
        saved.setCreatedAt(LocalDateTime.now());
        saved.setUser(u);

        when(itemRepository.save(any(Item.class))).thenReturn(saved);

        ItemDTO result = itemService.create(u, dto);

        assertThat(result.getId()).isEqualTo(1L);
        assertThat(result.getTitle()).isEqualTo("Phone");
        assertThat(result.getDescription()).isEqualTo("New phone");

        verify(eventProducer, times(1))
                .sendEvent(eq("CREATE"), eq(1L), eq("Phone"), eq(99L));
    }

    @Test
    void createTest_shouldThrow_whenTitleExists() {
        User u = new User();
        ItemDTO dto = new ItemDTO(null, "Phone", "", null);

        when(itemRepository.existsByTitleAndUser("Phone", u)).thenReturn(true);

        assertThatThrownBy(() -> itemService.create(u, dto))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("already exists");
    }

    @Test
    void deleteTest() {
        User u = new User();
        u.setId(55L);

        Item item = new Item();
        item.setId(10L);
        item.setUser(u);

        when(itemRepository.findByIdAndUser(10L, u)).thenReturn(Optional.of(item));

        itemService.delete(u, 10L);

        verify(itemRepository, times(1)).delete(item);
        verify(eventProducer, times(1))
                .sendEvent("DELETE", 10L, "", 55L);
    }

    @Test
    void deleteTest_shouldThrow_whenItemNotFound() {
        User u = new User();

        when(itemRepository.findByIdAndUser(10L, u)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> itemService.delete(u, 10L))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("not found");
    }

    @Test
    void updateTest() {
        User u = new User();
        u.setId(7L);

        Item existing = new Item();
        existing.setId(5L);
        existing.setTitle("Old");
        existing.setDescription("Old desc");
        existing.setUser(u);

        when(itemRepository.findByIdAndUser(5L, u)).thenReturn(Optional.of(existing));
        when(itemRepository.existsByTitleAndUser("New", u)).thenReturn(false);

        Item updated = new Item();
        updated.setId(5L);
        updated.setTitle("New");
        updated.setDescription("New desc");
        updated.setUser(u);

        when(itemRepository.save(existing)).thenReturn(updated);

        ItemDTO dto = new ItemDTO(null, "New", "New desc", null);

        ItemDTO result = itemService.update(u, 5L, dto);

        assertThat(result.getTitle()).isEqualTo("New");
        assertThat(result.getDescription()).isEqualTo("New desc");

        verify(eventProducer).sendEvent("UPDATE", 5L, "New", 7L);
    }

    @Test
    void updateTest_shouldThrow_whenTitleAlreadyExists() {
        User u = new User();
        Item existing = new Item();
        existing.setTitle("Old");

        when(itemRepository.findByIdAndUser(5L, u)).thenReturn(Optional.of(existing));
        when(itemRepository.existsByTitleAndUser("New", u)).thenReturn(true);

        ItemDTO dto = new ItemDTO(null, "New", "", null);

        assertThatThrownBy(() -> itemService.update(u, 5L, dto))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("already exists");
    }

    @Test
    void findAllSortedTest() {
        User u = new User();

        Item i1 = new Item();
        i1.setId(1L);
        i1.setTitle("A");
        i1.setDescription("d1");
        i1.setCreatedAt(LocalDateTime.now());

        Item i2 = new Item();
        i2.setId(2L);
        i2.setTitle("B");
        i2.setDescription("d2");
        i2.setCreatedAt(LocalDateTime.now());

        when(itemRepository.findByUser(eq(u), any(Sort.class)))
                .thenReturn(List.of(i1, i2));

        List<ItemDTO> result = itemService.findAllSorted(u, "title", "asc");

        assertThat(result).hasSize(2);
        assertThat(result.get(0).getTitle()).isEqualTo("A");
        assertThat(result.get(1).getTitle()).isEqualTo("B");
    }

    @Test
    void toDTOTest() {
        Item i = new Item();
        i.setId(10L);
        i.setTitle("Phone");
        i.setDescription("Desc");
        i.setCreatedAt(LocalDateTime.now());

        ItemDTO dto = invokeToDTO(i);

        assertThat(dto.getId()).isEqualTo(10L);
        assertThat(dto.getTitle()).isEqualTo("Phone");
        assertThat(dto.getDescription()).isEqualTo("Desc");
    }

    private ItemDTO invokeToDTO(Item i) {
        try {
            var m = ItemService.class.getDeclaredMethod("toDTO", Item.class);
            m.setAccessible(true);
            return (ItemDTO) m.invoke(itemService, i);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @Test
    public void registerTest() {

    }
}
