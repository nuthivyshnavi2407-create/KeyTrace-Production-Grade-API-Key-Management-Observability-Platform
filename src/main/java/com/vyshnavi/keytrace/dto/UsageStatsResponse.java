package com.vyshnavi.keytrace.dto;

public record UsageStatsResponse(
        String endpoint,
        Long totalCalls,
        Double averageLatencyMs
) {}
