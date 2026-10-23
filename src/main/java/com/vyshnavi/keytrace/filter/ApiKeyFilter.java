package com.vyshnavi.keytrace.filter;

import com.vyshnavi.keytrace.model.ApiKey;
import com.vyshnavi.keytrace.model.UsageLog;
import com.vyshnavi.keytrace.repository.ApiKeyRepository;
import com.vyshnavi.keytrace.repository.UsageLogRepository;
import com.vyshnavi.keytrace.service.RedisService;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.time.Instant;
import java.util.Base64;
import java.util.Optional;

@Component
@RequiredArgsConstructor
public class ApiKeyFilter extends OncePerRequestFilter {

    private final ApiKeyRepository apiKeyRepository;
    private final UsageLogRepository usageLogRepository;
    private final RedisService redisService; // Make sure this is here

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {

        response.setContentType("application/json");

        String authHeader = request.getHeader("Authorization");

        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            response.getWriter().write("{\"error\": \"Missing or invalid Authorization header\"}");
            return;
        }

        String rawKey = authHeader.substring(7);
        String hashedKey = hashKey(rawKey);

        // 1. Redis Revocation Check
        if (redisService.isKeyRevoked(hashedKey)) {
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            response.getWriter().write("{\"error\": \"API Key has been revoked\"}");
            return;
        }

        Optional<ApiKey> apiKeyOpt = apiKeyRepository.findByKeyHash(hashedKey);

        if (apiKeyOpt.isEmpty()) {
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            response.getWriter().write("{\"error\": \"Invalid API Key\"}");
            return;
        }

        ApiKey apiKey = apiKeyOpt.get();

        if (apiKey.getRevokedAt() != null) {
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            response.getWriter().write("{\"error\": \"API Key has been revoked\"}");
            return;
        }

        // 2. Rate Limiting (Redis)
        Long currentCount = redisService.incrementRateLimit(apiKey.getId().toString());
        int limit = apiKey.getRateLimitPerMinute();

        if (currentCount != null && currentCount > limit) {
            response.setStatus(429);
            response.getWriter().write("{\"error\": \"Rate limit exceeded. Limit is " + limit + " per minute.\"}");
            return;
        }

        // ATTACH API KEY TO REQUEST FOR THE CONTROLLER
        request.setAttribute("apiKey", apiKey);

        // 3. Usage Logging
        long startTime = System.currentTimeMillis();
        filterChain.doFilter(request, response);
        long latency = System.currentTimeMillis() - startTime;

        try {
            UsageLog log = UsageLog.builder()
                    .apiKey(apiKey)
                    .endpoint(request.getRequestURI())
                    .statusCode(response.getStatus())
                    .latencyMs(latency)
                    .timestamp(Instant.now())
                    .build();
            usageLogRepository.save(log);
        } catch (Exception e) {
            System.out.println("Failed to save usage log: " + e.getMessage());
        }
    }

    private String hashKey(String rawKey) {
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            byte[] hash = digest.digest(rawKey.getBytes(StandardCharsets.UTF_8));
            return Base64.getEncoder().encodeToString(hash);
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException("Error hashing key", e);
        }
    }
}