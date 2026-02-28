package com.securecode.service;

import io.github.bucket4j.Bucket;
import java.util.UUID;

public interface RateLimitService {
    Bucket resolveBucket(UUID tenantId);
}
