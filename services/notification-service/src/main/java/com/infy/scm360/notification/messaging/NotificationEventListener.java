package com.infy.scm360.notification.messaging;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.infy.scm360.common.event.EventEnvelope;
import com.infy.scm360.common.idempotency.IdempotentConsumer;
import com.infy.scm360.notification.service.NotificationService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.rabbit.annotation.Exchange;
import org.springframework.amqp.rabbit.annotation.Queue;
import org.springframework.amqp.rabbit.annotation.QueueBinding;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

import java.util.Map;

@Component
public class NotificationEventListener {

    private static final Logger log = LoggerFactory.getLogger(NotificationEventListener.class);

    private final IdempotentConsumer idempotentConsumer;
    private final NotificationService notificationService;
    private final ObjectMapper objectMapper;

    public NotificationEventListener(
            IdempotentConsumer idempotentConsumer,
            NotificationService notificationService,
            ObjectMapper objectMapper
    ) {
        this.idempotentConsumer = idempotentConsumer;
        this.notificationService = notificationService;
        this.objectMapper = objectMapper;
    }

    @RabbitListener(bindings = @QueueBinding(
            value = @Queue(value = "notification.inventory.stock.low", durable = "true"),
            exchange = @Exchange(value = "inventory.events", type = "topic"),
            key = "inventory.stock.low"
    ))
    public void onStockLow(String messageJson) {
        try {
            EventEnvelope<Map<String, Object>> envelope = objectMapper.readValue(
                    messageJson, new TypeReference<EventEnvelope<Map<String, Object>>>() {}
            );

            if (!idempotentConsumer.checkAndRecord(envelope.eventId(), "NotifStockLowConsumer")) {
                return;
            }

            Map<String, Object> data = envelope.payload();
            String sku = (String) data.get("productSku");
            String prodName = (String) data.get("productName");

            notificationService.createAndSend(
                    null,
                    "WAREHOUSE_MANAGER",
                    "WARNING",
                    "Low Stock Alert: " + (sku != null ? sku : "Product"),
                    "Stock for " + prodName + " has fallen below safety threshold.",
                    "/inventory"
            );

            notificationService.createAndSend(
                    null,
                    "PROCUREMENT_OFFICER",
                    "INFO",
                    "Replenishment Needed: " + (sku != null ? sku : "Product"),
                    "Automated requisition triggered for " + prodName + ".",
                    "/procurement"
            );
        } catch (Exception e) {
            log.error("Failed to process inventory.stock.low in notifications", e);
        }
    }

    @RabbitListener(bindings = @QueueBinding(
            value = @Queue(value = "notification.procurement.po.approved", durable = "true"),
            exchange = @Exchange(value = "procurement.events", type = "topic"),
            key = "procurement.po.approved"
    ))
    public void onPoApproved(String messageJson) {
        try {
            EventEnvelope<Map<String, Object>> envelope = objectMapper.readValue(
                    messageJson, new TypeReference<EventEnvelope<Map<String, Object>>>() {}
            );

            if (!idempotentConsumer.checkAndRecord(envelope.eventId(), "NotifPoApprovedConsumer")) {
                return;
            }

            Map<String, Object> data = envelope.payload();
            String poNumber = (String) data.get("poNumber");
            String supplierName = (String) data.get("supplierName");

            notificationService.createAndSend(
                    null,
                    "PROCUREMENT_OFFICER",
                    "SUCCESS",
                    "Purchase Order Approved: " + poNumber,
                    "Purchase Order " + poNumber + " for " + supplierName + " has received management approval.",
                    "/procurement"
            );
        } catch (Exception e) {
            log.error("Failed to process procurement.po.approved in notifications", e);
        }
    }

    @RabbitListener(bindings = @QueueBinding(
            value = @Queue(value = "notification.order.backordered", durable = "true"),
            exchange = @Exchange(value = "order.events", type = "topic"),
            key = "order.backordered"
    ))
    public void onOrderBackordered(String messageJson) {
        try {
            EventEnvelope<Map<String, Object>> envelope = objectMapper.readValue(
                    messageJson, new TypeReference<EventEnvelope<Map<String, Object>>>() {}
            );

            if (!idempotentConsumer.checkAndRecord(envelope.eventId(), "NotifOrderBackorderedConsumer")) {
                return;
            }

            Map<String, Object> data = envelope.payload();
            String orderNumber = (String) data.get("orderNumber");

            notificationService.createAndSend(
                    null,
                    "SALES_COORDINATOR",
                    "ALERT",
                    "Order Backordered: " + orderNumber,
                    "Customer Order " + orderNumber + " moved to backordered status due to inventory stockout.",
                    "/orders"
            );
        } catch (Exception e) {
            log.error("Failed to process order.backordered in notifications", e);
        }
    }

    @RabbitListener(bindings = @QueueBinding(
            value = @Queue(value = "notification.transport.shipment.delayed", durable = "true"),
            exchange = @Exchange(value = "transport.events", type = "topic"),
            key = "transport.shipment.delayed"
    ))
    public void onShipmentDelayed(String messageJson) {
        try {
            EventEnvelope<Map<String, Object>> envelope = objectMapper.readValue(
                    messageJson, new TypeReference<EventEnvelope<Map<String, Object>>>() {}
            );

            if (!idempotentConsumer.checkAndRecord(envelope.eventId(), "NotifShipmentDelayedConsumer")) {
                return;
            }

            Map<String, Object> data = envelope.payload();
            String reason = (String) data.get("reason");

            notificationService.createAndSend(
                    null,
                    "LOGISTICS_PLANNER",
                    "WARNING",
                    "Transit Delay Encountered",
                    "Shipment delivery delay: " + (reason != null ? reason : "Weather delay in transit route"),
                    "/transportation"
            );
        } catch (Exception e) {
            log.error("Failed to process transport.shipment.delayed in notifications", e);
        }
    }
}
