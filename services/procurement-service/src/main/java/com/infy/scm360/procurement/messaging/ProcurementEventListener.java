package com.infy.scm360.procurement.messaging;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.infy.scm360.common.event.EventEnvelope;
import com.infy.scm360.common.idempotency.IdempotentConsumer;
import com.infy.scm360.procurement.dto.MatchReceiptRequest;
import com.infy.scm360.procurement.service.PurchaseOrderService;
import com.infy.scm360.procurement.service.RequisitionService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.rabbit.annotation.Exchange;
import org.springframework.amqp.rabbit.annotation.Queue;
import org.springframework.amqp.rabbit.annotation.QueueBinding;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;

@Component
public class ProcurementEventListener {

    private static final Logger log = LoggerFactory.getLogger(ProcurementEventListener.class);

    private final IdempotentConsumer idempotentConsumer;
    private final RequisitionService requisitionService;
    private final PurchaseOrderService purchaseOrderService;
    private final ObjectMapper objectMapper;

    public ProcurementEventListener(
            IdempotentConsumer idempotentConsumer,
            RequisitionService requisitionService,
            PurchaseOrderService purchaseOrderService,
            ObjectMapper objectMapper
    ) {
        this.idempotentConsumer = idempotentConsumer;
        this.requisitionService = requisitionService;
        this.purchaseOrderService = purchaseOrderService;
        this.objectMapper = objectMapper;
    }

    @RabbitListener(bindings = @QueueBinding(
            value = @Queue(value = "procurement.inventory.stock.low", durable = "true"),
            exchange = @Exchange(value = "inventory.events", type = "topic"),
            key = "inventory.stock.low"
    ))
    public void onStockLow(String messageJson) {
        try {
            EventEnvelope<Map<String, Object>> envelope = objectMapper.readValue(
                    messageJson, new TypeReference<EventEnvelope<Map<String, Object>>>() {}
            );

            if (!idempotentConsumer.checkAndRecord(envelope.eventId(), "ProcurementLowStockConsumer")) {
                return;
            }

            Map<String, Object> data = envelope.payload();
            log.info("Processing stock low event for product: {}", data.get("productId"));

            Boolean autoReorder = (Boolean) data.getOrDefault("autoReorder", true);
            if (Boolean.TRUE.equals(autoReorder)) {
                String productId = (String) data.get("productId");
                String productSku = (String) data.get("productSku");
                String productName = (String) data.get("productName");
                Number quantity = (Number) data.getOrDefault("reorderQuantity", 100);
                Number estPrice = (Number) data.getOrDefault("estimatedUnitPrice", 50.0);

                requisitionService.createAutoRequisitionForLowStock(
                        productId,
                        productSku != null ? productSku : "SKU-" + productId.substring(0, 8),
                        productName != null ? productName : "Stock Replenishment",
                        BigDecimal.valueOf(quantity.doubleValue()),
                        BigDecimal.valueOf(estPrice.doubleValue()),
                        "Automated requisition triggered by inventory.stock.low"
                );
            }
        } catch (Exception e) {
            log.error("Failed to process inventory.stock.low message", e);
        }
    }

    @RabbitListener(bindings = @QueueBinding(
            value = @Queue(value = "procurement.warehouse.goods.received", durable = "true"),
            exchange = @Exchange(value = "warehouse.events", type = "topic"),
            key = "warehouse.goods.received"
    ))
    public void onGoodsReceived(String messageJson) {
        try {
            EventEnvelope<Map<String, Object>> envelope = objectMapper.readValue(
                    messageJson, new TypeReference<EventEnvelope<Map<String, Object>>>() {}
            );

            if (!idempotentConsumer.checkAndRecord(envelope.eventId(), "ProcurementGoodsReceivedConsumer")) {
                return;
            }

            Map<String, Object> data = envelope.payload();
            log.info("Processing goods received event: {}", envelope.eventId());

            String poId = (String) data.get("poId");
            String receiptNumber = (String) data.get("receiptNumber");
            String warehouseId = (String) data.get("warehouseId");
            String receivedBy = (String) data.get("receivedBy");

            if (poId != null && receiptNumber != null) {
                Map<String, BigDecimal> productQtys = new HashMap<>();
                Object itemsObj = data.get("items");
                if (itemsObj instanceof Iterable<?> items) {
                    for (Object item : items) {
                        if (item instanceof Map<?, ?> itemMap) {
                            String productId = (String) itemMap.get("productId");
                            Number qty = (Number) itemMap.get("receivedQuantity");
                            if (productId != null && qty != null) {
                                productQtys.put(productId, BigDecimal.valueOf(qty.doubleValue()));
                            }
                        }
                    }
                }

                if (!productQtys.isEmpty()) {
                    purchaseOrderService.matchReceipt(
                            poId,
                            new MatchReceiptRequest(receiptNumber, warehouseId != null ? warehouseId : "WH-DEFAULT", productQtys),
                            receivedBy
                    );
                }
            }
        } catch (Exception e) {
            log.error("Failed to process warehouse.goods.received message", e);
        }
    }

    @RabbitListener(bindings = @QueueBinding(
            value = @Queue(value = "procurement.supplier.acknowledged", durable = "true"),
            exchange = @Exchange(value = "supplier.events", type = "topic"),
            key = "supplier.po.acknowledged"
    ))
    public void onPoAcknowledged(String messageJson) {
        try {
            EventEnvelope<Map<String, Object>> envelope = objectMapper.readValue(
                    messageJson, new TypeReference<EventEnvelope<Map<String, Object>>>() {}
            );

            if (!idempotentConsumer.checkAndRecord(envelope.eventId(), "ProcurementPoAcknowledgedConsumer")) {
                return;
            }

            String poId = (String) envelope.payload().get("poId");
            log.info("Processing PO acknowledged event for poId: {}", poId);
            if (poId != null) {
                purchaseOrderService.acknowledgePurchaseOrder(poId, "Supplier acknowledged PO through portal");
            }
        } catch (Exception e) {
            log.error("Failed to process supplier.po.acknowledged message", e);
        }
    }
}
