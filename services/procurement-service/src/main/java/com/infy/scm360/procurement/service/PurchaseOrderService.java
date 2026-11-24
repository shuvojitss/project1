package com.infy.scm360.procurement.service;

import com.infy.scm360.common.exception.ResourceNotFoundException;
import com.infy.scm360.common.exception.StateConflictException;
import com.infy.scm360.common.security.ScmUserPrincipal;
import com.infy.scm360.procurement.domain.*;
import com.infy.scm360.procurement.dto.*;
import com.infy.scm360.procurement.messaging.ProcurementEventPublisher;
import com.infy.scm360.procurement.repository.GoodsReceiptRefRepository;
import com.infy.scm360.procurement.repository.PoStatusHistoryRepository;
import com.infy.scm360.procurement.repository.PurchaseOrderRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.Instant;
import java.time.LocalDate;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;
import java.util.stream.Collectors;

@Service
@Transactional
public class PurchaseOrderService {

    private final PurchaseOrderRepository poRepository;
    private final PoStatusHistoryRepository historyRepository;
    private final GoodsReceiptRefRepository receiptRefRepository;
    private final ApprovalRuleService approvalRuleService;
    private final ProcurementEventPublisher eventPublisher;
    private final RequisitionService requisitionService;

    public PurchaseOrderService(
            PurchaseOrderRepository poRepository,
            PoStatusHistoryRepository historyRepository,
            GoodsReceiptRefRepository receiptRefRepository,
            ApprovalRuleService approvalRuleService,
            ProcurementEventPublisher eventPublisher,
            RequisitionService requisitionService
    ) {
        this.poRepository = poRepository;
        this.historyRepository = historyRepository;
        this.receiptRefRepository = receiptRefRepository;
        this.approvalRuleService = approvalRuleService;
        this.eventPublisher = eventPublisher;
        this.requisitionService = requisitionService;
    }

    public PurchaseOrderResponse createPurchaseOrder(CreatePoRequest request, ScmUserPrincipal user) {
        PurchaseOrder po = new PurchaseOrder();
        po.setPoNumber(generatePoNumber());
        po.setRequisitionId(request.requisitionId());
        po.setSupplierId(request.supplierId());
        po.setSupplierName(request.supplierName());
        po.setTargetWarehouseId(request.targetWarehouseId());
        po.setTargetWarehouseName(request.targetWarehouseName());
        po.setExpectedDeliveryDate(request.expectedDeliveryDate());
        po.setNotes(request.notes());
        if (request.currency() != null && !request.currency().isBlank()) {
            po.setCurrency(request.currency());
        }
        po.setCreatedBy(user != null ? user.getUserId() : "SYSTEM");
        po.setCreatedByName(user != null ? user.getUsername() : "SYSTEM");
        po.setStatus(PoStatus.PENDING_APPROVAL);

        for (PoLineDto lineDto : request.lines()) {
            PoLine line = new PoLine(
                    lineDto.productId(),
                    lineDto.productSku(),
                    lineDto.productName(),
                    lineDto.orderedQuantity(),
                    lineDto.unitPrice()
            );
            po.addLine(line);
        }

        PurchaseOrder saved = poRepository.save(po);
        historyRepository.save(new PoStatusHistory(
                saved.getId(), "DRAFT", "PENDING_APPROVAL",
                user != null ? user.getUsername() : "SYSTEM", "PO created and submitted for approval"
        ));

        eventPublisher.publishPoCreated(saved.getId(), saved.getPoNumber(), toResponse(saved));
        return toResponse(saved);
    }

    public PurchaseOrderResponse convertRequisitionToPo(String requisitionId, ConvertToPoRequest request, ScmUserPrincipal user) {
        RequisitionResponse req = requisitionService.getRequisition(requisitionId);
        if (req.status() != RequisitionStatus.APPROVED) {
            throw new StateConflictException("Only APPROVED requisitions can be converted to PO. Current status: " + req.status());
        }

        List<PoLineDto> poLines = req.lines().stream()
                .map(l -> new PoLineDto(
                        null,
                        l.productId(),
                        l.productSku(),
                        l.productName(),
                        l.quantity(),
                        BigDecimal.ZERO,
                        l.estimatedUnitPrice(),
                        l.lineTotal()
                )).collect(Collectors.toList());

        CreatePoRequest poRequest = new CreatePoRequest(
                req.id(),
                request.supplierId(),
                request.supplierName(),
                request.targetWarehouseId(),
                request.targetWarehouseName(),
                request.expectedDeliveryDate() != null ? request.expectedDeliveryDate() : LocalDate.now().plusDays(7),
                request.notes() != null ? request.notes() : req.reason(),
                "USD",
                poLines
        );

        PurchaseOrderResponse createdPo = createPurchaseOrder(poRequest, user);
        requisitionService.markConvertedToPo(requisitionId);
        return createdPo;
    }

