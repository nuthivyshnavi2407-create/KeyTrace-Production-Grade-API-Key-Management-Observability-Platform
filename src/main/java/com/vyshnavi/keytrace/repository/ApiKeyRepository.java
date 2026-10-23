package com.vyshnavi.keytrace.repository;

import com.vyshnavi.keytrace.model.ApiKey;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional; // Add this import
import java.util.UUID;

public interface ApiKeyRepository extends JpaRepository<ApiKey, UUID> {
    // Add this line:
    Optional<ApiKey> findByKeyHash(String keyHash);
}