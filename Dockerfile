# Stage 1: Build the application
FROM maven:latest AS build
WORKDIR /app
COPY . .
RUN mvn clean package -DskipTests

# Stage 2: Create the runtime image
FROM openjdk:latest
EXPOSE 8080
COPY --from=build /app/target/rag-chatbot-spring-server.jar rag-chatbot-spring-server.jar
ENTRYPOINT ["java", "-jar", "/rag-chatbot-spring-server.jar"]
