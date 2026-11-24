package com.infy.scm360.supplier.domain;

import jakarta.persistence.*;
import java.time.LocalDate;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "asns")
public class Asn {

    @Id
    @Column(name = "id", length = 36)
    private String id;

    @Column(name = "asn_number", length = 64, nullable = false, unique = true)
    private String asnNumber;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "supplier_id", nullable = false)
    private Supplier supplier;

    @Column(name = "po_id", length = 36, nullable = false)
    private String poId;

    @Column(name = "expected_delivery_date", nullable = false)
    private LocalDate expectedDeliveryDate;

    @Column(name = "status", length = 20, nullable = false)
    private String status = "SUBMITTED";

    @Column(name = "created_at", nullable = false)
    private Instant createdAt;

    @OneToMany(mappedBy = "asn", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.EAGER)
    private List<AsnLine> lines = new ArrayList<>();

    public Asn() {}

    public String getId() { return id; }
    public void setId(String id) { this.id = id; }
    public String getAsnNumber() { return asnNumber; }
    public void setAsnNumber(String asnNumber) { this.asnNumber = asnNumber; }
    public Supplier getSupplier() { return supplier; }
    public void setSupplier(Supplier supplier) { this.supplier = supplier; }
    public String getPoId() { return poId; }
    public void setPoId(String poId) { this.poId = poId; }
    public LocalDate getExpectedDeliveryDate() { return expectedDeliveryDate; }
    public void setExpectedDeliveryDate(LocalDate expectedDeliveryDate) { this.expectedDeliveryDate = expectedDeliveryDate; }
    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
    public Instant getCreatedAt() { return createdAt; }
    public void setCreatedAt(Instant createdAt) { this.createdAt = createdAt; }
    public List<AsnLine> getLines() { return lines; }
    public void setLines(List<AsnLine> lines) { this.lines = lines; }
}
