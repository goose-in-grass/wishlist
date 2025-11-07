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
TODO переписать бизнес-логику, дропнуть БД и настроить все заново для большего контроля.
 Добавить миграции. Проверить работоспособность.

 TODO Узнать надо ли разделять контроллеры и бизнес-логику

 TODO переписать миграцию: при удаление БД(public) база не создается заново


 */

