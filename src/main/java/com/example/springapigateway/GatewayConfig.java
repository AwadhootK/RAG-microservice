package com.example.springapigateway;

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
        final String RAG_URI = System.getenv("RAG_HOST")+":"+System.getenv("RAG_PORT")+"/";
        final String AUTH_URI = System.getenv("AUTH_HOST")+":"+System.getenv("AUTH_PORT")+"/";

        return builder.routes()
                .route("auth-service", r -> r.path("/auth/**")
                        .filters(f -> f.filter(filter)
                                .stripPrefix(1)) // Strip the first path segment (/auth)
                        .uri(AUTH_URI))
                .route("rag-service", r -> r.path("/rag/**")
                        .filters(f -> f.filter(filter).stripPrefix(1))
                        .uri(RAG_URI)
                )
                .build();
    }
}
