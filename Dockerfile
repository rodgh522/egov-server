# Use a full JDK image for the build stage
FROM eclipse-temurin:17-jdk AS builder

WORKDIR /app

# Copy the wrapper and project files
COPY gradlew .
COPY gradle gradle
COPY build.gradle settings.gradle ./
COPY src src

# Fix permissions and build
RUN chmod +x gradlew
RUN ./gradlew build -x test --no-daemon

# --- Final Stage ---
FROM eclipse-temurin:17-jre
WORKDIR /app
EXPOSE 8080
COPY --from=builder /app/build/libs/*.jar app.jar
ENTRYPOINT ["java", "-jar", "app.jar"]