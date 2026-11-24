package com.infy.scm360.warehouse;

import com.infy.scm360.common.outbox.OutboxRepository;
import com.infy.scm360.warehouse.domain.InboundReceipt;
import com.infy.scm360.warehouse.domain.Warehouse;
import com.infy.scm360.warehouse.dto.ReceiveGoodsRequest;
import com.infy.scm360.warehouse.repository.InboundReceiptRepository;
import com.infy.scm360.warehouse.repository.WarehouseRepository;
import com.infy.scm360.warehouse.service.WarehouseService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@ActiveProfiles("test")
class WarehouseServiceApplicationTests {

    @Autowired
    private WarehouseService warehouseService;

    @Autowired
    private WarehouseRepository warehouseRepository;

    @Autowired
    private InboundReceiptRepository receiptRepository;

    @Autowired
    private OutboxRepository outboxRepository;

    @Test
    void testReceiveGoodsAndPublishEvent() {
        Warehouse wh = new Warehouse();
        wh.setId("wh-test-01");
        wh.setCode("WH-TEST-01");
        wh.setName("Test Warehouse");
        wh.setRegion("SOUTH");
        wh.setAddress("Test Address");
        wh.setCapacity(5000);
        wh.setCreatedAt(Instant.now());
        wh.setCreatedBy("SYSTEM");
        wh.setUpdatedAt(Instant.now());
        wh.setUpdatedBy("SYSTEM");
        warehouseRepository.save(wh);

        InboundReceipt receipt = new InboundReceipt();
        receipt.setId(UUID.randomUUID().toString());
        receipt.setReceiptNumber("REC-TEST-001");
        receipt.setPoId("po-test-100");
        receipt.setSupplierId("sup-test-200");
        receipt.setWarehouse(wh);
        receipt.setStatus("EXPECTED");
        receipt.setCreatedAt(Instant.now());
        receiptRepository.save(receipt);

        ReceiveGoodsRequest req = new ReceiveGoodsRequest(
            receipt.getId(),
            List.of(new ReceiveGoodsRequest.ReceiveLine("prod-001", new BigDecimal("100.000"), BigDecimal.ZERO, BigDecimal.ZERO, "bin-01"))
        );

        warehouseService.receiveGoods(req);

        InboundReceipt updated = receiptRepository.findById(receipt.getId()).orElseThrow();
        assertEquals("COMPLETED", updated.getStatus());

        var outboxEvents = outboxRepository.findAll();
        assertFalse(outboxEvents.isEmpty());
        assertTrue(outboxEvents.stream().anyMatch(e -> e.getRoutingKey().equals("warehouse.goods.received")));
    }
}
