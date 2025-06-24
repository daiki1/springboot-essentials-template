# Use OpenJDK 21 LTS
FROM eclipse-temurin:21-jdk

# Set working directory
WORKDIR /app

# Copy compiled JAR
COPY target/spring-project-0.0.1-SNAPSHOT.jar app.jar

# Expose Spring Boot default port
EXPOSE 8080

# Run Spring Boot app
ENTRYPOINT ["java", "-jar", "app.jar", "--spring.profiles.active=docker"]