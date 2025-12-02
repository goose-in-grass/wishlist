---

# WishList

Веб-приложение на **Spring Boot**, использующее PostgreSQL, Flyway, Redis (опционально) и RabbitMQ для асинхронной обработки событий.

---

## Технологии

### Backend

* Spring Boot 3+
* Spring Web
* Spring Data JPA
* Spring Security
* Spring AMQP (RabbitMQ)
* Thymeleaf
* Flyway
* Lombok

### Storage & Messaging

* **PostgreSQL** — основная база
* **Redis** — кеш/сессии (если включён)
* **RabbitMQ** — асинхронная обработка событий (регистрация, CRUD действий, нотификации)

### Инфраструктура

* Docker & Docker Compose
* Maven
* GitHub Actions (тесты, coverage ≥ 60%)

---

## Быстрый старт

### 1. Клонируем проект

```bash
git clone https://github.com/<your-user>/wishlist.git
cd wishlist
cp src/main/resources/application.example.yml src/main/resources/application.yml
```

### 2. Поднимаем инфраструктуру

```bash
docker-compose up -d --build
```

Поднимутся сервисы:

| Сервис               | Порт  |
| -------------------- | ----- |
| PostgreSQL           | 5433  |
| RabbitMQ (AMQP)      | 5672  |
| RabbitMQ Admin UI    | 15672 |
| Redis (если включён) | 6379  |
| Приложение           | 8080  |

### 3. Устанавливаем зависимости

```bash
./mvnw clean install
```

### 4. Миграции Flyway

Автоматически применяются при запуске.

Проверить вручную:

```bash
./mvnw flyway:info
```

---

## Работа с RabbitMQ

### Основные очереди (пример)

* `wishlist.events` — события CRUD
* `wishlist.email` — события регистрации
* `wishlist.logging` — запись активности

Все они создаются автоматически Spring Boot через `RabbitAdmin` и конфигурации.

### Проверка работы

Открыть панель администрирования:

```
http://localhost:15672
login: guest  
pass: guest
```

Там видно:

* созданные очереди
* биндинги
* сообщения в очереди
* ошибки потребителей

---

## Тестирование

### Создать тестовую БД

```bash
docker-compose exec postgres psql -U postgres -c "CREATE DATABASE wishlist_test;"
```

### Применить миграции для тестовой БД

```bash
./mvnw flyway:migrate -Dspring.profiles.active=test
```

### Запуск тестов с покрытием

```bash
./mvnw clean test jacoco:report
```

### Просмотр отчёта

```
target/site/jacoco/index.html
```

---

## Сервисы

| Сервис         | URL                                                                  |
| -------------- | -------------------------------------------------------------------- |
| Web UI         | [http://localhost:8080](http://localhost:8080)                       |
| API            | [http://localhost:8080/api/](http://localhost:8080/api/)...          |
| Swagger        | [http://localhost:8080/swagger-ui](http://localhost:8080/swagger-ui) |
| PostgreSQL     | localhost:5433                                                       |
| RabbitMQ       | localhost:5672                                                       |
| RabbitMQ Admin | [http://localhost:15672](http://localhost:15672)                     |

---

## CI/CD

Включён GitHub Actions workflow:

* запуск тестов при push в `main`
* проверка покрытия Jacoco
* публикация отчётов coverage
* проверка стабильности основного кода

Workflow расположен здесь:

```
.github/workflows/ci.yml
```