    public PurchaseOrderResponse approvePurchaseOrder(String id, ApproveRejectRequest request, ScmUserPrincipal user) {
        PurchaseOrder po = poRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Purchase Order not found with id: " + id));

        if (po.getStatus() != PoStatus.PENDING_APPROVAL) {
            throw new StateConflictException("PO cannot be approved from status: " + po.getStatus());
        }

        if (user != null) {
            approvalRuleService.validateApprovalLimit(user.getRoles(), po.getTotalAmount());
        }

        PoStatus oldStatus = po.getStatus();
        po.setStatus(PoStatus.APPROVED);
        po.setApprovedBy(user != null ? user.getUserId() : "SYSTEM");
        po.setApprovedByName(user != null ? user.getUsername() : "SYSTEM");
        po.setApprovedAt(Instant.now());

        PurchaseOrder saved = poRepository.save(po);
        historyRepository.save(new PoStatusHistory(
                saved.getId(), oldStatus.name(), PoStatus.APPROVED.name(),
                user != null ? user.getUsername() : "SYSTEM",
                request != null && request.comments() != null ? request.comments() : "PO Approved"
        ));

        eventPublisher.publishPoApproved(saved.getId(), saved.getPoNumber(), toResponse(saved));
        return toResponse(saved);
    }

    public PurchaseOrderResponse issuePurchaseOrder(String id, ScmUserPrincipal user) {
        PurchaseOrder po = poRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Purchase Order not found with id: " + id));

        if (po.getStatus() != PoStatus.APPROVED) {
            throw new StateConflictException("Only APPROVED purchase orders can be issued. Current status: " + po.getStatus());
        }

        PoStatus oldStatus = po.getStatus();
        po.setStatus(PoStatus.ISSUED);
        po.setIssuedAt(Instant.now());

        PurchaseOrder saved = poRepository.save(po);
        historyRepository.save(new PoStatusHistory(
                saved.getId(), oldStatus.name(), PoStatus.ISSUED.name(),
                user != null ? user.getUsername() : "SYSTEM", "PO issued to supplier"
        ));

        eventPublisher.publishPoIssued(saved.getId(), saved.getPoNumber(), toResponse(saved));
        return toResponse(saved);
    }

    public PurchaseOrderResponse acknowledgePurchaseOrder(String id, String comments) {
        PurchaseOrder po = poRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Purchase Order not found with id: " + id));

        if (po.getStatus() != PoStatus.ISSUED) {
            return toResponse(po);
        }

        PoStatus oldStatus = po.getStatus();
        po.setStatus(PoStatus.ACKNOWLEDGED);
        po.setAcknowledgedAt(Instant.now());

        PurchaseOrder saved = poRepository.save(po);
        historyRepository.save(new PoStatusHistory(
                saved.getId(), oldStatus.name(), PoStatus.ACKNOWLEDGED.name(),
                "SUPPLIER_PORTAL", comments != null ? comments : "Supplier acknowledged PO"
        ));

        return toResponse(saved);
    }

    public PurchaseOrderResponse cancelPurchaseOrder(String id, String reason, ScmUserPrincipal user) {
        PurchaseOrder po = poRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Purchase Order not found with id: " + id));

        if (po.getStatus() == PoStatus.CLOSED || po.getStatus() == PoStatus.CANCELLED || po.getStatus() == PoStatus.RECEIVED) {
            throw new StateConflictException("Cannot cancel PO in status: " + po.getStatus());
        }

        PoStatus oldStatus = po.getStatus();
        po.setStatus(PoStatus.CANCELLED);

        PurchaseOrder saved = poRepository.save(po);
        historyRepository.save(new PoStatusHistory(
                saved.getId(), oldStatus.name(), PoStatus.CANCELLED.name(),
                user != null ? user.getUsername() : "SYSTEM", reason != null ? reason : "PO cancelled"
        ));

        eventPublisher.publishPoCancelled(saved.getId(), saved.getPoNumber(), toResponse(saved));
        return toResponse(saved);
    }

