package com.project.ragchatbot;

import lombok.RequiredArgsConstructor;
import org.springframework.cloud.gateway.route.RouteLocator;
import org.springframework.cloud.gateway.route.builder.RouteLocatorBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@RequiredArgsConstructor
public class GatewayConfig {
    private final AuthenticationFilter filter;

    @Bean
    public RouteLocator routes(RouteLocatorBuilder builder) {
        // going to invoke rabbitmq here...
        String INDEXING_URI = "http://" + System.getenv("INDEXING_HOST") + ":" + System.getenv("INDEXING_PORT");
        String RAG_URI = "http://" + System.getenv("RAG_HOST") + ":" + System.getenv("RAG_PORT");

        return builder.routes()
                .route("indexing-service", r -> r.path("/indexing/**")
                        .filters(f -> f.filter(filter))
                        .uri(INDEXING_URI))

                .route("rag-service", r -> r.path("/rag/**")
                        .filters(f -> f.filter(filter))
                        .uri(RAG_URI))
                .build();
    }
}
