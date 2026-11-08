package com.vyshnavi.keytrace.service;

import org.springframework.stereotype.Service;

@Service
public class RedisService {
    public Long incrementRateLimit(String keyId) { return 1L; }
    public void cacheRevokedKey(String hashedKey) { }
    public boolean isKeyRevoked(String hashedKey) { return false; }
}
