package com.infy.scm360.warehouse.domain;

import jakarta.persistence.*;
import java.math.BigDecimal;

@Entity
@Table(name = "cycle_count_lines")
public class CycleCountLine {

    @Id
    @Column(name = "id", length = 36)
    private String id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "cycle_count_id", nullable = false)
    private CycleCount cycleCount;

    @Column(name = "product_id", length = 36, nullable = false)
    private String productId;

    @Column(name = "bin_id", length = 36, nullable = false)
    private String binId;

    @Column(name = "system_qty", precision = 19, scale = 3, nullable = false)
    private BigDecimal systemQty;

    @Column(name = "counted_qty", precision = 19, scale = 3, nullable = false)
    private BigDecimal countedQty;

    @Column(name = "variance", precision = 19, scale = 3, nullable = false)
    private BigDecimal variance;

    public CycleCountLine() {}

    public CycleCountLine(String id, CycleCount cycleCount, String productId, String binId,
                          BigDecimal systemQty, BigDecimal countedQty) {
        this.id = id;
        this.cycleCount = cycleCount;
        this.productId = productId;
        this.binId = binId;
        this.systemQty = systemQty;
        this.countedQty = countedQty;
        this.variance = countedQty.subtract(systemQty);
    }

    public String getId() { return id; }
    public void setId(String id) { this.id = id; }
    public CycleCount getCycleCount() { return cycleCount; }
    public void setCycleCount(CycleCount cycleCount) { this.cycleCount = cycleCount; }
    public String getProductId() { return productId; }
    public void setProductId(String productId) { this.productId = productId; }
    public String getBinId() { return binId; }
    public void setBinId(String binId) { this.binId = binId; }
    public BigDecimal getSystemQty() { return systemQty; }
    public void setSystemQty(BigDecimal systemQty) { this.systemQty = systemQty; }
    public BigDecimal getCountedQty() { return countedQty; }
    public void setCountedQty(BigDecimal countedQty) { this.countedQty = countedQty; }
    public BigDecimal getVariance() { return variance; }
    public void setVariance(BigDecimal variance) { this.variance = variance; }
}
