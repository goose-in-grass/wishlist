# --- 1. Сборка артефакта ---
FROM maven:3.9.6-eclipse-temurin-21 AS build
WORKDIR /app

# Копируем pom.xml (это полезно для кэша зависимостей)
COPY pom.xml .

# Копируем исходники
COPY src ./src

# Сборка
RUN mvn package -DskipTests

# --- 2. Финальный образ ---
FROM eclipse-temurin:21-jdk
WORKDIR /app

# Копируем собранный jar
COPY --from=build /app/target/*.jar app.jar

EXPOSE 8080

ENTRYPOINT ["java", "-jar", "app.jar"]
