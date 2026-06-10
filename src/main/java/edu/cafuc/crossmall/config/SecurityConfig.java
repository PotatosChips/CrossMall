package edu.cafuc.crossmall.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableWebSecurity
public class SecurityConfig {
    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
                .csrf(csrf -> csrf.disable())
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers("/api/userLogin",
                                        "/api/userRegister",
                                        "/api/userInfo",
                                        "/api/userLogout",
                                        "/api/categories",
                                        "/api/categories/**",
                                        "/api/regions",
                                        "/api/regions/**",
                                        "/api/shops",
                                        "/api/shops/**",
                                        "/api/products",
                                        "/api/products/**",
                                        "/api/cart",
                                        "/api/cart/**",
                                        "/api/order/**",
                                        "/api/seller",
                                        "/api/seller/**",
                                        "/api/reviews",
                                        "/api/reviews/**",
                                        "/api/after-sales",
                                        "/api/after-sales/**",
                                        "/api/admin",
                                        "/api/admin/**",
                                        "/error"
                                        ).permitAll()
                        .anyRequest().authenticated()
                );
        return http.build();
    }
}