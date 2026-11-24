package com.infy.scm360.catalog;

import com.infy.scm360.catalog.dto.CategoryDto;
import com.infy.scm360.catalog.dto.CreateProductRequest;
import com.infy.scm360.catalog.dto.ProductDto;
import com.infy.scm360.catalog.service.CategoryService;
import com.infy.scm360.catalog.service.ProductService;
import com.infy.scm360.common.outbox.OutboxRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@ActiveProfiles("test")
class CatalogServiceApplicationTests {

    @Autowired
    private ProductService productService;

    @Autowired
    private CategoryService categoryService;

    @Autowired
    private OutboxRepository outboxRepository;

    @Test
    void testCreateProductAndOutboxEvent() {
        CategoryDto cat = categoryService.createCategory(new CategoryDto(null, "Test Category", "Testing", null));
        assertNotNull(cat.id());

        CreateProductRequest req = new CreateProductRequest(
            "SKU-TEST-9999",
            "Precision Microcontroller",
            "32-bit Cortex M4",
            cat.id(),
            "EA",
            new BigDecimal("0.050"),
            "3x3x1 cm",
            new BigDecimal("350.00"),
            "INR"
        );

        ProductDto created = productService.createProduct(req);
        assertNotNull(created.id());
        assertEquals("SKU-TEST-9999", created.sku());
        assertEquals("ACTIVE", created.status());

        // Verify outbox event was recorded in the transaction
        var outboxEvents = outboxRepository.findAll();
        assertFalse(outboxEvents.isEmpty());
        assertTrue(outboxEvents.stream().anyMatch(e -> e.getRoutingKey().equals("catalog.product.created")));
    }
}
