````markdown
# Wishlist

Простой сервис для управления желаниями (wishlist).

## Быстрый старт

1. Клонируем проект:
```bash
git clone https://github.com/<your-user>/wishlist.git
cd wishlist
cp src/main/resources/application.example.yml src/main/resources/application.yml
````

2. Поднимаем инфраструктуру через Docker Compose:

```bash
docker-compose up -d --build
```

Сервисы и порты:

* PostgreSQL: 5432 (основная база)
* RabbitMQ (AMQP): 5672
* RabbitMQ Admin UI: 15672
* Redis (если включён): 6379
* Приложение: 8080

3. Устанавливаем зависимости и собираем проект:

```bash
./mvnw clean install
```

4. Миграции Flyway применяются автоматически. Проверить вручную:

```bash
./mvnw flyway:info
```

## Тестирование

1. Создать тестовую БД:

```bash
docker-compose exec postgres psql -U postgres -c "CREATE DATABASE wishlist_test;"
```

2. Применить миграции для тестовой БД:

```bash
./mvnw flyway:migrate -Dspring.profiles.active=test
```

3. Запуск тестов с покрытием:

```bash
./mvnw clean test jacoco:report
```

Отчёт покрытия:

```
target/site/jacoco/index.html
```

## API

Swagger UI: [http://localhost:8080/swagger-ui](http://localhost:8080/swagger-ui)

## Admin

RabbitMQ Admin: [http://localhost:15672](http://localhost:15672)
login: `guest`
pass: `guest`

## CI/CD

* GitHub Actions запускает тесты при push в main
* Проверяет coverage и стабильность кода

```

