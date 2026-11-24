package com.infy.scm360.supplier.service;

import com.infy.scm360.common.dto.PageResponse;
import com.infy.scm360.common.event.EventEnvelope;
import com.infy.scm360.common.event.EventTypes;
import com.infy.scm360.common.exception.ResourceNotFoundException;
import com.infy.scm360.common.exception.StateConflictException;
import com.infy.scm360.common.outbox.OutboxService;
import com.infy.scm360.common.security.SecurityUtils;
import com.infy.scm360.supplier.domain.*;
import com.infy.scm360.supplier.dto.*;
import com.infy.scm360.supplier.repository.*;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.Instant;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@Service
public class SupplierService {

    private final SupplierRepository supplierRepository;
    private final SupplierScorecardRepository scorecardRepository;
    private final SupplierItemRepository supplierItemRepository;
    private final AsnRepository asnRepository;
    private final OutboxService outboxService;

    public SupplierService(
            SupplierRepository supplierRepository,
            SupplierScorecardRepository scorecardRepository,
            SupplierItemRepository supplierItemRepository,
            AsnRepository asnRepository,
            OutboxService outboxService) {
        this.supplierRepository = supplierRepository;
        this.scorecardRepository = scorecardRepository;
        this.supplierItemRepository = supplierItemRepository;
        this.asnRepository = asnRepository;
        this.outboxService = outboxService;
    }

    @Transactional(readOnly = true)
    public PageResponse<SupplierDto> listSuppliers(Pageable pageable) {
        Page<Supplier> page = supplierRepository.findAllByDeletedAtIsNull(pageable);
        return PageResponse.from(page.map(this::mapToDto));
    }

    @Transactional(readOnly = true)
    public SupplierDto getSupplierById(String id) {
        Supplier s = supplierRepository.findById(id)
                .filter(sup -> sup.getDeletedAt() == null)
                .orElseThrow(() -> new ResourceNotFoundException("Supplier", id));
        return mapToDto(s);
    }

    @Transactional
    public SupplierDto createSupplier(CreateSupplierRequest req) {
        if (supplierRepository.findByCode(req.code()).isPresent()) {
            throw new StateConflictException("Supplier with code '" + req.code() + "' already exists");
        }

        Supplier s = new Supplier();
        s.setId(UUID.randomUUID().toString());
        s.setCode(req.code());
        s.setLegalName(req.legalName());
        s.setTaxId(req.taxId());
        s.setStatus("PENDING");
        s.setCreatedAt(Instant.now());
        s.setCreatedBy(SecurityUtils.getCurrentUserId());
        s.setUpdatedAt(Instant.now());
        s.setUpdatedBy(SecurityUtils.getCurrentUserId());

        if (req.contactName() != null && !req.contactName().isBlank()) {
            SupplierContact sc = new SupplierContact();
            sc.setId(UUID.randomUUID().toString());
            sc.setSupplier(s);
            sc.setName(req.contactName());
            sc.setEmail(req.contactEmail() != null ? req.contactEmail() : "");
            sc.setPhone(req.contactPhone());
            sc.setRole("PRIMARY");
            sc.setCreatedAt(Instant.now());
            s.getContacts().add(sc);
        }

        // Initialize scorecard
        SupplierScorecard sc = new SupplierScorecard();
        sc.setId(UUID.randomUUID().toString());
        sc.setSupplier(s);
        sc.setOverallScore(BigDecimal.valueOf(80.00));
        sc.setOnTimeDeliveryRate(BigDecimal.valueOf(85.00));
        sc.setQualityScore(BigDecimal.valueOf(90.00));
        sc.setLeadTimeVarianceScore(BigDecimal.valueOf(80.00));
        sc.setResponsivenessScore(BigDecimal.valueOf(80.00));
        sc.setUpdatedAt(Instant.now());
        s.setScorecard(sc);

        supplierRepository.save(s);
        return mapToDto(s);
    }

    @Transactional
    public SupplierDto approveSupplier(String id) {
        Supplier s = supplierRepository.findById(id)
                .filter(sup -> sup.getDeletedAt() == null)
                .orElseThrow(() -> new ResourceNotFoundException("Supplier", id));

        s.setStatus("APPROVED");
        s.setUpdatedAt(Instant.now());
        s.setUpdatedBy(SecurityUtils.getCurrentUserId());
        supplierRepository.save(s);

        var payload = Map.of(
            "supplierId", s.getId(),
            "code", s.getCode(),
            "legalName", s.getLegalName(),
            "status", "APPROVED"
        );
        EventEnvelope<?> env = EventEnvelope.of(EventTypes.SUPPLIER_APPROVED, "supplier-service", payload);
        outboxService.publish(EventTypes.EXCHANGE_SUPPLIER, EventTypes.SUPPLIER_APPROVED, "Supplier", s.getId(), env);

        return mapToDto(s);
    }

