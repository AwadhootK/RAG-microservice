# Stage 1: Build the application
FROM maven:latest AS build
WORKDIR /app
COPY . .
RUN mvn clean package -DskipTests

# Stage 2: Create the runtime image
FROM openjdk:latest

RUN apk add --no-cache iputils curl

EXPOSE 8080
COPY --from=build /app/target/spring-api-gateway.jar spring-api-gateway.jar
ENTRYPOINT ["java", "-jar", "/spring-api-gateway.jar"]
