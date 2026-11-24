package com.infy.scm360.supplier.domain;

import jakarta.persistence.*;
import java.math.BigDecimal;
import java.time.Instant;

@Entity
@Table(name = "supplier_scorecards")
public class SupplierScorecard {

    @Id
    @Column(name = "id", length = 36)
    private String id;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "supplier_id", nullable = false, unique = true)
    private Supplier supplier;

    @Column(name = "overall_score", precision = 5, scale = 2, nullable = false)
    private BigDecimal overallScore = BigDecimal.valueOf(85.00);

    @Column(name = "on_time_delivery_rate", precision = 5, scale = 2, nullable = false)
    private BigDecimal onTimeDeliveryRate = BigDecimal.valueOf(90.00);

    @Column(name = "quality_score", precision = 5, scale = 2, nullable = false)
    private BigDecimal qualityScore = BigDecimal.valueOf(95.00);

    @Column(name = "lead_time_variance_score", precision = 5, scale = 2, nullable = false)
    private BigDecimal leadTimeVarianceScore = BigDecimal.valueOf(80.00);

    @Column(name = "responsiveness_score", precision = 5, scale = 2, nullable = false)
    private BigDecimal responsivenessScore = BigDecimal.valueOf(85.00);

    @Column(name = "updated_at", nullable = false)
    private Instant updatedAt;

    public SupplierScorecard() {}

    public String getId() { return id; }
    public void setId(String id) { this.id = id; }
    public Supplier getSupplier() { return supplier; }
    public void setSupplier(Supplier supplier) { this.supplier = supplier; }
    public BigDecimal getOverallScore() { return overallScore; }
    public void setOverallScore(BigDecimal overallScore) { this.overallScore = overallScore; }
    public BigDecimal getOnTimeDeliveryRate() { return onTimeDeliveryRate; }
    public void setOnTimeDeliveryRate(BigDecimal onTimeDeliveryRate) { this.onTimeDeliveryRate = onTimeDeliveryRate; }
    public BigDecimal getQualityScore() { return qualityScore; }
    public void setQualityScore(BigDecimal qualityScore) { this.qualityScore = qualityScore; }
    public BigDecimal getLeadTimeVarianceScore() { return leadTimeVarianceScore; }
    public void setLeadTimeVarianceScore(BigDecimal leadTimeVarianceScore) { this.leadTimeVarianceScore = leadTimeVarianceScore; }
    public BigDecimal getResponsivenessScore() { return responsivenessScore; }
    public void setResponsivenessScore(BigDecimal responsivenessScore) { this.responsivenessScore = responsivenessScore; }
    public Instant getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(Instant updatedAt) { this.updatedAt = updatedAt; }
}