    @Transactional
    public SupplierDto suspendSupplier(String id) {
        Supplier s = supplierRepository.findById(id)
                .filter(sup -> sup.getDeletedAt() == null)
                .orElseThrow(() -> new ResourceNotFoundException("Supplier", id));

        s.setStatus("SUSPENDED");
        s.setUpdatedAt(Instant.now());
        s.setUpdatedBy(SecurityUtils.getCurrentUserId());
        supplierRepository.save(s);

        var payload = Map.of(
            "supplierId", s.getId(),
            "code", s.getCode(),
            "status", "SUSPENDED"
        );
        EventEnvelope<?> env = EventEnvelope.of(EventTypes.SUPPLIER_SUSPENDED, "supplier-service", payload);
        outboxService.publish(EventTypes.EXCHANGE_SUPPLIER, EventTypes.SUPPLIER_SUSPENDED, "Supplier", s.getId(), env);

        return mapToDto(s);
    }

    @Transactional(readOnly = true)
    public SupplierScorecardDto getScorecard(String supplierId) {
        SupplierScorecard sc = scorecardRepository.findBySupplierId(supplierId)
                .orElseThrow(() -> new ResourceNotFoundException("Scorecard for supplier", supplierId));
        return new SupplierScorecardDto(
            supplierId,
            sc.getOverallScore(),
            sc.getOnTimeDeliveryRate(),
            sc.getQualityScore(),
            sc.getLeadTimeVarianceScore(),
            sc.getResponsivenessScore(),
            sc.getUpdatedAt()
        );
    }

    @Transactional
    public void recordPoAcknowledgement(String supplierId, String poId, PoAcknowledgeRequest req) {
        var payload = Map.of(
            "supplierId", supplierId,
            "poId", poId,
            "accepted", req.accepted(),
            "notes", req.notes() != null ? req.notes() : ""
        );
        EventEnvelope<?> env = EventEnvelope.of(EventTypes.SUPPLIER_PO_ACKNOWLEDGED, "supplier-service", payload);
        outboxService.publish(EventTypes.EXCHANGE_SUPPLIER, EventTypes.SUPPLIER_PO_ACKNOWLEDGED, "PurchaseOrder", poId, env);
    }

    @Transactional
    public String submitAsn(String supplierId, SubmitAsnRequest req) {
        Supplier s = supplierRepository.findById(supplierId)
                .orElseThrow(() -> new ResourceNotFoundException("Supplier", supplierId));

        String asnNumber = "ASN-" + System.currentTimeMillis();
        Asn asn = new Asn();
        asn.setId(UUID.randomUUID().toString());
        asn.setAsnNumber(asnNumber);
        asn.setSupplier(s);
        asn.setPoId(req.poId());
        asn.setExpectedDeliveryDate(req.expectedDeliveryDate());
        asn.setStatus("SUBMITTED");
        asn.setCreatedAt(Instant.now());

        for (var line : req.lines()) {
            asn.getLines().add(new AsnLine(UUID.randomUUID().toString(), asn, line.productId(), line.quantity()));
        }

        asnRepository.save(asn);

        var payload = Map.of(
            "asnId", asn.getId(),
            "asnNumber", asn.getAsnNumber(),
            "supplierId", supplierId,
            "poId", req.poId(),
            "expectedDeliveryDate", req.expectedDeliveryDate().toString()
        );
        EventEnvelope<?> env = EventEnvelope.of(EventTypes.SUPPLIER_ASN_SUBMITTED, "supplier-service", payload);
        outboxService.publish(EventTypes.EXCHANGE_SUPPLIER, EventTypes.SUPPLIER_ASN_SUBMITTED, "ASN", asn.getId(), env);

        return asn.getId();
    }

    private SupplierDto mapToDto(Supplier s) {
        BigDecimal score = s.getScorecard() != null ? s.getScorecard().getOverallScore() : BigDecimal.ZERO;
        return new SupplierDto(
            s.getId(),
            s.getCode(),
            s.getLegalName(),
            s.getTaxId(),
            s.getStatus(),
            score
        );
    }
}
