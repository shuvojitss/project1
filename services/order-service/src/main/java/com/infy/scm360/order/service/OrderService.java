package com.infy.scm360.order.service;

import com.infy.scm360.common.exception.ResourceNotFoundException;
import com.infy.scm360.common.exception.StateConflictException;
import com.infy.scm360.common.security.ScmUserPrincipal;
import com.infy.scm360.order.domain.*;
import com.infy.scm360.order.dto.*;
import com.infy.scm360.order.messaging.OrderEventPublisher;
import com.infy.scm360.order.repository.CustomerOrderRepository;
import com.infy.scm360.order.repository.OrderStatusHistoryRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ThreadLocalRandom;
import java.util.stream.Collectors;

@Service
@Transactional
public class OrderService {

    private final CustomerOrderRepository orderRepository;
    private final OrderStatusHistoryRepository historyRepository;
    private final OrderEventPublisher eventPublisher;

    public OrderService(
            CustomerOrderRepository orderRepository,
            OrderStatusHistoryRepository historyRepository,
            OrderEventPublisher eventPublisher
    ) {
        this.orderRepository = orderRepository;
        this.historyRepository = historyRepository;
        this.eventPublisher = eventPublisher;
    }

    public OrderResponse createOrder(CreateOrderRequest request, ScmUserPrincipal user) {
        CustomerOrder order = new CustomerOrder();
        order.setOrderNumber(generateOrderNumber());
        order.setCustomerId(request.customerId());
        order.setCustomerName(request.customerName());
        order.setOriginWarehouseId(request.originWarehouseId());
        order.setOriginWarehouseName(request.originWarehouseName());
        order.setShippingAddress(request.shippingAddress());
        if (request.shippingMethod() != null && !request.shippingMethod().isBlank()) {
            order.setShippingMethod(request.shippingMethod());
        }
        if (request.currency() != null && !request.currency().isBlank()) {
            order.setCurrency(request.currency());
        }
        order.setCreatedBy(user != null ? user.getUserId() : "SYSTEM");
        order.setCreatedByName(user != null ? user.getUsername() : "SYSTEM");
        order.setStatus(OrderStatus.RESERVING);

        // Estimate initial delivery date based on shipping method
        int transitDays = getTransitDays(order.getShippingMethod());
        order.setEstimatedDeliveryDate(LocalDate.now().plusDays(transitDays + 1L));

        for (OrderLineDto lineDto : request.lines()) {
            OrderLine line = new OrderLine(
                    lineDto.productId(),
                    lineDto.productSku(),
                    lineDto.productName(),
                    lineDto.quantity(),
                    lineDto.unitPrice()
            );
            order.addLine(line);
        }

        CustomerOrder saved = orderRepository.save(order);
        historyRepository.save(new OrderStatusHistory(
                saved.getId(), "CREATED", "RESERVING",
                user != null ? user.getUsername() : "SYSTEM", "Order created. Initiated inventory reservation saga."
        ));

        // Publish order.placed to kick off reservation saga
        List<Map<String, Object>> lineItems = saved.getLines().stream()
                .map(l -> Map.<String, Object>of(
                        "productId", l.getProductId(),
                        "productSku", l.getProductSku(),
                        "quantity", l.getQuantity()
                )).collect(Collectors.toList());

        eventPublisher.publishOrderPlaced(saved.getId(), saved.getOrderNumber(), Map.of(
                "orderId", saved.getId(),
                "orderNumber", saved.getOrderNumber(),
                "warehouseId", saved.getOriginWarehouseId(),
                "items", lineItems,
                "status", "RESERVING"
        ));

        return toResponse(saved);
    }

    public OrderResponse cancelOrder(String id, CancelOrderRequest request, ScmUserPrincipal user) {
        CustomerOrder order = orderRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Customer order not found with id: " + id));

        if (order.getStatus() == OrderStatus.SHIPPED || order.getStatus() == OrderStatus.DELIVERED || order.getStatus() == OrderStatus.CANCELLED) {
            throw new StateConflictException("Cannot cancel order in status: " + order.getStatus());
        }

        OrderStatus oldStatus = order.getStatus();
        order.setStatus(OrderStatus.CANCELLED);
        order.setCancelledAt(Instant.now());
        order.setCancelReason(request != null && request.reason() != null ? request.reason() : "Cancelled by user");

        CustomerOrder saved = orderRepository.save(order);
        historyRepository.save(new OrderStatusHistory(
                saved.getId(), oldStatus.name(), OrderStatus.CANCELLED.name(),
                user != null ? user.getUsername() : "SYSTEM",
                "Order cancelled: " + order.getCancelReason()
        ));

        // Trigger reservation release
        eventPublisher.publishOrderCancelled(saved.getId(), saved.getOrderNumber(), Map.of(
                "orderId", saved.getId(),
                "orderNumber", saved.getOrderNumber(),
                "warehouseId", saved.getOriginWarehouseId(),
                "status", "CANCELLED",
                "reason", order.getCancelReason()
        ));

        return toResponse(saved);
    }

