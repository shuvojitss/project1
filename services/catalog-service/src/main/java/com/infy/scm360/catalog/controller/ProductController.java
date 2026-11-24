package com.infy.scm360.catalog.controller;

import com.infy.scm360.catalog.dto.CreateProductRequest;
import com.infy.scm360.catalog.dto.ProductDto;
import com.infy.scm360.catalog.dto.UpdateProductRequest;
import com.infy.scm360.catalog.service.ProductService;
import com.infy.scm360.common.dto.PageResponse;
import jakarta.validation.Valid;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/products")
public class ProductController {

    private final ProductService productService;

    public ProductController(ProductService productService) {
        this.productService = productService;
    }

    @GetMapping
    public ResponseEntity<PageResponse<ProductDto>> listProducts(
            @RequestParam(required = false) String search,
            @RequestParam(required = false) String categoryId,
            @PageableDefault(size = 20) Pageable pageable) {
        return ResponseEntity.ok(productService.listProducts(search, categoryId, pageable));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ProductDto> getProductById(@PathVariable String id) {
        return ResponseEntity.ok(productService.getProductById(id));
    }

    @PostMapping
    @PreAuthorize("hasAnyRole('ADMIN', 'PROCUREMENT_MANAGER', 'PROCUREMENT_OFFICER')")
    public ResponseEntity<ProductDto> createProduct(@Valid @RequestBody CreateProductRequest req) {
        return ResponseEntity.status(HttpStatus.CREATED).body(productService.createProduct(req));
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'PROCUREMENT_MANAGER', 'PROCUREMENT_OFFICER')")
    public ResponseEntity<ProductDto> updateProduct(
            @PathVariable String id,
            @RequestBody UpdateProductRequest req) {
        return ResponseEntity.ok(productService.updateProduct(id, req));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'PROCUREMENT_MANAGER')")
    public ResponseEntity<Void> discontinueProduct(@PathVariable String id) {
        productService.discontinueProduct(id);
        return ResponseEntity.noContent().build();
    }
}
