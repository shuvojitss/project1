package com.infy.scm360.supplier.controller;

import com.infy.scm360.common.exception.StateConflictException;
import com.infy.scm360.common.security.SecurityUtils;
import com.infy.scm360.supplier.dto.PoAcknowledgeRequest;
import com.infy.scm360.supplier.dto.SubmitAsnRequest;
import com.infy.scm360.supplier.dto.SupplierScorecardDto;
import com.infy.scm360.supplier.service.SupplierService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/v1/portal")
@PreAuthorize("hasRole('SUPPLIER_USER') or hasRole('ADMIN')")
public class SupplierPortalController {

    private final SupplierService supplierService;

    public SupplierPortalController(SupplierService supplierService) {
        this.supplierService = supplierService;
    }

    private String resolveSupplierId() {
        String supId = SecurityUtils.getCurrentSupplierId();
        if (supId == null && SecurityUtils.hasRole("ADMIN")) {
            // Default demo supplier for admin viewing portal
            return "sup-00000000-0000-0000-0000-000000000001";
        }
        if (supId == null) {
            throw new StateConflictException("User is not associated with any supplier");
        }
        return supId;
    }

    @GetMapping("/scorecard")
    public ResponseEntity<SupplierScorecardDto> getMyScorecard() {
        return ResponseEntity.ok(supplierService.getScorecard(resolveSupplierId()));
    }

    @PostMapping("/purchase-orders/{poId}/acknowledge")
    public ResponseEntity<Void> acknowledgePo(
            @PathVariable String poId,
            @RequestBody PoAcknowledgeRequest req) {
        supplierService.recordPoAcknowledgement(resolveSupplierId(), poId, req);
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/asn")
    public ResponseEntity<Map<String, String>> submitAsn(@Valid @RequestBody SubmitAsnRequest req) {
        String asnId = supplierService.submitAsn(resolveSupplierId(), req);
        return ResponseEntity.ok(Map.of("asnId", asnId));
    }
}
