package com.example.wishlist.controllers;

import com.example.wishlist.WishlistApplication;
import com.example.wishlist.models.Item;
import com.example.wishlist.models.User;
import com.example.wishlist.repository.ItemRepository;
import com.example.wishlist.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Primary;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.context.WebApplicationContext;

import static org.hamcrest.Matchers.hasSize;
import static org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.springSecurity;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest(
        classes = {
                WishlistApplication.class,
                ItemControllerIntegrationTest.TestConfig.class
        }
)
@ActiveProfiles("test")
@TestPropertySource(properties = {
        "spring.cache.type=none",
        "spring.autoconfigure.exclude=" +
                "org.springframework.boot.autoconfigure.data.redis.RedisAutoConfiguration," +
                "org.springframework.boot.autoconfigure.data.redis.RedisRepositoriesAutoConfiguration"
})
@Transactional
@AutoConfigureMockMvc
class ItemControllerIntegrationTest {

    @TestConfiguration
    static class TestConfig {
        @Bean
        @Primary
        public RabbitTemplate rabbitTemplate() {
            return Mockito.mock(RabbitTemplate.class);
        }
    }

    @Autowired
    private WebApplicationContext context;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private ItemRepository itemRepository;

    private MockMvc mockMvc;
    private User testUser;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders
                .webAppContextSetup(context)
                .apply(springSecurity())
                .build();

        testUser = new User();
        testUser.setUsername("testuser");
        testUser.setEmail("test@example.com");
        testUser.setPassword("password");
        userRepository.save(testUser);
    }

    @Test
    @WithMockUser(username = "testuser")
    void getAll_shouldReturnItemsSorted() throws Exception {
        Item item1 = new Item();
        item1.setTitle("Item 1");
        item1.setDescription("Description 1");
        item1.setUser(testUser);

        Item item2 = new Item();
        item2.setTitle("Item 2");
        item2.setDescription("Description 2");
        item2.setUser(testUser);

        itemRepository.save(item1);
        itemRepository.save(item2);

        mockMvc.perform(get("/api/items")
                        .param("sortBy", "title")
                        .param("sortOrder", "asc")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(2)))
                .andExpect(jsonPath("$[0].title").value("Item 1"))
                .andExpect(jsonPath("$[1].title").value("Item 2"));
    }

    @Test
    @WithMockUser(username = "testuser")
    void create_shouldCreateAndReturnItem() throws Exception {
        String itemJson = """
            {
                "title": "New Item",
                "description": "New Description"
            }
            """;

        mockMvc.perform(post("/api/items")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(itemJson))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.title").value("New Item"))
                .andExpect(jsonPath("$.description").value("New Description"));
    }

    @Test
    @WithMockUser(username = "testuser")
    void update_shouldUpdateAndReturnItem() throws Exception {
        Item item = new Item();
        item.setTitle("Old Item");
        item.setDescription("Old Description");
        item.setUser(testUser);
        Item savedItem = itemRepository.save(item);

        String updatedItemJson = """
            {
                "title": "Updated Item",
                "description": "Updated Description"
            }
            """;

        mockMvc.perform(put("/api/items/{id}", savedItem.getId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(updatedItemJson))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.title").value("Updated Item"))
                .andExpect(jsonPath("$.description").value("Updated Description"));
    }

    @Test
    @WithMockUser(username = "testuser")
    void delete_shouldDeleteItemAndReturnNoContent() throws Exception {
        Item item = new Item();
        item.setTitle("Item to delete");
        item.setDescription("Description");
        item.setUser(testUser);
        Item savedItem = itemRepository.save(item);

        mockMvc.perform(delete("/api/items/{id}", savedItem.getId()))
                .andExpect(status().isNoContent());
    }
}
