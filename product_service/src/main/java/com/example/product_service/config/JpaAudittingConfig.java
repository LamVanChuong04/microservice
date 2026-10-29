package com.example.product_service.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.domain.AuditorAware;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import java.util.Optional;

@Configuration
public class JpaAudittingConfig {
    @Bean
    public AuditorAware<String> auditorProvider() {
        ServletRequestAttributes attrs = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
        if (attrs != null) {
            String userId = attrs.getRequest().getHeader("X-User-Id");
            if (userId != null) {
                return () -> Optional.of(userId);
            }
        }
        return () -> Optional.of("system");
    }


}
