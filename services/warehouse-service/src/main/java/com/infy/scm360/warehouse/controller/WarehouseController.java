package com.infy.scm360.warehouse.controller;

import com.infy.scm360.common.dto.PageResponse;
import com.infy.scm360.warehouse.dto.*;
import com.infy.scm360.warehouse.service.WarehouseService;
import jakarta.validation.Valid;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/v1/warehouses")
public class WarehouseController {

    private final WarehouseService warehouseService;

    public WarehouseController(WarehouseService warehouseService) {
        this.warehouseService = warehouseService;
    }

    @GetMapping
    public ResponseEntity<PageResponse<WarehouseDto>> listWarehouses(@PageableDefault(size = 20) Pageable pageable) {
        return ResponseEntity.ok(warehouseService.listWarehouses(pageable));
    }

    @GetMapping("/{id}")
    public ResponseEntity<WarehouseDto> getWarehouseById(@PathVariable String id) {
        return ResponseEntity.ok(warehouseService.getWarehouseById(id));
    }

    @PostMapping("/receipts/receive")
    @PreAuthorize("hasAnyRole('ADMIN', 'WAREHOUSE_MANAGER', 'WAREHOUSE_OPERATOR')")
    public ResponseEntity<Void> receiveGoods(@Valid @RequestBody ReceiveGoodsRequest req) {
        warehouseService.receiveGoods(req);
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/pick-lists/{id}/complete")
    @PreAuthorize("hasAnyRole('ADMIN', 'WAREHOUSE_MANAGER', 'WAREHOUSE_OPERATOR')")
    public ResponseEntity<Void> completePick(@PathVariable String id) {
        warehouseService.completePick(id);
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/pack")
    @PreAuthorize("hasAnyRole('ADMIN', 'WAREHOUSE_MANAGER', 'WAREHOUSE_OPERATOR')")
    public ResponseEntity<Void> completePack(
            @RequestParam String orderId,
            @RequestParam String pickListId,
            @RequestParam String warehouseId) {
        warehouseService.completePack(orderId, pickListId, warehouseId);
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/transfers")
    @PreAuthorize("hasAnyRole('ADMIN', 'WAREHOUSE_MANAGER')")
    public ResponseEntity<Map<String, String>> createTransfer(@Valid @RequestBody CreateTransferRequest req) {
        String id = warehouseService.createTransfer(req);
        return ResponseEntity.ok(Map.of("transferId", id));
    }

    @PostMapping("/transfers/{id}/dispatch")
    @PreAuthorize("hasAnyRole('ADMIN', 'WAREHOUSE_MANAGER', 'WAREHOUSE_OPERATOR')")
    public ResponseEntity<Void> dispatchTransfer(@PathVariable String id) {
        warehouseService.dispatchTransfer(id);
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/transfers/{id}/receive")
    @PreAuthorize("hasAnyRole('ADMIN', 'WAREHOUSE_MANAGER', 'WAREHOUSE_OPERATOR')")
    public ResponseEntity<Void> receiveTransfer(@PathVariable String id) {
        warehouseService.receiveTransfer(id);
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/cycle-counts")
    @PreAuthorize("hasAnyRole('ADMIN', 'WAREHOUSE_MANAGER', 'WAREHOUSE_OPERATOR')")
    public ResponseEntity<Map<String, String>> submitCycleCount(@Valid @RequestBody SubmitCycleCountRequest req) {
        String id = warehouseService.submitCycleCount(req);
        return ResponseEntity.ok(Map.of("countId", id));
    }

    @PostMapping("/cycle-counts/{id}/approve")
    @PreAuthorize("hasAnyRole('ADMIN', 'WAREHOUSE_MANAGER')")
    public ResponseEntity<Void> approveCycleCountVariance(@PathVariable String id) {
        warehouseService.approveCycleCountVariance(id);
        return ResponseEntity.noContent().build();
    }
}
