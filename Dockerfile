# Stage 1: Build the application
FROM maven:latest AS build
WORKDIR /app
COPY . .
RUN mvn clean package -DskipTests

# Stage 2: Create the runtime image
FROM amazoncorretto:latest

# Optionally install ping (iputils) and curl if needed in the runtime image
RUN yum update -y && yum install -y iputils curl && yum clean all

RUN yum -y install java-1.8.0

RUN yum remove java-1.7.0-openjdk

# Expose port 8080
EXPOSE 8080

# Copy the built JAR from the build stage
COPY --from=build /app/target/spring-api-gateway.jar spring-api-gateway.jar

# Set the entry point to run the Spring application
ENTRYPOINT ["java", "-jar", "/spring-api-gateway.jar"]
