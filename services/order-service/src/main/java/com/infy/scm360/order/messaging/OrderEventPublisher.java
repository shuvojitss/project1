package com.infy.scm360.order.messaging;

import com.infy.scm360.common.event.EventEnvelope;
import com.infy.scm360.common.event.EventTypes;
import com.infy.scm360.common.outbox.OutboxService;
import org.springframework.stereotype.Component;

@Component
public class OrderEventPublisher {

    private final OutboxService outboxService;

    public OrderEventPublisher(OutboxService outboxService) {
        this.outboxService = outboxService;
    }

    public void publishOrderPlaced(String orderId, String orderNumber, Object payload) {
        EventEnvelope<?> event = EventEnvelope.of(EventTypes.ORDER_PLACED, "order-service", payload);
        outboxService.publish("order.events", EventTypes.ORDER_PLACED, "CustomerOrder", orderId, event);
    }

    public void publishOrderConfirmed(String orderId, String orderNumber, Object payload) {
        EventEnvelope<?> event = EventEnvelope.of(EventTypes.ORDER_CONFIRMED, "order-service", payload);
        outboxService.publish("order.events", EventTypes.ORDER_CONFIRMED, "CustomerOrder", orderId, event);
    }

    public void publishOrderBackordered(String orderId, String orderNumber, Object payload) {
        EventEnvelope<?> event = EventEnvelope.of(EventTypes.ORDER_BACKORDERED, "order-service", payload);
        outboxService.publish("order.events", EventTypes.ORDER_BACKORDERED, "CustomerOrder", orderId, event);
    }

    public void publishOrderCancelled(String orderId, String orderNumber, Object payload) {
        EventEnvelope<?> event = EventEnvelope.of(EventTypes.ORDER_CANCELLED, "order-service", payload);
        outboxService.publish("order.events", EventTypes.ORDER_CANCELLED, "CustomerOrder", orderId, event);
    }

    public void publishOrderShipped(String orderId, String orderNumber, Object payload) {
        EventEnvelope<?> event = EventEnvelope.of(EventTypes.ORDER_SHIPPED, "order-service", payload);
        outboxService.publish("order.events", EventTypes.ORDER_SHIPPED, "CustomerOrder", orderId, event);
    }

    public void publishOrderDelivered(String orderId, String orderNumber, Object payload) {
        EventEnvelope<?> event = EventEnvelope.of(EventTypes.ORDER_DELIVERED, "order-service", payload);
        outboxService.publish("order.events", EventTypes.ORDER_DELIVERED, "CustomerOrder", orderId, event);
    }
}