    @Transactional(readOnly = true)
    public OrderResponse getOrder(String id) {
        return orderRepository.findById(id)
                .map(this::toResponse)
                .orElseThrow(() -> new ResourceNotFoundException("Customer order not found with id: " + id));
    }

    @Transactional(readOnly = true)
    public Page<OrderResponse> listOrders(OrderStatus status, String customerId, String warehouseId, Pageable pageable) {
        if (status != null) {
            return orderRepository.findByStatus(status, pageable).map(this::toResponse);
        }
        if (customerId != null && !customerId.isBlank()) {
            return orderRepository.findByCustomerId(customerId, pageable).map(this::toResponse);
        }
        if (warehouseId != null && !warehouseId.isBlank()) {
            return orderRepository.findByOriginWarehouseId(warehouseId, pageable).map(this::toResponse);
        }
        return orderRepository.findAll(pageable).map(this::toResponse);
    }

    @Transactional(readOnly = true)
    public List<OrderStatusHistoryDto> getTimeline(String orderId) {
        return historyRepository.findByOrderIdOrderByChangedAtAsc(orderId).stream()
                .map(h -> new OrderStatusHistoryDto(
                        h.getId(),
                        h.getOrderId(),
                        h.getFromStatus(),
                        h.getToStatus(),
                        h.getChangedBy(),
                        h.getNotes(),
                        h.getChangedAt()
                )).collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public DeliveryEstimateResponse estimateDelivery(String orderId) {
        CustomerOrder order = orderRepository.findById(orderId)
                .orElseThrow(() -> new ResourceNotFoundException("Customer order not found with id: " + orderId));

        int transitDays = getTransitDays(order.getShippingMethod());
        LocalDate estimate = order.getEstimatedDeliveryDate() != null
                ? order.getEstimatedDeliveryDate()
                : LocalDate.now().plusDays(transitDays + 1L);

        return new DeliveryEstimateResponse(
                order.getId(),
                order.getOrderNumber(),
                order.getShippingMethod(),
                transitDays,
                estimate
        );
    }

    private int getTransitDays(String shippingMethod) {
        if (shippingMethod == null) return 5;
        return switch (shippingMethod.toUpperCase()) {
            case "EXPRESS" -> 2;
            case "OVERNIGHT" -> 1;
            case "FREIGHT" -> 7;
            default -> 5;
        };
    }

    private String generateOrderNumber() {
        int year = LocalDate.now().getYear();
        int rand = ThreadLocalRandom.current().nextInt(1000, 9999);
        return String.format("ORD-%d-%04d", year, rand);
    }

    public OrderResponse toResponse(CustomerOrder order) {
        List<OrderLineDto> lines = order.getLines().stream()
                .map(l -> new OrderLineDto(
                        l.getId(),
                        l.getProductId(),
                        l.getProductSku(),
                        l.getProductName(),
                        l.getQuantity(),
                        l.getUnitPrice(),
                        l.getLineTotal()
                )).collect(Collectors.toList());

        return new OrderResponse(
                order.getId(),
                order.getOrderNumber(),
                order.getCustomerId(),
                order.getCustomerName(),
                order.getStatus(),
                order.getOriginWarehouseId(),
                order.getOriginWarehouseName(),
                order.getShippingAddress(),
                order.getShippingMethod(),
                order.getTotalAmount(),
                order.getCurrency(),
                order.getCreatedBy(),
                order.getCreatedByName(),
                order.getConfirmedAt(),
                order.getShippedAt(),
                order.getDeliveredAt(),
                order.getCancelledAt(),
                order.getCancelReason(),
                order.getEstimatedDeliveryDate(),
                order.getTrackingNumber(),
                order.getCarrierId(),
                order.getCreatedAt(),
                order.getUpdatedAt(),
                lines
        );
    }
}
