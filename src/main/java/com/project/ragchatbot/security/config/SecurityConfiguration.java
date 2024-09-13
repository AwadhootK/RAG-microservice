//package com.project.ragchatbot.security.config;
//
//import org.springframework.context.annotation.Bean;
//import org.springframework.context.annotation.Configuration;
//import org.springframework.security.authentication.AuthenticationProvider;
//import org.springframework.security.config.annotation.web.builders.HttpSecurity;
//import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
//import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
//import org.springframework.security.config.http.SessionCreationPolicy;
//import org.springframework.security.web.SecurityFilterChain;
//import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
//
//import lombok.RequiredArgsConstructor;
//
//@Configuration
//@RequiredArgsConstructor
//@EnableWebSecurity
//public class SecurityConfiguration {
//
//        private final JWTAuthenticationFilter jwtAuthFilter;
//        private final AuthenticationProvider authenticationProvider;
//
//        @Bean
//        public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
//                http
//                                .csrf(AbstractHttpConfigurer::disable)
//                                .authorizeHttpRequests(authz -> authz
//                                                .requestMatchers("/api/v1/auth/**").permitAll()
//                                                .anyRequest().authenticated())
//                                .sessionManagement(session -> session
//                                                .sessionCreationPolicy(SessionCreationPolicy.STATELESS))
//                                .authenticationProvider(authenticationProvider)
//                                .addFilterBefore(jwtAuthFilter, UsernamePasswordAuthenticationFilter.class); // ! imp
//
//                return http.build();
//        }
//}
