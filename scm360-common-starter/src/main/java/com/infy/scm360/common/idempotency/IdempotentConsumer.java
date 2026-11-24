package com.infy.scm360.common.idempotency;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;

@Service
public class IdempotentConsumer {

    private static final Logger log = LoggerFactory.getLogger(IdempotentConsumer.class);

    private final ProcessedMessageRepository processedMessageRepository;

    public IdempotentConsumer(ProcessedMessageRepository processedMessageRepository) {
        this.processedMessageRepository = processedMessageRepository;
    }

    /**
     * Checks if the message was already processed. If not, records it.
     * Returns true if processing should proceed, or false if it is a duplicate.
     */
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public boolean checkAndRecord(String messageId, String consumerName) {
        if (messageId == null || consumerName == null) {
            return true;
        }

        if (processedMessageRepository.existsByMessageIdAndConsumer(messageId, consumerName)) {
            log.info("Message [{}] already processed by [{}]. Skipping duplicate.", messageId, consumerName);
            return false;
        }

        try {
            processedMessageRepository.saveAndFlush(new ProcessedMessage(messageId, consumerName, Instant.now()));
            return true;
        } catch (DataIntegrityViolationException ex) {
            log.info("Duplicate key detected for message [{}] and consumer [{}]. Skipping.", messageId, consumerName);
            return false;
        }
    }
}
