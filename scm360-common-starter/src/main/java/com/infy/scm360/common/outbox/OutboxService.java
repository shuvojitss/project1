package com.infy.scm360.common.outbox;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.UUID;

@Service
public class OutboxService {

    private final OutboxRepository outboxRepository;
    private final ObjectMapper objectMapper;

    public OutboxService(OutboxRepository outboxRepository, ObjectMapper objectMapper) {
        this.outboxRepository = outboxRepository;
        this.objectMapper = objectMapper;
    }

    @Transactional(propagation = Propagation.MANDATORY)
    public OutboxEvent publish(String exchangeName, String routingKey, String aggregateType, String aggregateId, Object payload) {
        String payloadJson;
        if (payload instanceof String str) {
            payloadJson = str;
        } else {
            try {
                payloadJson = objectMapper.writeValueAsString(payload);
            } catch (JsonProcessingException e) {
                throw new RuntimeException("Failed to serialize outbox event payload", e);
            }
        }

        OutboxEvent event = new OutboxEvent(
            UUID.randomUUID().toString(),
            aggregateType,
            aggregateId,
            exchangeName,
            routingKey,
            payloadJson,
            Instant.now()
        );

        return outboxRepository.save(event);
    }
}
