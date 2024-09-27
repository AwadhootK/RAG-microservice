package com.example.springapigateway;

import java.util.List;
import java.util.function.Predicate;

import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.stereotype.Component;

@Component
public class RouterValidator {

        public static final List<String> openApiEndpoints = List.of(
                        "/auth/signup",
                        "/auth/login",
                        "/auth/ping",
                        "/rag/ping",
                        "/chat/chat/ping",
                        "/chat/saveChats/ping",
                        "/chat/message/ping");

        public Predicate<ServerHttpRequest> isSecured = request -> openApiEndpoints
                        .stream()
                        .noneMatch(uri -> request.getURI().getPath().contains(uri));
}