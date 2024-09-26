# Stage 1: Build the application
FROM maven:latest AS build
WORKDIR /app
COPY . .
RUN mvn clean package -DskipTests

# Stage 2: Create the runtime image
FROM openjdk:latest

# Expose port 8080
EXPOSE 8080

# Copy the built JAR from the build stage
COPY --from=build /app/target/chat-service.jar chat-service.jar

# Set the entry point to run the Spring application
ENTRYPOINT ["java", "-jar", "/chat-service.jar"]
