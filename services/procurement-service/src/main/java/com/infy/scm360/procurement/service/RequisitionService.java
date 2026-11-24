package com.infy.scm360.procurement.service;

import com.infy.scm360.common.exception.ResourceNotFoundException;
import com.infy.scm360.common.exception.StateConflictException;
import com.infy.scm360.common.security.ScmUserPrincipal;
import com.infy.scm360.procurement.domain.Requisition;
import com.infy.scm360.procurement.domain.RequisitionLine;
import com.infy.scm360.procurement.domain.RequisitionStatus;
import com.infy.scm360.procurement.dto.*;
import com.infy.scm360.procurement.messaging.ProcurementEventPublisher;
import com.infy.scm360.procurement.repository.RequisitionRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;
import java.util.stream.Collectors;

@Service
@Transactional
public class RequisitionService {

    private final RequisitionRepository requisitionRepository;
    private final ApprovalRuleService approvalRuleService;
    private final ProcurementEventPublisher eventPublisher;

    public RequisitionService(
            RequisitionRepository requisitionRepository,
            ApprovalRuleService approvalRuleService,
            ProcurementEventPublisher eventPublisher
    ) {
        this.requisitionRepository = requisitionRepository;
        this.approvalRuleService = approvalRuleService;
        this.eventPublisher = eventPublisher;
    }

    public RequisitionResponse createRequisition(CreateRequisitionRequest request, ScmUserPrincipal user) {
        Requisition req = new Requisition();
        req.setRequisitionNumber(generateReqNumber());
        req.setRequestedBy(user != null ? user.getUserId() : "SYSTEM");
        req.setRequestedByName(user != null ? user.getUsername() : "SYSTEM");
        req.setStatus(RequisitionStatus.PENDING_APPROVAL);
        req.setReason(request.reason());

        for (RequisitionLineDto lineDto : request.lines()) {
            RequisitionLine line = new RequisitionLine(
                    lineDto.productId(),
                    lineDto.productSku(),
                    lineDto.productName(),
                    lineDto.quantity(),
                    lineDto.estimatedUnitPrice()
            );
            req.addLine(line);
        }

        Requisition saved = requisitionRepository.save(req);
        eventPublisher.publishRequisitionCreated(saved.getId(), saved.getRequisitionNumber(), toResponse(saved));
        return toResponse(saved);
    }

    public RequisitionResponse createAutoRequisitionForLowStock(
            String productId, String productSku, String productName,
            BigDecimal quantity, BigDecimal estimatedUnitPrice, String reason
    ) {
        Requisition req = new Requisition();
        req.setRequisitionNumber(generateReqNumber());
        req.setRequestedBy("SYSTEM");
        req.setRequestedByName("Automated Reorder System");
        req.setStatus(RequisitionStatus.PENDING_APPROVAL);
        req.setReason(reason);

        RequisitionLine line = new RequisitionLine(productId, productSku, productName, quantity, estimatedUnitPrice);
        req.addLine(line);

        Requisition saved = requisitionRepository.save(req);
        eventPublisher.publishRequisitionCreated(saved.getId(), saved.getRequisitionNumber(), toResponse(saved));
        return toResponse(saved);
    }

    public RequisitionResponse approveRequisition(String id, ApproveRejectRequest request, ScmUserPrincipal user) {
        Requisition req = requisitionRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Requisition not found with id: " + id));

        if (req.getStatus() != RequisitionStatus.PENDING_APPROVAL) {
            throw new StateConflictException("Requisition cannot be approved from status: " + req.getStatus());
        }

        if (user != null) {
            approvalRuleService.validateApprovalLimit(user.getRoles(), req.getTotalAmount());
        }
        req.setStatus(RequisitionStatus.APPROVED);

        Requisition saved = requisitionRepository.save(req);
        eventPublisher.publishRequisitionApproved(saved.getId(), saved.getRequisitionNumber(), toResponse(saved));
        return toResponse(saved);
    }

    public RequisitionResponse rejectRequisition(String id, ApproveRejectRequest request, ScmUserPrincipal user) {
        Requisition req = requisitionRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Requisition not found with id: " + id));

        if (req.getStatus() != RequisitionStatus.PENDING_APPROVAL) {
            throw new StateConflictException("Requisition cannot be rejected from status: " + req.getStatus());
        }

        req.setStatus(RequisitionStatus.REJECTED);
        return toResponse(requisitionRepository.save(req));
    }

    @Transactional(readOnly = true)
    public RequisitionResponse getRequisition(String id) {
        return requisitionRepository.findById(id)
                .map(this::toResponse)
                .orElseThrow(() -> new ResourceNotFoundException("Requisition not found with id: " + id));
    }

    @Transactional(readOnly = true)
    public Page<RequisitionResponse> listRequisitions(RequisitionStatus status, Pageable pageable) {
        if (status != null) {
            return requisitionRepository.findByStatus(status, pageable).map(this::toResponse);
        }
        return requisitionRepository.findAll(pageable).map(this::toResponse);
    }

    public void markConvertedToPo(String id) {
        Requisition req = requisitionRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Requisition not found with id: " + id));
        req.setStatus(RequisitionStatus.CONVERTED_TO_PO);
        requisitionRepository.save(req);
    }

    private String generateReqNumber() {
        int year = LocalDate.now().getYear();
        int rand = ThreadLocalRandom.current().nextInt(1000, 9999);
        return String.format("REQ-%d-%04d", year, rand);
    }

    public RequisitionResponse toResponse(Requisition req) {
        List<RequisitionLineDto> lines = req.getLines().stream()
                .map(l -> new RequisitionLineDto(
                        l.getId(),
                        l.getProductId(),
                        l.getProductSku(),
                        l.getProductName(),
                        l.getQuantity(),
                        l.getEstimatedUnitPrice(),
                        l.getLineTotal()
                )).collect(Collectors.toList());

        return new RequisitionResponse(
                req.getId(),
                req.getRequisitionNumber(),
                req.getRequestedBy(),
                req.getRequestedByName(),
                req.getStatus(),
                req.getTotalAmount(),
                req.getReason(),
                req.getCreatedAt(),
                req.getUpdatedAt(),
                lines
        );
    }
}
