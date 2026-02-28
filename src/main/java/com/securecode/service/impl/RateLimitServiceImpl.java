package com.securecode.service.impl;

import com.securecode.service.RateLimitService;
import io.github.bucket4j.Bandwidth;
import io.github.bucket4j.Bucket;
import io.github.bucket4j.Refill;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

@Service
public class RateLimitServiceImpl implements RateLimitService {

    private final Map<UUID, Bucket> cache = new ConcurrentHashMap<>();

    @Override
    public Bucket resolveBucket(UUID tenantId) {
        return cache.computeIfAbsent(tenantId, this::newBucket);
    }

    private Bucket newBucket(UUID tenantId) {
        Bandwidth limit = Bandwidth.classic(10, Refill.greedy(10, Duration.ofMinutes(1)));
        return Bucket.builder()
                .addLimit(limit)
                .build();
    }
}
