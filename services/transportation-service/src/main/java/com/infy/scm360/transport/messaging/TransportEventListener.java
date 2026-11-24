package com.infy.scm360.transport.messaging;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.infy.scm360.common.event.EventEnvelope;
import com.infy.scm360.common.idempotency.IdempotentConsumer;
import com.infy.scm360.transport.dto.CreateShipmentRequest;
import com.infy.scm360.transport.dto.ShipmentItemDto;
import com.infy.scm360.transport.service.ShipmentService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.rabbit.annotation.Exchange;
import org.springframework.amqp.rabbit.annotation.Queue;
import org.springframework.amqp.rabbit.annotation.QueueBinding;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Component
public class TransportEventListener {

    private static final Logger log = LoggerFactory.getLogger(TransportEventListener.class);

    private final IdempotentConsumer idempotentConsumer;
    private final ShipmentService shipmentService;
    private final ObjectMapper objectMapper;

    public TransportEventListener(
            IdempotentConsumer idempotentConsumer,
            ShipmentService shipmentService,
            ObjectMapper objectMapper
    ) {
        this.idempotentConsumer = idempotentConsumer;
        this.shipmentService = shipmentService;
        this.objectMapper = objectMapper;
    }

    @RabbitListener(bindings = @QueueBinding(
            value = @Queue(value = "transport.warehouse.packed", durable = "true"),
            exchange = @Exchange(value = "warehouse.events", type = "topic"),
            key = "warehouse.order.packed"
    ))
    public void onOrderPacked(String messageJson) {
        try {
            EventEnvelope<Map<String, Object>> envelope = objectMapper.readValue(
                    messageJson, new TypeReference<EventEnvelope<Map<String, Object>>>() {}
            );

            if (!idempotentConsumer.checkAndRecord(envelope.eventId(), "TransportOrderPackedConsumer")) {
                return;
            }

            Map<String, Object> data = envelope.payload();
            String orderId = (String) data.get("orderId");
            String orderNumber = (String) data.get("orderNumber");
            String warehouseId = (String) data.get("warehouseId");

            log.info("Processing order packed for orderId: {}", orderId);
            if (orderId != null) {
                List<ShipmentItemDto> items = new ArrayList<>();
                Object itemsObj = data.get("items");
                if (itemsObj instanceof Iterable<?> itemList) {
                    for (Object itm : itemList) {
                        if (itm instanceof Map<?, ?> itmMap) {
                            String prodId = (String) itmMap.get("productId");
                            String sku = (String) itmMap.get("productSku");
                            String name = (String) itmMap.get("productName");
                            Number qty = (Number) itmMap.get("quantity");
                            Object rawWt = itmMap.get("weightKg");
                            double wt = (rawWt instanceof Number num) ? num.doubleValue() : 10.0;
                            items.add(new ShipmentItemDto(
                                    null,
                                    prodId != null ? prodId : "PROD-GEN",
                                    sku != null ? sku : "SKU-GEN",
                                    name != null ? name : "Packaged Goods",
                                    qty != null ? BigDecimal.valueOf(qty.doubleValue()) : BigDecimal.ONE,
                                    BigDecimal.valueOf(wt)
                            ));
                        }
                    }
                }

                if (items.isEmpty()) {
                    items.add(new ShipmentItemDto(
                            null, "PROD-DEF", "SKU-DEF", "Standard Pallet", BigDecimal.ONE, BigDecimal.valueOf(50.0)
                    ));
                }

                CreateShipmentRequest req = new CreateShipmentRequest(
                        orderId,
                        orderNumber != null ? orderNumber : "ORD-" + orderId.substring(0, 8),
                        "Warehouse Facility: " + warehouseId,
                        "US_MIDWEST",
                        "Customer Destination Delivery Address",
                        "US_MIDWEST",
                        null,
                        "USD",
                        items
                );
                shipmentService.createShipment(req);
            }
        } catch (Exception e) {
            log.error("Failed to process warehouse.order.packed in transport", e);
        }
    }
}
