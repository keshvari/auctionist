package com.example.auctionist.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;


@Configuration
public class TimeConfig {
    @Bean
    public java.time.Clock clock(@Value("${app.clock.fixed:}") String fixed) {
        if (fixed != null && !fixed.isBlank()) {
            return java.time.Clock.fixed(java.time.Instant.parse(fixed), java.time.ZoneOffset.UTC);
        }
        return java.time.Clock.systemUTC();
    }
}


