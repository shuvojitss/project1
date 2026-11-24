package com.infy.scm360.order;

import com.infy.scm360.common.security.ScmUserPrincipal;
import com.infy.scm360.order.domain.OrderStatus;
import com.infy.scm360.order.dto.*;
import com.infy.scm360.order.service.FulfilmentSagaCoordinator;
import com.infy.scm360.order.service.OrderService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@ActiveProfiles("test")
@Transactional
class OrderServiceApplicationTests {

    @Autowired
    private OrderService orderService;

    @Autowired
    private FulfilmentSagaCoordinator sagaCoordinator;

    @Test
    @DisplayName("Order creation and full fulfilment saga lifecycle")
    void testOrderFulfilmentLifecycle() {
        ScmUserPrincipal salesUser = new ScmUserPrincipal(
                "00000000-0000-0000-0000-000000000007",
                "sales1",
                List.of("SALES_COORDINATOR"),
                List.of(),
                null
        );

        CreateOrderRequest request = new CreateOrderRequest(
                "c1000000-0000-0000-0000-000000000001",
                "Acme Manufacturing Corp",
                "b0000000-0000-0000-0000-000000000001",
                "Central Distribution Hub",
                "1200 Industrial Pkwy, Chicago, IL 60601",
                "EXPRESS",
                "USD",
                List.of(new OrderLineDto(
                        null,
                        "c0000000-0000-0000-0000-000000000001",
                        "SKU-RAW-001",
                        "Industrial Steel Sheet",
                        BigDecimal.valueOf(10),
                        BigDecimal.valueOf(185.00),
                        null
                ))
        );

        // 1. Create order -> initial state RESERVING
        OrderResponse created = orderService.createOrder(request, salesUser);
        assertNotNull(created.id());
        assertEquals(OrderStatus.RESERVING, created.status());
        assertEquals(0, BigDecimal.valueOf(1850.00).compareTo(created.totalAmount()));

        // 2. Saga: Inventory stock reserved -> Order CONFIRMED
        sagaCoordinator.handleStockReserved(created.id());
        OrderResponse confirmed = orderService.getOrder(created.id());
        assertEquals(OrderStatus.CONFIRMED, confirmed.status());
        assertNotNull(confirmed.confirmedAt());

        // 3. Saga: Warehouse picked -> Order PICKING
        sagaCoordinator.handleOrderPicked(created.id());
        OrderResponse picking = orderService.getOrder(created.id());
        assertEquals(OrderStatus.PICKING, picking.status());

        // 4. Saga: Warehouse packed -> Order PACKED
        sagaCoordinator.handleOrderPacked(created.id());
        OrderResponse packed = orderService.getOrder(created.id());
        assertEquals(OrderStatus.PACKED, packed.status());

        // 5. Saga: Transport dispatched -> Order SHIPPED
        sagaCoordinator.handleShipmentDispatched(
                created.id(),
                "car00000-0000-0000-0000-000000000001",
                "TRACK-123456",
                LocalDate.now().plusDays(2)
        );
        OrderResponse shipped = orderService.getOrder(created.id());
        assertEquals(OrderStatus.SHIPPED, shipped.status());
        assertEquals("TRACK-123456", shipped.trackingNumber());
        assertNotNull(shipped.shippedAt());

        // 6. Saga: Transport delivered -> Order DELIVERED
        sagaCoordinator.handleShipmentDelivered(created.id());
        OrderResponse delivered = orderService.getOrder(created.id());
        assertEquals(OrderStatus.DELIVERED, delivered.status());
        assertNotNull(delivered.deliveredAt());

        // Check timeline events
        List<OrderStatusHistoryDto> timeline = orderService.getTimeline(created.id());
        assertTrue(timeline.size() >= 5);

        // Delivery estimate check
        DeliveryEstimateResponse estimate = orderService.estimateDelivery(created.id());
        assertEquals("EXPRESS", estimate.shippingMethod());
        assertEquals(2, estimate.estimatedTransitDays());
    }

    @Test
    @DisplayName("Saga: Reservation failed moves order to BACKORDERED")
    void testBackorderSaga() {
        ScmUserPrincipal salesUser = new ScmUserPrincipal(
                "00000000-0000-0000-0000-000000000007",
                "sales1",
                List.of("SALES_COORDINATOR"),
                List.of(),
                null
        );

        CreateOrderRequest request = new CreateOrderRequest(
                "c1000000-0000-0000-0000-000000000002",
                "Nexus Tech",
                "b0000000-0000-0000-0000-000000000002",
                "West Coast Fulfillment",
                "450 Silicon Blvd",
                "STANDARD",
                "USD",
                List.of(new OrderLineDto(
                        null,
                        "c0000000-0000-0000-0000-000000000002",
                        "SKU-MCH-001",
                        "Press Machine",
                        BigDecimal.valueOf(5),
                        BigDecimal.valueOf(2500.00),
                        null
                ))
        );

        OrderResponse order = orderService.createOrder(request, salesUser);
        sagaCoordinator.handleReservationFailed(order.id(), "c0000000-0000-0000-0000-000000000002", BigDecimal.valueOf(5), "Insufficient stock in warehouse");

        OrderResponse backordered = orderService.getOrder(order.id());
        assertEquals(OrderStatus.BACKORDERED, backordered.status());
    }

    @Test
    @DisplayName("Order cancellation workflow")
    void testCancelOrder() {
        ScmUserPrincipal salesUser = new ScmUserPrincipal(
                "00000000-0000-0000-0000-000000000007",
                "sales1",
                List.of("SALES_COORDINATOR"),
                List.of(),
                null
        );

        CreateOrderRequest request = new CreateOrderRequest(
                "c1000000-0000-0000-0000-000000000003",
                "Vanguard Aerospace",
                "b0000000-0000-0000-0000-000000000001",
                "Central Distribution Hub",
                "890 Hangar Way",
                "STANDARD",
                "USD",
                List.of(new OrderLineDto(
                        null,
                        "c0000000-0000-0000-0000-000000000001",
                        "SKU-RAW-001",
                        "Steel Sheet",
                        BigDecimal.valueOf(2),
                        BigDecimal.valueOf(185.00),
                        null
                ))
        );

        OrderResponse order = orderService.createOrder(request, salesUser);
        OrderResponse cancelled = orderService.cancelOrder(order.id(), new CancelOrderRequest("Customer requested refund"), salesUser);
        assertEquals(OrderStatus.CANCELLED, cancelled.status());
        assertNotNull(cancelled.cancelledAt());
        assertEquals("Customer requested refund", cancelled.cancelReason());
    }
}
