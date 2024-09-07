package com.project.ragchatbot;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class RagchatbotApplication {
    public static void main(String[] args) {
        System.out.println("POSTGRES HOST = " + System.getenv("POSTGRES_HOST"));
        System.out.println("POSTGRES PORT = " + System.getenv("POSTGRES_PORT"));

        SpringApplication.run(RagchatbotApplication.class, args);
    }

}
