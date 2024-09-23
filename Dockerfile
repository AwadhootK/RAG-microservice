# Stage 1: Build the application
FROM maven:latest AS build
WORKDIR /app
COPY . .
RUN mvn clean package -DskipTests

# Stage 2: Create the runtime image
FROM openjdk:latest

RUN apt-get update && apt-get install -y iputils-ping curl --no-install-recommends && rm -rf /var/lib/apt/lists/*

EXPOSE 8080
COPY --from=build /app/target/spring-api-gateway.jar spring-api-gateway.jar
ENTRYPOINT ["java", "-jar", "/spring-api-gateway.jar"]
