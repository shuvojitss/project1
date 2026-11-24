package com.infy.scm360.common.outbox;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.core.MessageBuilder;
import org.springframework.amqp.core.MessageProperties;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.nio.charset.StandardCharsets;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.List;

@Component
@EnableScheduling
@ConditionalOnProperty(name = "scm360.outbox.relay.enabled", havingValue = "true", matchIfMissing = true)
public class OutboxRelay {

    private static final Logger log = LoggerFactory.getLogger(OutboxRelay.class);
    private static final int MAX_ATTEMPTS = 10;
    private static final int BATCH_SIZE = 100;

    private final OutboxRepository outboxRepository;
    private final RabbitTemplate rabbitTemplate;

    public OutboxRelay(OutboxRepository outboxRepository, RabbitTemplate rabbitTemplate) {
        this.outboxRepository = outboxRepository;
        this.rabbitTemplate = rabbitTemplate;
    }

    @Scheduled(fixedDelay = 500)
    @Transactional
    public void processOutbox() {
        List<OutboxEvent> pendingEvents;
        try {
            pendingEvents = outboxRepository.findPendingEventsForProcessing(BATCH_SIZE);
        } catch (Exception e) {
            // Fallback for H2 or engines not supporting FOR UPDATE SKIP LOCKED
            pendingEvents = outboxRepository.findTop100ByStatusOrderByCreatedAtAsc(OutboxStatus.PENDING);
        }

        if (pendingEvents.isEmpty()) {
            return;
        }

        for (OutboxEvent event : pendingEvents) {
            try {
                Message message = MessageBuilder.withBody(event.getPayload().getBytes(StandardCharsets.UTF_8))
                        .setContentType(MessageProperties.CONTENT_TYPE_JSON)
                        .setContentEncoding("UTF-8")
                        .setMessageId(event.getId())
                        .build();

                rabbitTemplate.send(event.getExchangeName(), event.getRoutingKey(), message);

                event.setStatus(OutboxStatus.SENT);
                event.setSentAt(Instant.now());
                log.debug("Published outbox event [{}] to exchange [{}] with routing key [{}]",
                        event.getId(), event.getExchangeName(), event.getRoutingKey());
            } catch (Exception ex) {
                int attempts = event.getAttempts() + 1;
                event.setAttempts(attempts);
                if (attempts >= MAX_ATTEMPTS) {
                    event.setStatus(OutboxStatus.FAILED);
                    log.error("Outbox event [{}] failed after {} attempts. Marked as FAILED.", event.getId(), attempts, ex);
                } else {
                    log.warn("Failed to publish outbox event [{}] on attempt {}: {}", event.getId(), attempts, ex.getMessage());
                }
            }
            outboxRepository.save(event);
        }
    }

    // Cleanup job deletes SENT rows older than 7 days
    @Scheduled(cron = "0 0 2 * * *") // Daily at 2 AM
    @Transactional
    public void cleanupOldSentEvents() {
        Instant cutoff = Instant.now().minus(7, ChronoUnit.DAYS);
        int deleted = outboxRepository.deleteOldSentEvents(OutboxStatus.SENT, cutoff);
        if (deleted > 0) {
            log.info("Cleaned up {} sent outbox events older than 7 days.", deleted);
        }
    }
}
