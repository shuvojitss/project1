package com.infy.scm360.inventory.service;

import com.infy.scm360.common.dto.PageResponse;
import com.infy.scm360.common.event.EventEnvelope;
import com.infy.scm360.common.event.EventTypes;
import com.infy.scm360.common.exception.InsufficientStockException;
import com.infy.scm360.common.outbox.OutboxService;
import com.infy.scm360.inventory.domain.*;
import com.infy.scm360.inventory.dto.*;
import com.infy.scm360.inventory.repository.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.*;

@Service
public class InventoryService {

    private static final Logger log = LoggerFactory.getLogger(InventoryService.class);

    private final StockLevelRepository stockLevelRepository;
    private final StockLedgerRepository stockLedgerRepository;
    private final ReservationRepository reservationRepository;
    private final ReorderRuleRepository reorderRuleRepository;
    private final OutboxService outboxService;

    public InventoryService(
            StockLevelRepository stockLevelRepository,
            StockLedgerRepository stockLedgerRepository,
            ReservationRepository reservationRepository,
            ReorderRuleRepository reorderRuleRepository,
            OutboxService outboxService) {
        this.stockLevelRepository = stockLevelRepository;
        this.stockLedgerRepository = stockLedgerRepository;
        this.reservationRepository = reservationRepository;
        this.reorderRuleRepository = reorderRuleRepository;
        this.outboxService = outboxService;
    }

    @Transactional(readOnly = true)
    public PageResponse<StockLevelDto> listStock(String warehouseId, Pageable pageable) {
        Page<StockLevel> page = warehouseId != null && !warehouseId.isBlank()
                ? stockLevelRepository.findByWarehouseId(warehouseId, pageable)
                : stockLevelRepository.findAll(pageable);

        return PageResponse.from(page.map(this::mapToStockDto));
    }

    @Transactional(readOnly = true)
    public PageResponse<StockLedgerDto> listLedger(String productId, String warehouseId, Pageable pageable) {
        Page<StockLedger> page = stockLedgerRepository.findByProductIdAndWarehouseIdOrderByOccurredAtDesc(productId, warehouseId, pageable);
        return PageResponse.from(page.map(this::mapToLedgerDto));
    }

    /**
     * Atomically reserves stock for all lines in the order.
     * If ANY line cannot be reserved, all partial reservations made in this transaction are released,
     * and an inventory.reservation.failed event is emitted.
     */
    @Transactional
    public boolean reserveStock(ReserveStockRequest req) {
        Instant now = Instant.now();
        List<ReserveStockRequest.LineItem> reservedSoFar = new ArrayList<>();

        for (var line : req.lines()) {
            int updated = stockLevelRepository.reserveStock(line.productId(), req.warehouseId(), line.quantity(), now);
            if (updated <= 0) {
                // Insufficient stock! Roll back previous lines
                for (var prev : reservedSoFar) {
                    stockLevelRepository.releaseReservation(prev.productId(), req.warehouseId(), prev.quantity(), now);
                }

                log.warn("Stock reservation failed for order [{}]: insufficient stock for SKU [{}] at WH [{}]",
                        req.orderId(), line.productId(), req.warehouseId());

                var payload = Map.of(
                    "orderId", req.orderId(),
                    "warehouseId", req.warehouseId(),
                    "failedProductId", line.productId(),
                    "requestedQuantity", line.quantity()
                );
                EventEnvelope<?> env = EventEnvelope.of(EventTypes.INVENTORY_RESERVATION_FAILED, "inventory-service", payload);
                outboxService.publish(EventTypes.EXCHANGE_INVENTORY, EventTypes.INVENTORY_RESERVATION_FAILED, "Order", req.orderId(), env);
                return false;
            }
            reservedSoFar.add(line);
        }

        // All lines reserved successfully! Write reservations and ledger rows
        Instant expiresAt = now.plus(24, ChronoUnit.HOURS);
        for (var line : req.lines()) {
            Reservation res = new Reservation(
                UUID.randomUUID().toString(),
                req.orderId(),
                line.productId(),
                req.warehouseId(),
                line.quantity(),
                expiresAt
            );
            reservationRepository.save(res);

            StockLedger ledger = new StockLedger(
                UUID.randomUUID().toString(),
                line.productId(),
                req.warehouseId(),
                "RESERVE",
                line.quantity(),
                "ORDER",
                req.orderId(),
                now
            );
            stockLedgerRepository.save(ledger);
        }

        var payload = Map.of(
            "orderId", req.orderId(),
            "warehouseId", req.warehouseId(),
            "status", "RESERVED"
        );
        EventEnvelope<?> env = EventEnvelope.of(EventTypes.INVENTORY_STOCK_RESERVED, "inventory-service", payload);
        outboxService.publish(EventTypes.EXCHANGE_INVENTORY, EventTypes.INVENTORY_STOCK_RESERVED, "Order", req.orderId(), env);

        return true;
    }

