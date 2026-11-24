package com.infy.scm360.identity.service;

import com.infy.scm360.identity.domain.AuditLog;
import com.infy.scm360.identity.repository.AuditLogRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
public class AuditService {

    private final AuditLogRepository auditLogRepository;

    public AuditService(AuditLogRepository auditLogRepository) {
        this.auditLogRepository = auditLogRepository;
    }

    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void record(String userId, String action, String details, String ipAddress) {
        AuditLog log = new AuditLog(
            UUID.randomUUID().toString(),
            userId,
            action,
            details,
            ipAddress
        );
        auditLogRepository.save(log);
    }
}
