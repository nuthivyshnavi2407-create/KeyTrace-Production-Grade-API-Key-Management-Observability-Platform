package com.vyshnavi.keytrace.service;

import com.vyshnavi.keytrace.dto.CreateApiKeyRequest;
import com.vyshnavi.keytrace.dto.CreateApiKeyResponse;
import com.vyshnavi.keytrace.model.ApiKey;
import com.vyshnavi.keytrace.model.User;
import com.vyshnavi.keytrace.repository.ApiKeyRepository;
import com.vyshnavi.keytrace.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.time.Instant;
import java.util.Base64;
import java.util.UUID;

@Service
@RequiredArgsConstructor // THIS IS CRITICAL - IT INJECTS THE REDIS SERVICE
public class ApiKeyService {

    private final ApiKeyRepository apiKeyRepository;
    private final UserRepository userRepository;
    private final RedisService redisService; // Inject Redis

    public CreateApiKeyResponse createApiKey(CreateApiKeyRequest request) {
        User user = userRepository.findById(request.userId())
                .orElseThrow(() -> new RuntimeException("User not found"));

        String rawKey = generateRawKey();
        String hashedKey = hashKey(rawKey);

        ApiKey apiKey = ApiKey.builder()
                .user(user)
                .name(request.name())
                .keyHash(hashedKey)
                .scopes(request.scopes())
                .rateLimitPerMinute(request.rateLimitPerMinute() != null ? request.rateLimitPerMinute() : 60)
                .createdAt(Instant.now())
                .build();

        ApiKey saved = apiKeyRepository.save(apiKey);

        return new CreateApiKeyResponse(
                saved.getId(),
                saved.getName(),
                rawKey,
                "Save this key securely. It will NOT be shown again."
        );
    }

    public void revokeKey(UUID keyId) {
        ApiKey key = apiKeyRepository.findById(keyId).orElseThrow();
        key.setRevokedAt(Instant.now());
        apiKeyRepository.save(key);

        // Add to Redis 1-second revocation cache
        redisService.cacheRevokedKey(key.getKeyHash());
    }

    private String generateRawKey() {
        byte[] randomBytes = new byte[32];
        new SecureRandom().nextBytes(randomBytes);
        return "sk_live_" + Base64.getUrlEncoder().withoutPadding().encodeToString(randomBytes);
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