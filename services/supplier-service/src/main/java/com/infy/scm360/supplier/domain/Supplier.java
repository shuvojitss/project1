package com.infy.scm360.supplier.domain;

import jakarta.persistence.*;
import java.time.Instant;
import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "suppliers")
public class Supplier {

    @Id
    @Column(name = "id", length = 36)
    private String id;

    @Column(name = "code", length = 32, nullable = false, unique = true)
    private String code;

    @Column(name = "legal_name", length = 128, nullable = false)
    private String legalName;

    @Column(name = "tax_id", length = 64, nullable = false)
    private String taxId;

    @Column(name = "status", length = 20, nullable = false)
    private String status = "PENDING";

    @Version
    @Column(name = "version")
    private Long version = 0L;

    @Column(name = "created_at", nullable = false)
    private Instant createdAt;

    @Column(name = "created_by", length = 64, nullable = false)
    private String createdBy;

    @Column(name = "updated_at", nullable = false)
    private Instant updatedAt;

    @Column(name = "updated_by", length = 64, nullable = false)
    private String updatedBy;

    @Column(name = "deleted_at")
    private Instant deletedAt;

    @OneToMany(mappedBy = "supplier", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    private Set<SupplierContact> contacts = new HashSet<>();

    @OneToOne(mappedBy = "supplier", cascade = CascadeType.ALL, fetch = FetchType.EAGER)
    private SupplierScorecard scorecard;

    public Supplier() {}

    public String getId() { return id; }
    public void setId(String id) { this.id = id; }
    public String getCode() { return code; }
    public void setCode(String code) { this.code = code; }
    public String getLegalName() { return legalName; }
    public void setLegalName(String legalName) { this.legalName = legalName; }
    public String getTaxId() { return taxId; }
    public void setTaxId(String taxId) { this.taxId = taxId; }
    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
    public Long getVersion() { return version; }
    public void setVersion(Long version) { this.version = version; }
    public Instant getCreatedAt() { return createdAt; }
    public void setCreatedAt(Instant createdAt) { this.createdAt = createdAt; }
    public String getCreatedBy() { return createdBy; }
    public void setCreatedBy(String createdBy) { this.createdBy = createdBy; }
    public Instant getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(Instant updatedAt) { this.updatedAt = updatedAt; }
    public String getUpdatedBy() { return updatedBy; }
    public void setUpdatedBy(String updatedBy) { this.updatedBy = updatedBy; }
    public Instant getDeletedAt() { return deletedAt; }
    public void setDeletedAt(Instant deletedAt) { this.deletedAt = deletedAt; }
    public Set<SupplierContact> getContacts() { return contacts; }
    public void setContacts(Set<SupplierContact> contacts) { this.contacts = contacts; }
    public SupplierScorecard getScorecard() { return scorecard; }
    public void setScorecard(SupplierScorecard scorecard) { this.scorecard = scorecard; }
}
