FROM openjdk:8
EXPOSE 8080
ADD target/rag-chatbot-spring-server.jar rag-chatbot-spring-server.jar
ENTRYPOINT ["java", "-jar", "/rag-chatbot-spring-server.jar"]