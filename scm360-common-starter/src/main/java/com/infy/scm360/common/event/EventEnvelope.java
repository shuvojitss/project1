package com.infy.scm360.common.event;

import com.infy.scm360.common.correlation.CorrelationContext;

import java.time.Instant;
import java.util.UUID;

public record EventEnvelope<T>(
    String eventId,
    String eventType,
    int eventVersion,
    Instant occurredAt,
    String producer,
    String correlationId,
    String causationId,
    T payload
) {
    public static <T> EventEnvelope<T> of(String eventType, String producer, T payload) {
        return of(eventType, producer, payload, CorrelationContext.getCorrelationId(), null);
    }

    public static <T> EventEnvelope<T> of(String eventType, String producer, T payload, String correlationId, String causationId) {
        return new EventEnvelope<>(
            UUID.randomUUID().toString(),
            eventType,
            1,
            Instant.now(),
            producer,
            correlationId != null ? correlationId : UUID.randomUUID().toString(),
            causationId,
            payload
        );
    }

    public static <T> EventEnvelope<T> withVersion(String eventType, int version, String producer, T payload, String correlationId, String causationId) {
        return new EventEnvelope<>(
            UUID.randomUUID().toString(),
            eventType,
            version,
            Instant.now(),
            producer,
            correlationId != null ? correlationId : UUID.randomUUID().toString(),
            causationId,
            payload
        );
    }
}
