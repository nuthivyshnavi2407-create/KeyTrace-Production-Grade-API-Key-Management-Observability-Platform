package com.vyshnavi.keytrace.config;

import io.lettuce.core.ClientOptions;
import io.lettuce.core.SslOptions;
import org.springframework.boot.autoconfigure.data.redis.LettuceClientConfigurationBuilderCustomizer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RedisConfig {

    @Bean
    public LettuceClientConfigurationBuilderCustomizer lettuceClientConfigurationBuilderCustomizer() {
        return builder -> {
            // Force SSL and use RESP2 protocol (required for Upstash stability)
            builder.clientOptions(ClientOptions.builder()
                    .sslOptions(SslOptions.builder().build())
                    .protocolVersion(io.lettuce.core.protocol.ProtocolVersion.RESP2)
                    .build());
            // Explicitly enable SSL on the builder
            builder.useSsl();
        };
    }
}
