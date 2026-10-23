package com.vyshnavi.keytrace.controller;

import com.vyshnavi.keytrace.dto.UsageStatsResponse;
import com.vyshnavi.keytrace.repository.UsageLogRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/keys")
@RequiredArgsConstructor
public class UsageController {

    private final UsageLogRepository usageLogRepository;

    @GetMapping("/{keyId}/usage")
    public List<UsageStatsResponse> getUsage(
            @PathVariable UUID keyId,
            @RequestParam(required = false) Instant start,
            @RequestParam(required = false) Instant end) {

        // If no dates provided, default to the last 30 days
        Instant startDate = (start != null) ? start : Instant.now().minus(30, ChronoUnit.DAYS);
        Instant endDate = (end != null) ? end : Instant.now();

        List<Object[]> results = usageLogRepository.getUsageStats(keyId, startDate, endDate);

        // Map the raw database results to our clean DTO
        return results.stream()
                .map(row -> new UsageStatsResponse(
                        (String) row[0],
                        (Long) row[1],
                        (Double) row[2]
                ))
                .collect(Collectors.toList());
    }
}