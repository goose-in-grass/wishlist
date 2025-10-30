FROM eclipse-temurin:21-jdk
WORKDIR /app
COPY target/wishlist-0.0.1-SNAPSHOT.jar wishlist.jar
ENTRYPOINT ["java", "-jar", "wishlist.jar"]
