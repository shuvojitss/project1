package com.infy.scm360.inventory;

import com.infy.scm360.inventory.domain.StockLevel;
import com.infy.scm360.inventory.dto.ReserveStockRequest;
import com.infy.scm360.inventory.repository.StockLevelRepository;
import com.infy.scm360.inventory.service.InventoryService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicInteger;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@ActiveProfiles("test")
class InventoryConcurrencyTests {

    @Autowired
    private InventoryService inventoryService;

    @Autowired
    private StockLevelRepository stockLevelRepository;

    @Test
    void testParallelReservationsNoOversell() throws Exception {
        String productId = "prod-concurrency-test";
        String warehouseId = "wh-concurrency-test";

        // Initial stock: 50 on hand, 0 reserved
        StockLevel stock = new StockLevel(
            UUID.randomUUID().toString(),
            productId,
            warehouseId,
            null,
            new BigDecimal("50.000"),
            BigDecimal.ZERO
        );
        stockLevelRepository.saveAndFlush(stock);

        int threads = 100;
        ExecutorService executor = Executors.newFixedThreadPool(20);
        CountDownLatch startLatch = new CountDownLatch(1);
        CountDownLatch finishLatch = new CountDownLatch(threads);

        AtomicInteger successCount = new AtomicInteger(0);
        AtomicInteger failCount = new AtomicInteger(0);

        for (int i = 0; i < threads; i++) {
            final String orderId = "order-concurrent-" + i;
            executor.submit(() -> {
                try {
                    startLatch.await();
                    ReserveStockRequest req = new ReserveStockRequest(
                        orderId,
                        warehouseId,
                        List.of(new ReserveStockRequest.LineItem(productId, BigDecimal.ONE))
                    );
                    boolean success = inventoryService.reserveStock(req);
                    if (success) {
                        successCount.incrementAndGet();
                    } else {
                        failCount.incrementAndGet();
                    }
                } catch (Exception e) {
                    failCount.incrementAndGet();
                } finally {
                    finishLatch.countDown();
                }
            });
        }

        // Release all threads simultaneously
        startLatch.countDown();
        assertTrue(finishLatch.await(30, TimeUnit.SECONDS), "Concurrent reservations should finish in 30s");
        executor.shutdown();

        // Verification: Exactly 50 successes, 50 failures
        assertEquals(50, successCount.get(), "Exactly 50 reservations should have succeeded");
        assertEquals(50, failCount.get(), "Exactly 50 reservations should have failed due to stock exhaustion");

        // Verify stock levels in database
        List<StockLevel> levels = stockLevelRepository.findByProductIdAndWarehouseId(productId, warehouseId);
        assertFalse(levels.isEmpty());
        StockLevel finalStock = levels.get(0);
        assertEquals(new BigDecimal("50.000"), finalStock.getOnHand());
        assertEquals(new BigDecimal("50.000"), finalStock.getReserved());
        assertEquals(0, BigDecimal.ZERO.compareTo(finalStock.getAvailable()), "Available stock must be exactly 0, never negative!");
    }
}