    public PurchaseOrderResponse matchReceipt(String id, MatchReceiptRequest request, String receivedBy) {
        PurchaseOrder po = poRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Purchase Order not found with id: " + id));

        if (po.getStatus() != PoStatus.ISSUED && po.getStatus() != PoStatus.ACKNOWLEDGED && po.getStatus() != PoStatus.PARTIALLY_RECEIVED) {
            throw new StateConflictException("Cannot match receipt against PO in status: " + po.getStatus());
        }

        receiptRefRepository.save(new GoodsReceiptRef(
                po.getId(),
                request.receiptNumber(),
                request.warehouseId(),
                receivedBy != null ? receivedBy : "Warehouse Receiver"
        ));

        for (PoLine line : po.getLines()) {
            if (request.productQuantitiesReceived().containsKey(line.getProductId())) {
                BigDecimal receivedQty = request.productQuantitiesReceived().get(line.getProductId());
                line.setReceivedQuantity(line.getReceivedQuantity().add(receivedQty));
            } else if (request.productQuantitiesReceived().containsKey(line.getProductSku())) {
                BigDecimal receivedQty = request.productQuantitiesReceived().get(line.getProductSku());
                line.setReceivedQuantity(line.getReceivedQuantity().add(receivedQty));
            }
        }

        boolean allReceived = po.getLines().stream()
                .allMatch(l -> l.getReceivedQuantity().compareTo(l.getOrderedQuantity()) >= 0);

        PoStatus oldStatus = po.getStatus();
        PoStatus newStatus = allReceived ? PoStatus.RECEIVED : PoStatus.PARTIALLY_RECEIVED;
        po.setStatus(newStatus);

        PurchaseOrder saved = poRepository.save(po);
        historyRepository.save(new PoStatusHistory(
                saved.getId(), oldStatus.name(), newStatus.name(),
                receivedBy != null ? receivedBy : "Warehouse Receiver",
                "Receipt " + request.receiptNumber() + " matched. New status: " + newStatus
        ));

        if (newStatus == PoStatus.RECEIVED) {
            eventPublisher.publishPoClosed(saved.getId(), saved.getPoNumber(), toResponse(saved));
        }

        return toResponse(saved);
    }

    @Transactional(readOnly = true)
    public PurchaseOrderResponse getPo(String id) {
        return poRepository.findById(id)
                .map(this::toResponse)
                .orElseThrow(() -> new ResourceNotFoundException("Purchase Order not found with id: " + id));
    }

    @Transactional(readOnly = true)
    public Page<PurchaseOrderResponse> listPos(PoStatus status, String supplierId, Pageable pageable) {
        if (status != null) {
            return poRepository.findByStatus(status, pageable).map(this::toResponse);
        }
        if (supplierId != null && !supplierId.isBlank()) {
            return poRepository.findBySupplierId(supplierId, pageable).map(this::toResponse);
        }
        return poRepository.findAll(pageable).map(this::toResponse);
    }

    @Transactional(readOnly = true)
    public List<PoStatusHistoryDto> getHistory(String poId) {
        return historyRepository.findByPoIdOrderByChangedAtAsc(poId).stream()
                .map(h -> new PoStatusHistoryDto(
                        h.getId(),
                        h.getPoId(),
                        h.getFromStatus(),
                        h.getToStatus(),
                        h.getChangedBy(),
                        h.getComments(),
                        h.getChangedAt()
                )).collect(Collectors.toList());
    }

    private String generatePoNumber() {
        int year = LocalDate.now().getYear();
        int rand = ThreadLocalRandom.current().nextInt(1000, 9999);
        return String.format("PO-%d-%04d", year, rand);
    }

    public PurchaseOrderResponse toResponse(PurchaseOrder po) {
        List<PoLineDto> lines = po.getLines().stream()
                .map(l -> new PoLineDto(
                        l.getId(),
                        l.getProductId(),
                        l.getProductSku(),
                        l.getProductName(),
                        l.getOrderedQuantity(),
                        l.getReceivedQuantity(),
                        l.getUnitPrice(),
                        l.getLineTotal()
                )).collect(Collectors.toList());

        return new PurchaseOrderResponse(
                po.getId(),
                po.getPoNumber(),
                po.getRequisitionId(),
                po.getSupplierId(),
                po.getSupplierName(),
                po.getStatus(),
                po.getTotalAmount(),
                po.getCurrency(),
                po.getNotes(),
                po.getCreatedBy(),
                po.getCreatedByName(),
                po.getApprovedBy(),
                po.getApprovedByName(),
                po.getApprovedAt(),
                po.getIssuedAt(),
                po.getAcknowledgedAt(),
                po.getTargetWarehouseId(),
                po.getTargetWarehouseName(),
                po.getExpectedDeliveryDate(),
                po.getCreatedAt(),
                po.getUpdatedAt(),
                lines
        );
    }
}
