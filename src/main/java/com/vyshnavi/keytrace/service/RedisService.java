package com.vyshnavi.keytrace.service;

import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import java.time.Duration;

@Service
@RequiredArgsConstructor
public class RedisService {

    private final StringRedisTemplate redisTemplate;

    public Long incrementRateLimit(String keyId) {
        String redisKey = "rate_limit:" + keyId;
        Long count = redisTemplate.opsForValue().increment(redisKey);
        if (count != null && count == 1) {
            redisTemplate.expire(redisKey, Duration.ofMinutes(1));
        }
        return count;
    }

    public void cacheRevokedKey(String hashedKey) {
        String redisKey = "revoked:" + hashedKey;
        redisTemplate.opsForValue().set(redisKey, "true", Duration.ofDays(30));
    }

    public boolean isKeyRevoked(String hashedKey) {
        String redisKey = "revoked:" + hashedKey;
        return Boolean.TRUE.equals(redisTemplate.hasKey(redisKey));
    }
}
