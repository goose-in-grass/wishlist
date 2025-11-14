# --- 1. Сборка артефакта ---
FROM maven:3.9.6-eclipse-temurin-21 AS build
WORKDIR /app

# Копируем только pom.xml и качаем зависимости (кэш)
COPY pom.xml .
RUN mvn dependency:go-offline

# Копируем весь код и собираем jar
COPY src ./src
RUN mvn package -DskipTests

# --- 2. Финальный образ ---
FROM eclipse-temurin:21-jdk
WORKDIR /app

COPY --from=build /app/target/*.jar app.jar
EXPOSE 8080

# ENTRYPOINT с возможностью override через docker-compose
ENTRYPOINT ["java", "-jar", "app.jar"]
