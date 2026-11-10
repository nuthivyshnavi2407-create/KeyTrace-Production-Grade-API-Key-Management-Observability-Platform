package com.vyshnavi.keytrace.config;

import org.springframework.boot.autoconfigure.data.redis.LettuceClientConfigurationBuilderCustomizer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RedisConfig {

    @Bean
    public LettuceClientConfigurationBuilderCustomizer lettuceClientConfigurationBuilderCustomizer() {
        // This tells Spring Data Redis to use SSL (required for Upstash)
        return builder -> builder.useSsl().disablePeerVerification();
    }
}