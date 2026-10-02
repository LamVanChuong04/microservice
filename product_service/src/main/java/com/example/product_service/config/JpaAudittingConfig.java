package com.example.product_service.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.domain.AuditorAware;

import java.util.Optional;

@Configuration
public class JpaAudittingConfig {
    @Bean
    public AuditorAware<String> auditorProvider() {
        return () -> Optional.of("system");
    }


}
