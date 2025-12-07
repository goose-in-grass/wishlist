---

# Wishlist Project

## Быстрый старт

1. **Клонируем проект**

```bash
git clone https://github.com/<your-user>/wishlist.git
cd wishlist
cp src/main/resources/application.example.yml src/main/resources/application.yml
```

2. **Поднимаем инфраструктуру через Docker Compose**

```bash
docker-compose up -d --build
```

Поднимутся сервисы:

| Сервис                | Порт  |
| --------------------- | ----- |
| PostgreSQL (основная) | 5432  |
| PostgreSQL (тестовая) | 5433  |
| RabbitMQ (AMQP)       | 5672  |
| RabbitMQ Admin UI     | 15672 |
| Redis (если включён)  | 6379  |
| Приложение Web        | 8080  |

3. **Устанавливаем зависимости**

```bash
./mvnw clean install
```

4. **Миграции Flyway**

* Автоматически применяются при запуске.
* Проверить вручную:

```bash
./mvnw flyway:info
```

* Для тестовой базы:

```bash
docker-compose exec postgres_test psql -U test -c "CREATE DATABASE testdb;"
./mvnw flyway:migrate -Dspring.profiles.active=test
```

---

## Работа с RabbitMQ

Основные очереди:

| Очередь          | Назначение          |
| ---------------- | ------------------- |
| wishlist.events  | События CRUD        |
| wishlist.email   | События регистрации |
| wishlist.logging | Запись активности   |

* Все очереди создаются автоматически через Spring Boot и RabbitAdmin.
* Проверить работу можно в **RabbitMQ Admin UI**: [http://localhost:15672](http://localhost:15672)
  login: `guest`
  pass: `guest`

---

## Проверка работы приложения

* Web UI: [http://localhost:8080](http://localhost:8080)
* API: [http://localhost:8080/api/...](http://localhost:8080/api/...)
* Swagger: [http://localhost:8080/swagger-ui/index.html](http://localhost:8080/swagger-ui/index.html)
  ⚠️ Доступ только после логина. Можно использовать пользователя, созданного через `/register`.

**Примеры CURL-запросов к API:**

```bash
# Получить все предметы
curl -u testuser:password http://localhost:8080/api/items

# Создать предмет
curl -u testuser:password -X POST http://localhost:8080/api/items \
  -H "Content-Type: application/json" \
  -d '{"title":"New Item","description":"Description"}'
```

---

## Тестирование

1. **Создать тестовую БД**

```bash
docker-compose exec postgres_test psql -U test -c "CREATE DATABASE testdb;"
```

2. **Применить миграции для тестовой БД**

```bash
./mvnw flyway:migrate -Dspring.profiles.active=test
```

3. **Запуск тестов с покрытием**

```bash
./mvnw clean test jacoco:report
```

4. **Просмотр отчёта покрытия**

```text
target/site/jacoco/index.html
```

---

## Сброс данных (для чистой базы)

Для основной базы:

```bash
docker-compose exec postgres psql -U ${POSTGRES_USER} -d ${POSTGRES_DB} -c "DELETE FROM items;"
docker-compose exec postgres psql -U ${POSTGRES_USER} -d ${POSTGRES_DB} -c "DELETE FROM users;"
```

---

## CI/CD

* GitHub Actions workflow:

  * Запуск тестов при push в main
  * Проверка покрытия Jacoco (≥ 60%)
  * Публикация отчётов coverage
* Расположение: `.github/workflows/ci.yml`

---

### Технологии

**Backend:** Spring Boot 3+, Spring Web, Spring Data JPA, Spring Security, Spring AMQP, Thymeleaf, Flyway, Lombok
**Storage & Messaging:** PostgreSQL, Redis (кэш/сессии), RabbitMQ
**Инфраструктура:** Docker & Docker Compose, Maven, GitHub Actions

