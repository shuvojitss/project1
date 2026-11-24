package com.infy.scm360.order.controller;

import com.infy.scm360.common.dto.PageResponse;
import com.infy.scm360.common.security.ScmUserPrincipal;
import com.infy.scm360.common.security.SecurityUtils;
import com.infy.scm360.order.domain.OrderStatus;
import com.infy.scm360.order.dto.*;
import com.infy.scm360.order.service.OrderService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/orders")
@Tag(name = "Customer Orders", description = "Order management, fulfilment state machine, timeline, and delivery estimation")
public class OrderController {

    private final OrderService orderService;

    public OrderController(OrderService orderService) {
        this.orderService = orderService;
    }

    @PostMapping
    @PreAuthorize("hasAnyRole('SALES_COORDINATOR', 'ADMIN')")
    @Operation(summary = "Create customer order and initiate reservation saga")
    public ResponseEntity<OrderResponse> createOrder(@Valid @RequestBody CreateOrderRequest request) {
        ScmUserPrincipal user = SecurityUtils.getCurrentUser().orElse(null);
        return ResponseEntity.status(HttpStatus.CREATED).body(orderService.createOrder(request, user));
    }

    @GetMapping
    @PreAuthorize("isAuthenticated()")
    @Operation(summary = "List customer orders with filtering and pagination")
    public ResponseEntity<PageResponse<OrderResponse>> listOrders(
            @RequestParam(required = false) OrderStatus status,
            @RequestParam(required = false) String customerId,
            @RequestParam(required = false) String warehouseId,
            Pageable pageable
    ) {
        Page<OrderResponse> page = orderService.listOrders(status, customerId, warehouseId, pageable);
        return ResponseEntity.ok(PageResponse.from(page));
    }

    @GetMapping("/{id}")
    @PreAuthorize("isAuthenticated()")
    @Operation(summary = "Get customer order by id")
    public ResponseEntity<OrderResponse> getOrder(@PathVariable String id) {
        return ResponseEntity.ok(orderService.getOrder(id));
    }

    @PostMapping("/{id}/cancel")
    @PreAuthorize("hasAnyRole('SALES_COORDINATOR', 'ADMIN')")
    @Operation(summary = "Cancel customer order and release reservations")
    public ResponseEntity<OrderResponse> cancelOrder(
            @PathVariable String id,
            @RequestBody(required = false) CancelOrderRequest request
    ) {
        ScmUserPrincipal user = SecurityUtils.getCurrentUser().orElse(null);
        return ResponseEntity.ok(orderService.cancelOrder(id, request, user));
    }

    @GetMapping("/{id}/timeline")
    @PreAuthorize("isAuthenticated()")
    @Operation(summary = "Get order fulfilment lifecycle event timeline")
    public ResponseEntity<List<OrderStatusHistoryDto>> getTimeline(@PathVariable String id) {
        return ResponseEntity.ok(orderService.getTimeline(id));
    }

    @GetMapping("/{id}/delivery-estimate")
    @PreAuthorize("isAuthenticated()")
    @Operation(summary = "Calculate dynamic delivery estimate and transit duration")
    public ResponseEntity<DeliveryEstimateResponse> getDeliveryEstimate(@PathVariable String id) {
        return ResponseEntity.ok(orderService.estimateDelivery(id));
    }
}
