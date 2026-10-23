package com.vyshnavi.keytrace.dto;

import java.util.UUID;

public record CreateApiKeyResponse(
        UUID id,
        String name,
        String rawApiKey, // We only return this ONCE
        String message
) {}