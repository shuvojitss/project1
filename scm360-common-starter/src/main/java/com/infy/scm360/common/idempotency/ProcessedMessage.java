package com.infy.scm360.common.idempotency;

import jakarta.persistence.*;
import java.time.Instant;

@Entity
@Table(name = "processed_message")
@IdClass(ProcessedMessageId.class)
public class ProcessedMessage {

    @Id
    @Column(name = "message_id", length = 36, nullable = false)
    private String messageId;

    @Id
    @Column(name = "consumer", length = 100, nullable = false)
    private String consumer;

    @Column(name = "processed_at", nullable = false)
    private Instant processedAt;

    public ProcessedMessage() {}

    public ProcessedMessage(String messageId, String consumer, Instant processedAt) {
        this.messageId = messageId;
        this.consumer = consumer;
        this.processedAt = processedAt != null ? processedAt : Instant.now();
    }

    public String getMessageId() { return messageId; }
    public void setMessageId(String messageId) { this.messageId = messageId; }
    public String getConsumer() { return consumer; }
    public void setConsumer(String consumer) { this.consumer = consumer; }
    public Instant getProcessedAt() { return processedAt; }
    public void setProcessedAt(Instant processedAt) { this.processedAt = processedAt; }
}
