package com.example.wishlist;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.data.redis.repository.configuration.EnableRedisRepositories;


@EnableCaching
@SpringBootApplication
@EnableJpaRepositories(basePackages = "com.example.wishlist.repository")
public class WishlistApplication {

	public static void main(String[] args) {
		SpringApplication.run(WishlistApplication.class, args);
	}

}

/*

●
TODO:
    1.Добавить фильтрацию по категориям
    .
    .
    2.Redis:
        Пользовательские сессии
    3.RabbitMQ:
        ○ После регистрации пользователя или создания основной сущности отправлять сообщение в очередь
        ○ Консольный воркер обрабатывает очередь и пишет лог (например, «Пользователь зарегистрирован» или «Сущность создана»)
    4.Тесты:
        ○	Юнит-тесты для сервисов (валидация, бизнес-логика)
        ○	Интеграционные тесты для CRUD API
        ○	Code coverage ≥ 60%
    5.CI/CD:
        ○	GitHub Actions workflow:
        ○	запуск тестов при push
        ○	проверка code coverage
    6.Документация:
        ○	OpenAPI/Swagger для API
        ○	README.md с инструкцией:



 */