    @Transactional
    public void commitReservationsForOrder(String orderId) {
        List<Reservation> reservations = reservationRepository.findByOrderId(orderId);
        Instant now = Instant.now();

        for (Reservation res : reservations) {
            if ("RESERVED".equals(res.getStatus())) {
                stockLevelRepository.commitReservation(res.getProductId(), res.getWarehouseId(), res.getQuantity(), now);
                res.setStatus("COMMITTED");
                reservationRepository.save(res);

                StockLedger ledger = new StockLedger(
                    UUID.randomUUID().toString(),
                    res.getProductId(),
                    res.getWarehouseId(),
                    "ISSUE",
                    res.getQuantity().negate(),
                    "ORDER_PICKED",
                    orderId,
                    now
                );
                stockLedgerRepository.save(ledger);
            }
        }
    }

    @Transactional
    public void releaseReservationsForOrder(String orderId) {
        List<Reservation> reservations = reservationRepository.findByOrderId(orderId);
        Instant now = Instant.now();

        for (Reservation res : reservations) {
            if ("RESERVED".equals(res.getStatus())) {
                stockLevelRepository.releaseReservation(res.getProductId(), res.getWarehouseId(), res.getQuantity(), now);
                res.setStatus("RELEASED");
                reservationRepository.save(res);

                StockLedger ledger = new StockLedger(
                    UUID.randomUUID().toString(),
                    res.getProductId(),
                    res.getWarehouseId(),
                    "RELEASE",
                    res.getQuantity().negate(),
                    "ORDER_CANCELLED",
                    orderId,
                    now
                );
                stockLedgerRepository.save(ledger);
            }
        }

        var payload = Map.of("orderId", orderId, "status", "RELEASED");
        EventEnvelope<?> env = EventEnvelope.of(EventTypes.INVENTORY_RESERVATION_RELEASED, "inventory-service", payload);
        outboxService.publish(EventTypes.EXCHANGE_INVENTORY, EventTypes.INVENTORY_RESERVATION_RELEASED, "Order", orderId, env);
    }

    @Transactional
    public void receiveGoods(String productId, String warehouseId, BigDecimal quantity, String referenceId) {
        Instant now = Instant.now();
        int updated = stockLevelRepository.addOnHand(productId, warehouseId, quantity, now);
        if (updated <= 0) {
            StockLevel sl = new StockLevel(
                UUID.randomUUID().toString(),
                productId,
                warehouseId,
                null,
                quantity,
                BigDecimal.ZERO
            );
            stockLevelRepository.save(sl);
        }

        StockLedger ledger = new StockLedger(
            UUID.randomUUID().toString(),
            productId,
            warehouseId,
            "RECEIPT",
            quantity,
            "GOODS_RECEIPT",
            referenceId,
            now
        );
        stockLedgerRepository.save(ledger);

        var payload = Map.of(
            "productId", productId,
            "warehouseId", warehouseId,
            "quantity", quantity,
            "action", "RECEIPT"
        );
        EventEnvelope<?> env = EventEnvelope.of(EventTypes.INVENTORY_STOCK_CHANGED, "inventory-service", payload);
        outboxService.publish(EventTypes.EXCHANGE_INVENTORY, EventTypes.INVENTORY_STOCK_CHANGED, "StockLevel", productId, env);

        // Check low stock
        checkReorderThreshold(productId, warehouseId);
    }

    private void checkReorderThreshold(String productId, String warehouseId) {
        reorderRuleRepository.findByProductIdAndWarehouseId(productId, warehouseId).ifPresent(rule -> {
            var levels = stockLevelRepository.findByProductIdAndWarehouseId(productId, warehouseId);
            BigDecimal totalAvailable = levels.stream()
                    .map(StockLevel::getAvailable)
                    .reduce(BigDecimal.ZERO, BigDecimal::add);

            if (totalAvailable.compareTo(rule.getReorderPoint()) <= 0) {
                var payload = Map.of(
                    "productId", productId,
                    "warehouseId", warehouseId,
                    "availableStock", totalAvailable,
                    "reorderPoint", rule.getReorderPoint(),
                    "preferredSupplierId", rule.getPreferredSupplierId() != null ? rule.getPreferredSupplierId() : ""
                );
                EventEnvelope<?> env = EventEnvelope.of(EventTypes.INVENTORY_STOCK_LOW, "inventory-service", payload);
                outboxService.publish(EventTypes.EXCHANGE_INVENTORY, EventTypes.INVENTORY_STOCK_LOW, "ReorderRule", rule.getId(), env);
            }
        });
    }

    private StockLevelDto mapToStockDto(StockLevel s) {
        return new StockLevelDto(
            s.getId(),
            s.getProductId(),
            s.getWarehouseId(),
            s.getBinId(),
            s.getOnHand(),
            s.getReserved(),
            s.getAvailable(),
            s.getUpdatedAt()
        );
    }

    private StockLedgerDto mapToLedgerDto(StockLedger l) {
        return new StockLedgerDto(
            l.getId(),
            l.getProductId(),
            l.getWarehouseId(),
            l.getMovementType(),
            l.getQuantityDelta(),
            l.getReferenceType(),
            l.getReferenceId(),
            l.getOccurredAt()
        );
    }
}
