package com.example.wishlist;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.FilterType;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.data.redis.repository.configuration.EnableRedisRepositories;


@SpringBootApplication
@ComponentScan(
        basePackages = "com.example.wishlist",
        excludeFilters = @ComponentScan.Filter(
                type = FilterType.ASSIGNABLE_TYPE,
                classes = com.example.wishlist.config.RedisConfig.class
        )
)
public class WishlistApplication {
    public static void main(String[] args) {
        SpringApplication.run(WishlistApplication.class, args);
    }
}

/*
TODO:
    2.Redis:
        Пользовательские сессии. Как проверить?
    3.RabbitMQ:
        ✅ Уточнить грамотно ли реализован функционал
    4.Тесты:
        ○	✅Юнит-тесты для сервисов (валидация, бизнес-логика)
        ○	Интеграционные тесты для CRUD API 👉(дописать)
        ○	✅Code coverage ≥ 60%
    5.CI/CD:
        ○	GitHub Actions workflow:
        ○	запуск тестов при push
        ○	проверка code coverage
    6.Документация:
        ○	OpenAPI/Swagger для API
        ○	README.md с инструкцией:



 */

