package com.infy.scm360.supplier;

import com.infy.scm360.common.outbox.OutboxRepository;
import com.infy.scm360.supplier.dto.CreateSupplierRequest;
import com.infy.scm360.supplier.dto.SupplierDto;
import com.infy.scm360.supplier.service.SupplierService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@ActiveProfiles("test")
class SupplierServiceApplicationTests {

    @Autowired
    private SupplierService supplierService;

    @Autowired
    private OutboxRepository outboxRepository;

    @Test
    void testOnboardingAndApprovalFlow() {
        CreateSupplierRequest req = new CreateSupplierRequest(
            "SUP-TEST-99",
            "Reliable Fasteners Ltd",
            "GSTIN29AAACR9999Z1Z5",
            "Karan Malhotra",
            "karan@reliablefasteners.com",
            "+919876543210"
        );

        SupplierDto created = supplierService.createSupplier(req);
        assertNotNull(created.id());
        assertEquals("PENDING", created.status());

        // Approve supplier
        SupplierDto approved = supplierService.approveSupplier(created.id());
        assertEquals("APPROVED", approved.status());

        // Verify outbox event supplier.approved was created
        var outboxEvents = outboxRepository.findAll();
        assertFalse(outboxEvents.isEmpty());
        assertTrue(outboxEvents.stream().anyMatch(e -> e.getRoutingKey().equals("supplier.approved")));
    }
}
