package com.infy.scm360.order.messaging;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.infy.scm360.common.event.EventEnvelope;
import com.infy.scm360.common.idempotency.IdempotentConsumer;
import com.infy.scm360.order.service.FulfilmentSagaCoordinator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.rabbit.annotation.Exchange;
import org.springframework.amqp.rabbit.annotation.Queue;
import org.springframework.amqp.rabbit.annotation.QueueBinding;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Map;

@Component
public class OrderEventListener {

    private static final Logger log = LoggerFactory.getLogger(OrderEventListener.class);

    private final IdempotentConsumer idempotentConsumer;
    private final FulfilmentSagaCoordinator sagaCoordinator;
    private final ObjectMapper objectMapper;

    public OrderEventListener(
            IdempotentConsumer idempotentConsumer,
            FulfilmentSagaCoordinator sagaCoordinator,
            ObjectMapper objectMapper
    ) {
        this.idempotentConsumer = idempotentConsumer;
        this.sagaCoordinator = sagaCoordinator;
        this.objectMapper = objectMapper;
    }

    @RabbitListener(bindings = @QueueBinding(
            value = @Queue(value = "order.inventory.stock.reserved", durable = "true"),
            exchange = @Exchange(value = "inventory.events", type = "topic"),
            key = "inventory.stock.reserved"
    ))
    public void onStockReserved(String messageJson) {
        try {
            EventEnvelope<Map<String, Object>> envelope = objectMapper.readValue(
                    messageJson, new TypeReference<EventEnvelope<Map<String, Object>>>() {}
            );

            if (!idempotentConsumer.checkAndRecord(envelope.eventId(), "OrderStockReservedConsumer")) {
                return;
            }

            String orderId = (String) envelope.payload().get("orderId");
            log.info("Processing stock reserved for orderId: {}", orderId);
            if (orderId != null) {
                sagaCoordinator.handleStockReserved(orderId);
            }
        } catch (Exception e) {
            log.error("Failed to process inventory.stock.reserved", e);
        }
    }

    @RabbitListener(bindings = @QueueBinding(
            value = @Queue(value = "order.inventory.reservation.failed", durable = "true"),
            exchange = @Exchange(value = "inventory.events", type = "topic"),
            key = "inventory.reservation.failed"
    ))
    public void onReservationFailed(String messageJson) {
        try {
            EventEnvelope<Map<String, Object>> envelope = objectMapper.readValue(
                    messageJson, new TypeReference<EventEnvelope<Map<String, Object>>>() {}
            );

            if (!idempotentConsumer.checkAndRecord(envelope.eventId(), "OrderReservationFailedConsumer")) {
                return;
            }

            Map<String, Object> data = envelope.payload();
            String orderId = (String) data.get("orderId");
            String productId = (String) data.get("productId");
            Number qty = (Number) data.get("quantity");
            String reason = (String) data.get("reason");

            log.info("Processing reservation failed for orderId: {}", orderId);
            if (orderId != null) {
                sagaCoordinator.handleReservationFailed(
                        orderId,
                        productId,
                        qty != null ? BigDecimal.valueOf(qty.doubleValue()) : BigDecimal.ONE,
                        reason != null ? reason : "Insufficient stock"
                );
            }
        } catch (Exception e) {
            log.error("Failed to process inventory.reservation.failed", e);
        }
    }

    @RabbitListener(bindings = @QueueBinding(
            value = @Queue(value = "order.warehouse.picked", durable = "true"),
            exchange = @Exchange(value = "warehouse.events", type = "topic"),
            key = "warehouse.order.picked"
    ))
    public void onOrderPicked(String messageJson) {
        try {
            EventEnvelope<Map<String, Object>> envelope = objectMapper.readValue(
                    messageJson, new TypeReference<EventEnvelope<Map<String, Object>>>() {}
            );

            if (!idempotentConsumer.checkAndRecord(envelope.eventId(), "OrderPickedConsumer")) {
                return;
            }

            String orderId = (String) envelope.payload().get("orderId");
            if (orderId != null) {
                sagaCoordinator.handleOrderPicked(orderId);
            }
        } catch (Exception e) {
            log.error("Failed to process warehouse.order.picked", e);
        }
    }

    @RabbitListener(bindings = @QueueBinding(
            value = @Queue(value = "order.warehouse.packed", durable = "true"),
            exchange = @Exchange(value = "warehouse.events", type = "topic"),
            key = "warehouse.order.packed"
    ))
    public void onOrderPacked(String messageJson) {
        try {
            EventEnvelope<Map<String, Object>> envelope = objectMapper.readValue(
                    messageJson, new TypeReference<EventEnvelope<Map<String, Object>>>() {}
            );

            if (!idempotentConsumer.checkAndRecord(envelope.eventId(), "OrderPackedConsumer")) {
                return;
            }

            String orderId = (String) envelope.payload().get("orderId");
            if (orderId != null) {
                sagaCoordinator.handleOrderPacked(orderId);
            }
        } catch (Exception e) {
            log.error("Failed to process warehouse.order.packed", e);
        }
    }

    @RabbitListener(bindings = @QueueBinding(
            value = @Queue(value = "order.transport.dispatched", durable = "true"),
            exchange = @Exchange(value = "transport.events", type = "topic"),
            key = "transport.shipment.dispatched"
    ))
    public void onShipmentDispatched(String messageJson) {
        try {
            EventEnvelope<Map<String, Object>> envelope = objectMapper.readValue(
                    messageJson, new TypeReference<EventEnvelope<Map<String, Object>>>() {}
            );

            if (!idempotentConsumer.checkAndRecord(envelope.eventId(), "OrderShipmentDispatchedConsumer")) {
                return;
            }

            Map<String, Object> data = envelope.payload();
            String orderId = (String) data.get("orderId");
            String carrierId = (String) data.get("carrierId");
            String trackingNumber = (String) data.get("trackingNumber");
            String estDateStr = (String) data.get("estimatedDeliveryDate");
            LocalDate estDate = estDateStr != null ? LocalDate.parse(estDateStr) : null;

            if (orderId != null) {
                sagaCoordinator.handleShipmentDispatched(orderId, carrierId, trackingNumber, estDate);
            }
        } catch (Exception e) {
            log.error("Failed to process transport.shipment.dispatched", e);
        }
    }

    @RabbitListener(bindings = @QueueBinding(
            value = @Queue(value = "order.transport.delivered", durable = "true"),
            exchange = @Exchange(value = "transport.events", type = "topic"),
            key = "transport.shipment.delivered"
    ))
    public void onShipmentDelivered(String messageJson) {
        try {
            EventEnvelope<Map<String, Object>> envelope = objectMapper.readValue(
                    messageJson, new TypeReference<EventEnvelope<Map<String, Object>>>() {}
            );

            if (!idempotentConsumer.checkAndRecord(envelope.eventId(), "OrderShipmentDeliveredConsumer")) {
                return;
            }

            String orderId = (String) envelope.payload().get("orderId");
            if (orderId != null) {
                sagaCoordinator.handleShipmentDelivered(orderId);
            }
        } catch (Exception e) {
            log.error("Failed to process transport.shipment.delivered", e);
        }
    }
}
