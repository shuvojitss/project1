package com.infy.scm360.procurement.controller;

import com.infy.scm360.common.dto.PageResponse;
import com.infy.scm360.common.security.ScmUserPrincipal;
import com.infy.scm360.common.security.SecurityUtils;
import com.infy.scm360.procurement.domain.PoStatus;
import com.infy.scm360.procurement.dto.*;
import com.infy.scm360.procurement.service.PurchaseOrderService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/purchase-orders")
@Tag(name = "Procurement Purchase Orders", description = "Purchase order lifecycle, approval rules, issuing, and receipt matching")
public class PurchaseOrderController {

    private final PurchaseOrderService purchaseOrderService;

    public PurchaseOrderController(PurchaseOrderService purchaseOrderService) {
        this.purchaseOrderService = purchaseOrderService;
    }

    @PostMapping
    @PreAuthorize("hasAnyRole('PROCUREMENT_OFFICER', 'PROCUREMENT_MANAGER', 'ADMIN')")
    @Operation(summary = "Create purchase order")
    public ResponseEntity<PurchaseOrderResponse> createPo(@Valid @RequestBody CreatePoRequest request) {
        ScmUserPrincipal user = SecurityUtils.getCurrentUser().orElse(null);
        return ResponseEntity.status(HttpStatus.CREATED).body(purchaseOrderService.createPurchaseOrder(request, user));
    }

    @GetMapping
    @PreAuthorize("isAuthenticated()")
    @Operation(summary = "List purchase orders")
    public ResponseEntity<PageResponse<PurchaseOrderResponse>> listPos(
            @RequestParam(required = false) PoStatus status,
            @RequestParam(required = false) String supplierId,
            Pageable pageable
    ) {
        ScmUserPrincipal user = SecurityUtils.getCurrentUser().orElse(null);
        if (user != null && user.getRoles().contains("SUPPLIER_USER")) {
            supplierId = user.getSupplierId();
        }
        Page<PurchaseOrderResponse> page = purchaseOrderService.listPos(status, supplierId, pageable);
        return ResponseEntity.ok(PageResponse.from(page));
    }

    @GetMapping("/{id}")
    @PreAuthorize("isAuthenticated()")
    @Operation(summary = "Get purchase order by id")
    public ResponseEntity<PurchaseOrderResponse> getPo(@PathVariable String id) {
        return ResponseEntity.ok(purchaseOrderService.getPo(id));
    }

    @PostMapping("/{id}/approve")
    @PreAuthorize("hasAnyRole('PROCUREMENT_OFFICER', 'PROCUREMENT_MANAGER', 'ADMIN')")
    @Operation(summary = "Approve purchase order (validates role amount threshold)")
    public ResponseEntity<PurchaseOrderResponse> approvePo(
            @PathVariable String id,
            @RequestBody(required = false) ApproveRejectRequest request
    ) {
        ScmUserPrincipal user = SecurityUtils.getCurrentUser().orElse(null);
        return ResponseEntity.ok(purchaseOrderService.approvePurchaseOrder(id, request, user));
    }

    @PostMapping("/{id}/issue")
    @PreAuthorize("hasAnyRole('PROCUREMENT_OFFICER', 'PROCUREMENT_MANAGER', 'ADMIN')")
    @Operation(summary = "Issue approved purchase order to supplier")
    public ResponseEntity<PurchaseOrderResponse> issuePo(@PathVariable String id) {
        ScmUserPrincipal user = SecurityUtils.getCurrentUser().orElse(null);
        return ResponseEntity.ok(purchaseOrderService.issuePurchaseOrder(id, user));
    }

    @PostMapping("/{id}/cancel")
    @PreAuthorize("hasAnyRole('PROCUREMENT_OFFICER', 'PROCUREMENT_MANAGER', 'ADMIN')")
    @Operation(summary = "Cancel purchase order")
    public ResponseEntity<PurchaseOrderResponse> cancelPo(
            @PathVariable String id,
            @RequestParam(required = false, defaultValue = "Cancelled by user") String reason
    ) {
        ScmUserPrincipal user = SecurityUtils.getCurrentUser().orElse(null);
        return ResponseEntity.ok(purchaseOrderService.cancelPurchaseOrder(id, reason, user));
    }

    @GetMapping("/{id}/history")
    @PreAuthorize("isAuthenticated()")
    @Operation(summary = "Get purchase order status transition history")
    public ResponseEntity<List<PoStatusHistoryDto>> getHistory(@PathVariable String id) {
        return ResponseEntity.ok(purchaseOrderService.getHistory(id));
    }

    @PostMapping("/{id}/match-receipt")
    @PreAuthorize("hasAnyRole('WAREHOUSE_MANAGER', 'WAREHOUSE_OPERATOR', 'PROCUREMENT_MANAGER', 'ADMIN')")
    @Operation(summary = "Match warehouse goods receipt against purchase order lines")
    public ResponseEntity<PurchaseOrderResponse> matchReceipt(
            @PathVariable String id,
            @Valid @RequestBody MatchReceiptRequest request
    ) {
        ScmUserPrincipal user = SecurityUtils.getCurrentUser().orElse(null);
        String receivedBy = user != null ? user.getUsername() : "Warehouse Operator";
        return ResponseEntity.ok(purchaseOrderService.matchReceipt(id, request, receivedBy));
    }
}
