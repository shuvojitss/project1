package com.infy.scm360.order.service;

import com.infy.scm360.common.exception.ResourceNotFoundException;
import com.infy.scm360.order.domain.*;
import com.infy.scm360.order.messaging.OrderEventPublisher;
import com.infy.scm360.order.repository.BackorderRepository;
import com.infy.scm360.order.repository.CustomerOrderRepository;
import com.infy.scm360.order.repository.OrderStatusHistoryRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.Instant;
import java.time.LocalDate;
import java.util.Map;

@Service
@Transactional
public class FulfilmentSagaCoordinator {

    private static final Logger log = LoggerFactory.getLogger(FulfilmentSagaCoordinator.class);

    private final CustomerOrderRepository orderRepository;
    private final OrderStatusHistoryRepository historyRepository;
    private final BackorderRepository backorderRepository;
    private final OrderEventPublisher eventPublisher;

    public FulfilmentSagaCoordinator(
            CustomerOrderRepository orderRepository,
            OrderStatusHistoryRepository historyRepository,
            BackorderRepository backorderRepository,
            OrderEventPublisher eventPublisher
    ) {
        this.orderRepository = orderRepository;
        this.historyRepository = historyRepository;
        this.backorderRepository = backorderRepository;
        this.eventPublisher = eventPublisher;
    }

    public void handleStockReserved(String orderId) {
        CustomerOrder order = orderRepository.findById(orderId).orElse(null);
        if (order == null || order.getStatus() == OrderStatus.CANCELLED) {
            log.warn("Received stock.reserved for invalid or cancelled order: {}", orderId);
            return;
        }

        OrderStatus oldStatus = order.getStatus();
        order.setStatus(OrderStatus.CONFIRMED);
        order.setConfirmedAt(Instant.now());
        orderRepository.save(order);

        historyRepository.save(new OrderStatusHistory(
                order.getId(), oldStatus.name(), OrderStatus.CONFIRMED.name(),
                "FULFILMENT_SAGA", "Stock reservation confirmed. Order confirmed."
        ));

        eventPublisher.publishOrderConfirmed(order.getId(), order.getOrderNumber(), Map.of(
                "orderId", order.getId(),
                "orderNumber", order.getOrderNumber(),
                "status", "CONFIRMED",
                "warehouseId", order.getOriginWarehouseId()
        ));
    }

    public void handleReservationFailed(String orderId, String productId, BigDecimal quantity, String reason) {
        CustomerOrder order = orderRepository.findById(orderId).orElse(null);
        if (order == null || order.getStatus() == OrderStatus.CANCELLED) {
            return;
        }

        OrderStatus oldStatus = order.getStatus();
        order.setStatus(OrderStatus.BACKORDERED);
        orderRepository.save(order);

        backorderRepository.save(new Backorder(
                order.getId(),
                order.getOrderNumber(),
                productId != null ? productId : "UNKNOWN",
                "BACKORDER-SKU",
                quantity != null ? quantity : BigDecimal.ONE
        ));

        historyRepository.save(new OrderStatusHistory(
                order.getId(), oldStatus.name(), OrderStatus.BACKORDERED.name(),
                "FULFILMENT_SAGA", "Stock reservation failed (" + reason + "). Moved to BACKORDERED."
        ));

        eventPublisher.publishOrderBackordered(order.getId(), order.getOrderNumber(), Map.of(
                "orderId", order.getId(),
                "orderNumber", order.getOrderNumber(),
                "status", "BACKORDERED",
                "reason", reason != null ? reason : "Insufficient inventory"
        ));
    }

    public void handleOrderPicked(String orderId) {
        CustomerOrder order = orderRepository.findById(orderId).orElse(null);
        if (order == null || order.getStatus() == OrderStatus.CANCELLED) return;

        OrderStatus oldStatus = order.getStatus();
        order.setStatus(OrderStatus.PICKING);
        orderRepository.save(order);

        historyRepository.save(new OrderStatusHistory(
                order.getId(), oldStatus.name(), OrderStatus.PICKING.name(),
                "WAREHOUSE_SERVICE", "Warehouse goods picked for fulfilment."
        ));
    }

    public void handleOrderPacked(String orderId) {
        CustomerOrder order = orderRepository.findById(orderId).orElse(null);
        if (order == null || order.getStatus() == OrderStatus.CANCELLED) return;

        OrderStatus oldStatus = order.getStatus();
        order.setStatus(OrderStatus.PACKED);
        orderRepository.save(order);

        historyRepository.save(new OrderStatusHistory(
                order.getId(), oldStatus.name(), OrderStatus.PACKED.name(),
                "WAREHOUSE_SERVICE", "Order packed and staged for shipment."
        ));
    }

    public void handleShipmentDispatched(String orderId, String carrierId, String trackingNumber, LocalDate estimatedDeliveryDate) {
        CustomerOrder order = orderRepository.findById(orderId).orElse(null);
        if (order == null || order.getStatus() == OrderStatus.CANCELLED) return;

        OrderStatus oldStatus = order.getStatus();
        order.setStatus(OrderStatus.SHIPPED);
        order.setShippedAt(Instant.now());
        if (carrierId != null) order.setCarrierId(carrierId);
        if (trackingNumber != null) order.setTrackingNumber(trackingNumber);
        if (estimatedDeliveryDate != null) order.setEstimatedDeliveryDate(estimatedDeliveryDate);
        orderRepository.save(order);

        historyRepository.save(new OrderStatusHistory(
                order.getId(), oldStatus.name(), OrderStatus.SHIPPED.name(),
                "TRANSPORT_SERVICE", "Shipment dispatched. Tracking: " + trackingNumber
        ));

        eventPublisher.publishOrderShipped(order.getId(), order.getOrderNumber(), Map.of(
                "orderId", order.getId(),
                "orderNumber", order.getOrderNumber(),
                "carrierId", carrierId != null ? carrierId : "",
                "trackingNumber", trackingNumber != null ? trackingNumber : "",
                "status", "SHIPPED"
        ));
    }

    public void handleShipmentDelivered(String orderId) {
        CustomerOrder order = orderRepository.findById(orderId).orElse(null);
        if (order == null || order.getStatus() == OrderStatus.CANCELLED) return;

        OrderStatus oldStatus = order.getStatus();
        order.setStatus(OrderStatus.DELIVERED);
        order.setDeliveredAt(Instant.now());
        orderRepository.save(order);

        historyRepository.save(new OrderStatusHistory(
                order.getId(), oldStatus.name(), OrderStatus.DELIVERED.name(),
                "TRANSPORT_SERVICE", "Proof of delivery confirmed. Order complete."
        ));

        eventPublisher.publishOrderDelivered(order.getId(), order.getOrderNumber(), Map.of(
                "orderId", order.getId(),
                "orderNumber", order.getOrderNumber(),
                "status", "DELIVERED"
        ));
    }
}
