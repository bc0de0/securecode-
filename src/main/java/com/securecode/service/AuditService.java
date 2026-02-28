package com.securecode.service;

import java.util.Map;
import java.util.UUID;

public interface AuditService {
    void log(UUID actorId, String actionType, String targetType, UUID targetId, Map<String, Object> metadata);
}
