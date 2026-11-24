package com.infy.scm360.procurement.controller;

import com.infy.scm360.common.dto.PageResponse;
import com.infy.scm360.common.security.ScmUserPrincipal;
import com.infy.scm360.common.security.SecurityUtils;
import com.infy.scm360.procurement.domain.RequisitionStatus;
import com.infy.scm360.procurement.dto.*;
import com.infy.scm360.procurement.service.PurchaseOrderService;
import com.infy.scm360.procurement.service.RequisitionService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/requisitions")
@Tag(name = "Procurement Requisitions", description = "Purchase requisition creation, approval, and PO conversion")
public class RequisitionController {

    private final RequisitionService requisitionService;
    private final PurchaseOrderService purchaseOrderService;

    public RequisitionController(RequisitionService requisitionService, PurchaseOrderService purchaseOrderService) {
        this.requisitionService = requisitionService;
        this.purchaseOrderService = purchaseOrderService;
    }

    @PostMapping
    @PreAuthorize("hasAnyRole('PROCUREMENT_OFFICER', 'PROCUREMENT_MANAGER', 'ADMIN', 'WAREHOUSE_MANAGER')")
    @Operation(summary = "Create purchase requisition")
    public ResponseEntity<RequisitionResponse> createRequisition(@Valid @RequestBody CreateRequisitionRequest request) {
        ScmUserPrincipal user = SecurityUtils.getCurrentUser().orElse(null);
        return ResponseEntity.status(HttpStatus.CREATED).body(requisitionService.createRequisition(request, user));
    }

    @GetMapping
    @PreAuthorize("isAuthenticated()")
    @Operation(summary = "List purchase requisitions")
    public ResponseEntity<PageResponse<RequisitionResponse>> listRequisitions(
            @RequestParam(required = false) RequisitionStatus status,
            Pageable pageable
    ) {
        Page<RequisitionResponse> page = requisitionService.listRequisitions(status, pageable);
        return ResponseEntity.ok(PageResponse.from(page));
    }

    @GetMapping("/{id}")
    @PreAuthorize("isAuthenticated()")
    @Operation(summary = "Get purchase requisition by id")
    public ResponseEntity<RequisitionResponse> getRequisition(@PathVariable String id) {
        return ResponseEntity.ok(requisitionService.getRequisition(id));
    }

    @PostMapping("/{id}/approve")
    @PreAuthorize("hasAnyRole('PROCUREMENT_OFFICER', 'PROCUREMENT_MANAGER', 'ADMIN')")
    @Operation(summary = "Approve purchase requisition")
    public ResponseEntity<RequisitionResponse> approveRequisition(
            @PathVariable String id,
            @RequestBody(required = false) ApproveRejectRequest request
    ) {
        ScmUserPrincipal user = SecurityUtils.getCurrentUser().orElse(null);
        return ResponseEntity.ok(requisitionService.approveRequisition(id, request, user));
    }

    @PostMapping("/{id}/reject")
    @PreAuthorize("hasAnyRole('PROCUREMENT_OFFICER', 'PROCUREMENT_MANAGER', 'ADMIN')")
    @Operation(summary = "Reject purchase requisition")
    public ResponseEntity<RequisitionResponse> rejectRequisition(
            @PathVariable String id,
            @RequestBody(required = false) ApproveRejectRequest request
    ) {
        ScmUserPrincipal user = SecurityUtils.getCurrentUser().orElse(null);
        return ResponseEntity.ok(requisitionService.rejectRequisition(id, request, user));
    }

    @PostMapping("/{id}/convert-to-po")
    @PreAuthorize("hasAnyRole('PROCUREMENT_OFFICER', 'PROCUREMENT_MANAGER', 'ADMIN')")
    @Operation(summary = "Convert approved requisition into purchase order")
    public ResponseEntity<PurchaseOrderResponse> convertToPo(
            @PathVariable String id,
            @Valid @RequestBody ConvertToPoRequest request
    ) {
        ScmUserPrincipal user = SecurityUtils.getCurrentUser().orElse(null);
        return ResponseEntity.status(HttpStatus.CREATED).body(purchaseOrderService.convertRequisitionToPo(id, request, user));
    }
}
