package com.vyshnavi.keytrace.dto;

import java.util.List;
import java.util.UUID;

public record CreateApiKeyRequest(
        UUID userId,
        String name,
        List<String> scopes,
        Integer rateLimitPerMinute
) {}