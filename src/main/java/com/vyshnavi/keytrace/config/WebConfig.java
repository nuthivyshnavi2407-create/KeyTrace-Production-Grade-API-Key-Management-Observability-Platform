package com.vyshnavi.keytrace.config;

import com.vyshnavi.keytrace.filter.ApiKeyFilter;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.Ordered;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import org.springframework.web.filter.CorsFilter;

import java.util.Arrays;

@Configuration
@RequiredArgsConstructor
public class WebConfig {

    private final ApiKeyFilter apiKeyFilter;

    // Register the API Key Filter for the /api/v1/* paths
    @Bean
    public FilterRegistrationBean<ApiKeyFilter> apiKeyFilterRegistration() {
        FilterRegistrationBean<ApiKeyFilter> registration = new FilterRegistrationBean<>();
        registration.setFilter(apiKeyFilter);
        registration.addUrlPatterns("/api/v1/*");
        registration.setOrder(2); // Runs after CORS
        return registration;
    }

    // CORS Filter to allow React (Vercel) to talk to Spring Boot (Render)
    @Bean
    public FilterRegistrationBean<CorsFilter> corsFilter() {
        CorsConfiguration config = new CorsConfiguration();

        // Allow localhost AND your live Vercel frontend URL
        config.setAllowedOrigins(Arrays.asList(
                "http://localhost:5173",
                "https://key-trace-production-grade-api-key-management-observ-5jsm46w9d.vercel.app"
        ));

        config.setAllowedMethods(Arrays.asList("GET", "POST", "PUT", "DELETE", "OPTIONS"));
        config.setAllowedHeaders(Arrays.asList("*"));
        config.setAllowCredentials(true);
        config.setMaxAge(3600L);

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", config);

        FilterRegistrationBean<CorsFilter> bean = new FilterRegistrationBean<>(new CorsFilter(source));
        bean.setOrder(Ordered.HIGHEST_PRECEDENCE); // Runs first, before everything else
        return bean;
    }
}