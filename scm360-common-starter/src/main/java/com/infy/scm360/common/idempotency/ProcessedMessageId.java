package com.infy.scm360.common.idempotency;

import java.io.Serializable;
import java.util.Objects;

public class ProcessedMessageId implements Serializable {
    private String messageId;
    private String consumer;

    public ProcessedMessageId() {}

    public ProcessedMessageId(String messageId, String consumer) {
        this.messageId = messageId;
        this.consumer = consumer;
    }

    public String getMessageId() { return messageId; }
    public void setMessageId(String messageId) { this.messageId = messageId; }
    public String getConsumer() { return consumer; }
    public void setConsumer(String consumer) { this.consumer = consumer; }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof ProcessedMessageId that)) return false;
        return Objects.equals(messageId, that.messageId) && Objects.equals(consumer, that.consumer);
    }

    @Override
    public int hashCode() {
        return Objects.hash(messageId, consumer);
    }
}
