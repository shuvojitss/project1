package com.infy.scm360.supplier.controller;

import com.infy.scm360.common.dto.PageResponse;
import com.infy.scm360.supplier.dto.CreateSupplierRequest;
import com.infy.scm360.supplier.dto.SupplierDto;
import com.infy.scm360.supplier.dto.SupplierScorecardDto;
import com.infy.scm360.supplier.service.SupplierService;
import jakarta.validation.Valid;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/suppliers")
public class SupplierController {

    private final SupplierService supplierService;

    public SupplierController(SupplierService supplierService) {
        this.supplierService = supplierService;
    }

    @GetMapping
    public ResponseEntity<PageResponse<SupplierDto>> listSuppliers(@PageableDefault(size = 20) Pageable pageable) {
        return ResponseEntity.ok(supplierService.listSuppliers(pageable));
    }

    @GetMapping("/{id}")
    public ResponseEntity<SupplierDto> getSupplierById(@PathVariable String id) {
        return ResponseEntity.ok(supplierService.getSupplierById(id));
    }

    @PostMapping
    @PreAuthorize("hasAnyRole('ADMIN', 'PROCUREMENT_MANAGER', 'PROCUREMENT_OFFICER')")
    public ResponseEntity<SupplierDto> createSupplier(@Valid @RequestBody CreateSupplierRequest req) {
        return ResponseEntity.status(HttpStatus.CREATED).body(supplierService.createSupplier(req));
    }

    @PutMapping("/{id}/approve")
    @PreAuthorize("hasAnyRole('ADMIN', 'PROCUREMENT_MANAGER')")
    public ResponseEntity<SupplierDto> approveSupplier(@PathVariable String id) {
        return ResponseEntity.ok(supplierService.approveSupplier(id));
    }

    @PutMapping("/{id}/suspend")
    @PreAuthorize("hasAnyRole('ADMIN', 'PROCUREMENT_MANAGER')")
    public ResponseEntity<SupplierDto> suspendSupplier(@PathVariable String id) {
        return ResponseEntity.ok(supplierService.suspendSupplier(id));
    }

    @GetMapping("/{id}/scorecard")
    public ResponseEntity<SupplierScorecardDto> getScorecard(@PathVariable String id) {
        return ResponseEntity.ok(supplierService.getScorecard(id));
    }
}
