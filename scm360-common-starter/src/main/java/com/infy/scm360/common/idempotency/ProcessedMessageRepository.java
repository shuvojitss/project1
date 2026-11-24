package com.infy.scm360.common.idempotency;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ProcessedMessageRepository extends JpaRepository<ProcessedMessage, ProcessedMessageId> {
    boolean existsByMessageIdAndConsumer(String messageId, String consumer);
}
