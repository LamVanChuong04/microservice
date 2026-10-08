package com.example.api_gateway.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.convert.converter.Converter;
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
                    exchange.pathMatchers("/api/v1/orders/**").hasAnyAuthority("ROLE_CUSTOMER", "ROLE_ADMIN");
                    exchange.pathMatchers("/api/v1/products/**").hasAnyAuthority("ROLE_CUSTOMER", "ROLE_ADMIN");
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
