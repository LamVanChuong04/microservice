package com.example.api_gateway.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.convert.converter.Converter;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AbstractAuthenticationToken;
import org.springframework.security.config.web.server.ServerHttpSecurity;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationConverter;
import org.springframework.security.oauth2.server.resource.authentication.ReactiveJwtAuthenticationConverterAdapter;
import org.springframework.security.web.server.SecurityWebFilterChain;
import reactor.core.publisher.Mono;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Configuration
public class SecurityConfig {
    @Bean
    public SecurityWebFilterChain springSecurityFilterChain(ServerHttpSecurity http) throws Exception {
        http
                .csrf(csrf -> csrf.disable())
                .authorizeExchange(exchange -> {
                    //
                    exchange.pathMatchers(HttpMethod.GET, "/api/v1/orders/**").permitAll();
                    exchange.pathMatchers(HttpMethod.GET, "/api/v1/category/**").permitAll();
                    exchange.pathMatchers(HttpMethod.GET, "/api/v1/products/**").permitAll();
                    exchange.pathMatchers("/api/v1/auth/**").permitAll();
                    // them, xoa, sua order
                    exchange.pathMatchers(HttpMethod.GET,"/api/v1/orders").hasAuthority("ROLE_ADMIN");
                    exchange.pathMatchers(HttpMethod.DELETE, "/api/v1/orders").hasAuthority("ROLE_ADMIN");
                    exchange.pathMatchers(HttpMethod.POST, "/api/v1/orders").hasAnyAuthority("ROLE_ADMIN", "ROLE_CUSTOMER");
                    // them, xoa, sua product
                    exchange.pathMatchers(HttpMethod.POST, "/api/v1/products").hasAuthority("ROLE_ADMIN");
                    exchange.pathMatchers(HttpMethod.DELETE, "/api/v1/products").hasAuthority("ROLE_ADMIN");
                    exchange.pathMatchers(HttpMethod.PUT, "/api/v1/products").hasAuthority("ROLE_ADMIN");
                    // them, xoa, sua category
                    exchange.pathMatchers(HttpMethod.PUT, "/api/v1/categories").hasAuthority("ROLE_ADMIN");
                    exchange.pathMatchers(HttpMethod.POST, "/api/v1/categories").hasAuthority("ROLE_ADMIN");
                    exchange.pathMatchers(HttpMethod.DELETE, "/api/v1/categories").hasAuthority("ROLE_ADMIN");

                    // hasRole -> + prefix (ROLE_)
                    // hasAuthority -> giữ nguyên
                })
                .oauth2ResourceServer(oauth ->
                        oauth
                                .jwt(jwt -> jwt.jwtAuthenticationConverter(jwtAuthenticationConverter()))
                );

        return http.build();
    }

    private Converter<Jwt, Mono<AbstractAuthenticationToken>> jwtAuthenticationConverter() {
        JwtAuthenticationConverter converter = new JwtAuthenticationConverter();

        converter.setJwtGrantedAuthoritiesConverter(jwt -> {
            Map<String, Object> resourceAccess = jwt.getClaim("resource_access");

            if (resourceAccess == null) return List.of();

            Map<String, Object> backendAccess =
                    (Map<String, Object>) resourceAccess.get("backend");

            if (backendAccess == null) return List.of();

            List<String> roles = (List<String>) backendAccess.get("roles");

            if (roles == null) return List.of();

            return roles.stream()
                    .map(role -> new SimpleGrantedAuthority("ROLE_" + role))
                    .collect(Collectors.toList());
        });

        return new ReactiveJwtAuthenticationConverterAdapter(converter);
    }
}
