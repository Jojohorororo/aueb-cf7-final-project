package com.videoclub.config;

import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.web.SecurityFilterChain;

@TestConfiguration
@EnableWebSecurity
public class TestSecurityConfig {

    @Bean
    public SecurityFilterChain testFilterChain(HttpSecurity http) throws Exception {
        http.csrf(csrf -> csrf.disable())
            .authorizeHttpRequests(auth -> auth
                .requestMatchers("/api/auth/**").permitAll()
                .requestMatchers(HttpMethod.POST, "/api/movies/**").hasRole("USER")
                .requestMatchers(HttpMethod.PUT, "/api/movies/**").hasRole("ADMIN")
                .requestMatchers(HttpMethod.DELETE, "/api/movies/**").hasRole("ADMIN")
                .requestMatchers(HttpMethod.GET, "/api/movies/**").hasRole("USER")
                .anyRequest().authenticated()
            );
        return http.build();
    }
}

