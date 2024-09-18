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
        return builder.routes()
                .route("auth-service", r -> r.path("/auth/**")
                        .filters(f -> f.filter(filter)
                                .stripPrefix(1)) // Strip the first path segment (/auth)
                        .uri("http://127.0.0.1:8500/"))
                .route("rag-service", r -> r.path("/rag/**")
                        .filters(f -> f.filter(filter).stripPrefix(1))
                        .uri("http://127.0.0.1:8500/")
                )
                .build();
    }
}
