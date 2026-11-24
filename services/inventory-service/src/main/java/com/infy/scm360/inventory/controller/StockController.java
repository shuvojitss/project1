package com.infy.scm360.inventory.controller;

import com.infy.scm360.common.dto.PageResponse;
import com.infy.scm360.inventory.dto.ReserveStockRequest;
import com.infy.scm360.inventory.dto.StockLedgerDto;
import com.infy.scm360.inventory.dto.StockLevelDto;
import com.infy.scm360.inventory.service.InventoryService;
import jakarta.validation.Valid;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/v1/inventory")
public class StockController {

    private final InventoryService inventoryService;

    public StockController(InventoryService inventoryService) {
        this.inventoryService = inventoryService;
    }

    @GetMapping("/stock")
    public ResponseEntity<PageResponse<StockLevelDto>> listStock(
            @RequestParam(required = false) String warehouseId,
            @PageableDefault(size = 20) Pageable pageable) {
        return ResponseEntity.ok(inventoryService.listStock(warehouseId, pageable));
    }

    @GetMapping("/ledger")
    public ResponseEntity<PageResponse<StockLedgerDto>> listLedger(
            @RequestParam String productId,
            @RequestParam String warehouseId,
            @PageableDefault(size = 20) Pageable pageable) {
        return ResponseEntity.ok(inventoryService.listLedger(productId, warehouseId, pageable));
    }

    @PostMapping("/reservations")
    @PreAuthorize("hasAnyRole('ADMIN', 'SALES_COORDINATOR')")
    public ResponseEntity<Map<String, Object>> reserveStock(@Valid @RequestBody ReserveStockRequest req) {
        boolean success = inventoryService.reserveStock(req);
        return ResponseEntity.ok(Map.of(
            "orderId", req.orderId(),
            "reserved", success
        ));
    }
}
