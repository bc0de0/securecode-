package com.securecode.service.impl;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.securecode.model.AuditLog;
import com.securecode.repository.AuditLogRepository;
import com.securecode.service.AuditService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.Map;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
public class AuditServiceImpl implements AuditService {

    private final AuditLogRepository auditLogRepository;
    private final ObjectMapper objectMapper;

    @Override
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void log(UUID actorId, String actionType, String targetType, UUID targetId, Map<String, Object> metadata) {
        try {
            String metadataJson = metadata != null ? objectMapper.writeValueAsString(metadata) : null;

            AuditLog auditLog = AuditLog.builder()
                    .actorId(actorId)
                    .actionType(actionType)
                    .targetType(targetType)
                    .targetId(targetId)
                    .metadataJson(metadataJson)
                    .build();

            auditLogRepository.save(auditLog);
            log.info("Audit Logged: {} by {} on {} {}", actionType, actorId, targetType, targetId);
        } catch (Exception e) {
            log.error("Failed to persist audit log", e);
        }
    }
}
