# Stage 1: Build the application
FROM maven:latest AS build
WORKDIR /app
COPY . .
RUN mvn clean package -DskipTests

# Stage 2: Create the runtime image
FROM amazoncorretto:latest

EXPOSE 8080
COPY --from=build /app/target/spring-api-gateway.jar spring-api-gateway.jar
ENTRYPOINT ["java", "-jar", "/spring-api-gateway.jar"]