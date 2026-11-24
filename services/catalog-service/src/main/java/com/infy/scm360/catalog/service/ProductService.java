package com.infy.scm360.catalog.service;

import com.infy.scm360.catalog.domain.Category;
import com.infy.scm360.catalog.domain.Product;
import com.infy.scm360.catalog.dto.CreateProductRequest;
import com.infy.scm360.catalog.dto.ProductDto;
import com.infy.scm360.catalog.dto.UpdateProductRequest;
import com.infy.scm360.catalog.repository.CategoryRepository;
import com.infy.scm360.catalog.repository.ProductRepository;
import com.infy.scm360.common.dto.PageResponse;
import com.infy.scm360.common.event.EventEnvelope;
import com.infy.scm360.common.event.EventTypes;
import com.infy.scm360.common.exception.ResourceNotFoundException;
import com.infy.scm360.common.exception.StateConflictException;
import com.infy.scm360.common.outbox.OutboxService;
import com.infy.scm360.common.security.SecurityUtils;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.Map;
import java.util.UUID;

@Service
public class ProductService {

    private final ProductRepository productRepository;
    private final CategoryRepository categoryRepository;
    private final OutboxService outboxService;

    public ProductService(ProductRepository productRepository, CategoryRepository categoryRepository, OutboxService outboxService) {
        this.productRepository = productRepository;
        this.categoryRepository = categoryRepository;
        this.outboxService = outboxService;
    }

    @Transactional(readOnly = true)
    public PageResponse<ProductDto> listProducts(String search, String categoryId, Pageable pageable) {
        Page<Product> page;
        if (search != null && !search.isBlank()) {
            page = productRepository.searchProducts(search.trim(), pageable);
        } else if (categoryId != null && !categoryId.isBlank()) {
            page = productRepository.findByCategoryIdAndDeletedAtIsNull(categoryId, pageable);
        } else {
            page = productRepository.findAllByDeletedAtIsNull(pageable);
        }
        return PageResponse.from(page.map(this::mapToDto));
    }

    @Transactional(readOnly = true)
    public ProductDto getProductById(String id) {
        Product p = productRepository.findById(id)
                .filter(prod -> prod.getDeletedAt() == null)
                .orElseThrow(() -> new ResourceNotFoundException("Product", id));
        return mapToDto(p);
    }

    @Transactional
    public ProductDto createProduct(CreateProductRequest req) {
        if (productRepository.findBySku(req.sku()).isPresent()) {
            throw new StateConflictException("Product with SKU '" + req.sku() + "' already exists");
        }

        Category cat = categoryRepository.findById(req.categoryId())
                .orElseThrow(() -> new ResourceNotFoundException("Category", req.categoryId()));

        Product p = new Product();
        p.setId(UUID.randomUUID().toString());
        p.setSku(req.sku());
        p.setName(req.name());
        p.setDescription(req.description());
        p.setCategory(cat);
        p.setUom(req.uom());
        p.setWeight(req.weight());
        p.setDimensions(req.dimensions());
        p.setUnitCost(req.unitCost());
        p.setCurrency(req.currency() != null ? req.currency() : "INR");
        p.setStatus("ACTIVE");
        p.setCreatedAt(Instant.now());
        p.setCreatedBy(SecurityUtils.getCurrentUserId());
        p.setUpdatedAt(Instant.now());
        p.setUpdatedBy(SecurityUtils.getCurrentUserId());

        productRepository.save(p);

        // Publish catalog.product.created outbox event
        var payload = Map.of(
            "productId", p.getId(),
            "sku", p.getSku(),
            "name", p.getName(),
            "categoryId", cat.getId(),
            "uom", p.getUom(),
            "weight", p.getWeight(),
            "unitCost", p.getUnitCost(),
            "currency", p.getCurrency(),
            "status", p.getStatus()
        );
        EventEnvelope<?> envelope = EventEnvelope.of(EventTypes.CATALOG_PRODUCT_CREATED, "catalog-service", payload);
        outboxService.publish(EventTypes.EXCHANGE_CATALOG, EventTypes.CATALOG_PRODUCT_CREATED, "Product", p.getId(), envelope);

        return mapToDto(p);
    }

    @Transactional
    public ProductDto updateProduct(String id, UpdateProductRequest req) {
        Product p = productRepository.findById(id)
                .filter(prod -> prod.getDeletedAt() == null)
                .orElseThrow(() -> new ResourceNotFoundException("Product", id));

        if (req.name() != null) p.setName(req.name());
        if (req.description() != null) p.setDescription(req.description());
        if (req.categoryId() != null) {
            Category cat = categoryRepository.findById(req.categoryId())
                    .orElseThrow(() -> new ResourceNotFoundException("Category", req.categoryId()));
            p.setCategory(cat);
        }
        if (req.uom() != null) p.setUom(req.uom());
        if (req.weight() != null) p.setWeight(req.weight());
        if (req.dimensions() != null) p.setDimensions(req.dimensions());
        if (req.unitCost() != null) p.setUnitCost(req.unitCost());
        if (req.currency() != null) p.setCurrency(req.currency());
        if (req.status() != null) p.setStatus(req.status());
        p.setUpdatedAt(Instant.now());
        p.setUpdatedBy(SecurityUtils.getCurrentUserId());

        productRepository.save(p);

        var payload = Map.of(
            "productId", p.getId(),
            "sku", p.getSku(),
            "name", p.getName(),
            "categoryId", p.getCategory().getId(),
            "uom", p.getUom(),
            "weight", p.getWeight(),
            "unitCost", p.getUnitCost(),
            "currency", p.getCurrency(),
            "status", p.getStatus()
        );
        EventEnvelope<?> envelope = EventEnvelope.of(EventTypes.CATALOG_PRODUCT_UPDATED, "catalog-service", payload);
        outboxService.publish(EventTypes.EXCHANGE_CATALOG, EventTypes.CATALOG_PRODUCT_UPDATED, "Product", p.getId(), envelope);

        return mapToDto(p);
    }

    @Transactional
    public void discontinueProduct(String id) {
        Product p = productRepository.findById(id)
                .filter(prod -> prod.getDeletedAt() == null)
                .orElseThrow(() -> new ResourceNotFoundException("Product", id));

        p.setStatus("DISCONTINUED");
        p.setUpdatedAt(Instant.now());
        p.setUpdatedBy(SecurityUtils.getCurrentUserId());
        productRepository.save(p);

        var payload = Map.of("productId", p.getId(), "sku", p.getSku(), "status", "DISCONTINUED");
        EventEnvelope<?> envelope = EventEnvelope.of(EventTypes.CATALOG_PRODUCT_DISCONTINUED, "catalog-service", payload);
        outboxService.publish(EventTypes.EXCHANGE_CATALOG, EventTypes.CATALOG_PRODUCT_DISCONTINUED, "Product", p.getId(), envelope);
    }

    private ProductDto mapToDto(Product p) {
        return new ProductDto(
            p.getId(),
            p.getSku(),
            p.getName(),
            p.getDescription(),
            p.getCategory() != null ? p.getCategory().getId() : null,
            p.getCategory() != null ? p.getCategory().getName() : null,
            p.getUom(),
            p.getWeight(),
            p.getDimensions(),
            p.getUnitCost(),
            p.getCurrency(),
            p.getStatus()
        );
    }
}
