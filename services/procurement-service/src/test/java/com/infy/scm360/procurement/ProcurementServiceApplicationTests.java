package com.infy.scm360.procurement;

import com.infy.scm360.common.security.ScmUserPrincipal;
import com.infy.scm360.procurement.domain.PoStatus;
import com.infy.scm360.procurement.domain.RequisitionStatus;
import com.infy.scm360.procurement.dto.*;
import com.infy.scm360.procurement.service.PurchaseOrderService;
import com.infy.scm360.procurement.service.RequisitionService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@ActiveProfiles("test")
@Transactional
class ProcurementServiceApplicationTests {

    @Autowired
    private RequisitionService requisitionService;

    @Autowired
    private PurchaseOrderService purchaseOrderService;

    @Test
    @DisplayName("Context loads and seed data is accessible")
    void contextLoads() {
        assertNotNull(requisitionService);
        assertNotNull(purchaseOrderService);
    }

    @Test
    @DisplayName("Requisition creation and approval workflow")
    void testRequisitionLifecycle() {
        ScmUserPrincipal officer = new ScmUserPrincipal(
                "00000000-0000-0000-0000-000000000003",
                "officer1",
                List.of("PROCUREMENT_OFFICER"),
                List.of(),
                null
        );

        CreateRequisitionRequest req = new CreateRequisitionRequest(
                "Restock raw material",
                List.of(new RequisitionLineDto(
                        null,
                        "c0000000-0000-0000-0000-000000000001",
                        "SKU-RAW-001",
                        "Steel Sheet",
                        BigDecimal.valueOf(10),
                        BigDecimal.valueOf(150.00),
                        null
                ))
        );

        RequisitionResponse created = requisitionService.createRequisition(req, officer);
        assertNotNull(created.id());
        assertEquals(RequisitionStatus.PENDING_APPROVAL, created.status());
        assertEquals(0, BigDecimal.valueOf(1500.00).compareTo(created.totalAmount()));

        // Officer approves requisition ($1500 is within $10,000 officer limit)
        RequisitionResponse approved = requisitionService.approveRequisition(
                created.id(), new ApproveRejectRequest("Looks good"), officer
        );
        assertEquals(RequisitionStatus.APPROVED, approved.status());

        // Convert requisition to PO
        ConvertToPoRequest convertReq = new ConvertToPoRequest(
                "a0000000-0000-0000-0000-000000000001",
                "Apex Global",
                "b0000000-0000-0000-0000-000000000001",
                "Central Hub",
                LocalDate.now().plusDays(5),
                "Convert test"
        );
        PurchaseOrderResponse po = purchaseOrderService.convertRequisitionToPo(created.id(), convertReq, officer);
        assertNotNull(po.id());
        assertEquals(PoStatus.PENDING_APPROVAL, po.status());
        assertEquals(0, BigDecimal.valueOf(1500.00).compareTo(po.totalAmount()));
    }

    @Test
    @DisplayName("Approval rule enforcement prevents Officer from approving high amount PO")
    void testApprovalRuleEnforcement() {
        ScmUserPrincipal officer = new ScmUserPrincipal(
                "00000000-0000-0000-0000-000000000003",
                "officer1",
                List.of("PROCUREMENT_OFFICER"),
                List.of(),
                null
        );

        ScmUserPrincipal manager = new ScmUserPrincipal(
                "00000000-0000-0000-0000-000000000002",
                "manager1",
                List.of("PROCUREMENT_MANAGER"),
                List.of(),
                null
        );

        // Create PO of $25,000 (exceeds $10,000 officer threshold)
        CreatePoRequest poReq = new CreatePoRequest(
                null,
                "a0000000-0000-0000-0000-000000000001",
                "Apex Global",
                "b0000000-0000-0000-0000-000000000001",
                "Central Hub",
                LocalDate.now().plusDays(7),
                "Large machinery order",
                "USD",
                List.of(new PoLineDto(
                        null,
                        "c0000000-0000-0000-0000-000000000002",
                        "SKU-MCH-001",
                        "Heavy Press",
                        BigDecimal.valueOf(1),
                        BigDecimal.ZERO,
                        BigDecimal.valueOf(25000.00),
                        null
                ))
        );

        PurchaseOrderResponse po = purchaseOrderService.createPurchaseOrder(poReq, officer);

        // Officer tries to approve -> throws AccessDeniedException
        assertThrows(AccessDeniedException.class, () ->
                purchaseOrderService.approvePurchaseOrder(po.id(), new ApproveRejectRequest("Officer trying to approve"), officer)
        );

        // Manager approves -> success
        PurchaseOrderResponse approved = purchaseOrderService.approvePurchaseOrder(
                po.id(), new ApproveRejectRequest("Manager approved"), manager
        );
        assertEquals(PoStatus.APPROVED, approved.status());

        // Issue PO
        PurchaseOrderResponse issued = purchaseOrderService.issuePurchaseOrder(po.id(), officer);
        assertEquals(PoStatus.ISSUED, issued.status());

        // Match receipt
        MatchReceiptRequest matchReq = new MatchReceiptRequest(
                "REC-TEST-001",
                "b0000000-0000-0000-0000-000000000001",
                Map.of("c0000000-0000-0000-0000-000000000002", BigDecimal.valueOf(1))
        );
        PurchaseOrderResponse received = purchaseOrderService.matchReceipt(po.id(), matchReq, "Inspector");
        assertEquals(PoStatus.RECEIVED, received.status());
    }
}
