package com.vyshnavi.keytrace.repository;

import com.vyshnavi.keytrace.model.UsageLog;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

public interface UsageLogRepository extends JpaRepository<UsageLog, UUID> {

    // This query grabs the stats for a specific key between a start and end date
    @Query("SELECT u.endpoint, COUNT(u), AVG(u.latencyMs) " +
            "FROM UsageLog u " +
            "WHERE u.apiKey.id = :keyId AND u.timestamp BETWEEN :start AND :end " +
            "GROUP BY u.endpoint")
    List<Object[]> getUsageStats(
            @Param("keyId") UUID keyId,
            @Param("start") Instant start,
            @Param("end") Instant end
    );
}