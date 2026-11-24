package com.infy.scm360.analytics.messaging;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.infy.scm360.analytics.domain.FactOrder;
import com.infy.scm360.analytics.domain.FactPo;
import com.infy.scm360.analytics.domain.FactShipment;
import com.infy.scm360.analytics.repository.FactOrderRepository;
import com.infy.scm360.analytics.repository.FactPoRepository;
import com.infy.scm360.analytics.repository.FactShipmentRepository;
import com.infy.scm360.common.event.EventEnvelope;
import com.infy.scm360.common.idempotency.IdempotentConsumer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.rabbit.annotation.Exchange;
import org.springframework.amqp.rabbit.annotation.Queue;
import org.springframework.amqp.rabbit.annotation.QueueBinding;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.Map;

@Component
public class AnalyticsEventListener {

    private static final Logger log = LoggerFactory.getLogger(AnalyticsEventListener.class);

    private final IdempotentConsumer idempotentConsumer;
    private final FactOrderRepository orderRepo;
    private final FactPoRepository poRepo;
    private final FactShipmentRepository shipmentRepo;
    private final ObjectMapper objectMapper;

    public AnalyticsEventListener(
            IdempotentConsumer idempotentConsumer,
            FactOrderRepository orderRepo,
            FactPoRepository poRepo,
            FactShipmentRepository shipmentRepo,
            ObjectMapper objectMapper
    ) {
        this.idempotentConsumer = idempotentConsumer;
        this.orderRepo = orderRepo;
        this.poRepo = poRepo;
        this.shipmentRepo = shipmentRepo;
        this.objectMapper = objectMapper;
    }

    @RabbitListener(bindings = @QueueBinding(
            value = @Queue(value = "analytics.order.placed", durable = "true"),
            exchange = @Exchange(value = "order.events", type = "topic"),
            key = "order.placed"
    ))
    public void onOrderPlaced(String messageJson) {
        try {
            EventEnvelope<Map<String, Object>> envelope = objectMapper.readValue(
                    messageJson, new TypeReference<EventEnvelope<Map<String, Object>>>() {}
            );

            if (!idempotentConsumer.checkAndRecord(envelope.eventId(), "AnalyticsOrderPlacedConsumer")) {
                return;
            }

            Map<String, Object> data = envelope.payload();
            String orderId = (String) data.get("orderId");
            String orderNumber = (String) data.get("orderNumber");
            String warehouseId = (String) data.get("warehouseId");

            if (orderId != null) {
                FactOrder fact = orderRepo.findByOrderId(orderId).orElseGet(FactOrder::new);
                fact.setOrderId(orderId);
                fact.setOrderNumber(orderNumber != null ? orderNumber : "ORD-" + orderId.substring(0, 8));
                fact.setCustomerId("CUST-UNKNOWN");
                fact.setCustomerName("Customer");
                fact.setOriginWarehouseId(warehouseId != null ? warehouseId : "WH-DEFAULT");
                fact.setStatus("CREATED");
                fact.setCreatedAt(Instant.now());
                orderRepo.save(fact);
            }
        } catch (Exception e) {
            log.error("Failed to process order.placed in analytics", e);
        }
    }

    @RabbitListener(bindings = @QueueBinding(
            value = @Queue(value = "analytics.po.issued", durable = "true"),
            exchange = @Exchange(value = "procurement.events", type = "topic"),
            key = "procurement.po.issued"
    ))
    public void onPoIssued(String messageJson) {
        try {
            EventEnvelope<Map<String, Object>> envelope = objectMapper.readValue(
                    messageJson, new TypeReference<EventEnvelope<Map<String, Object>>>() {}
            );

            if (!idempotentConsumer.checkAndRecord(envelope.eventId(), "AnalyticsPoIssuedConsumer")) {
                return;
            }

            Map<String, Object> data = envelope.payload();
            String poId = (String) data.get("id");
            String poNumber = (String) data.get("poNumber");
            String supplierId = (String) data.get("supplierId");
            String supplierName = (String) data.get("supplierName");
            Number totalAmt = (Number) data.get("totalAmount");

            if (poId != null) {
                FactPo fact = poRepo.findByPoId(poId).orElseGet(FactPo::new);
                fact.setPoId(poId);
                fact.setPoNumber(poNumber != null ? poNumber : "PO-" + poId.substring(0, 8));
                fact.setSupplierId(supplierId != null ? supplierId : "VEND-UNKNOWN");
                fact.setSupplierName(supplierName != null ? supplierName : "Supplier");
                fact.setStatus("ISSUED");
                fact.setTotalAmount(totalAmt != null ? BigDecimal.valueOf(totalAmt.doubleValue()) : BigDecimal.ZERO);
                fact.setIssuedAt(Instant.now());
                poRepo.save(fact);
            }
        } catch (Exception e) {
            log.error("Failed to process procurement.po.issued in analytics", e);
        }
    }

    @RabbitListener(bindings = @QueueBinding(
            value = @Queue(value = "analytics.transport.dispatched", durable = "true"),
            exchange = @Exchange(value = "transport.events", type = "topic"),
            key = "transport.shipment.dispatched"
    ))
    public void onShipmentDispatched(String messageJson) {
        try {
            EventEnvelope<Map<String, Object>> envelope = objectMapper.readValue(
                    messageJson, new TypeReference<EventEnvelope<Map<String, Object>>>() {}
            );

            if (!idempotentConsumer.checkAndRecord(envelope.eventId(), "AnalyticsShipmentDispatchedConsumer")) {
                return;
            }

            Map<String, Object> data = envelope.payload();
            String shipmentId = (String) data.get("shipmentId");
            String shipmentNumber = (String) data.get("shipmentNumber");
            String carrierId = (String) data.get("carrierId");

            if (shipmentId != null) {
                FactShipment fact = shipmentRepo.findByShipmentId(shipmentId).orElseGet(FactShipment::new);
                fact.setShipmentId(shipmentId);
                fact.setShipmentNumber(shipmentNumber != null ? shipmentNumber : "SHP-" + shipmentId.substring(0, 8));
                fact.setCarrierId(carrierId != null ? carrierId : "CAR-UNKNOWN");
                fact.setCarrierName("Carrier Partner");
                fact.setOriginRegion("US_MIDWEST");
                fact.setDestinationRegion("US_MIDWEST");
                fact.setStatus("DISPATCHED");
                fact.setDispatchedAt(Instant.now());
                shipmentRepo.save(fact);
            }
        } catch (Exception e) {
            log.error("Failed to process transport.shipment.dispatched in analytics", e);
        }
    }
}
