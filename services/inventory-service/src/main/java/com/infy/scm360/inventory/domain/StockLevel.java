package com.infy.scm360.inventory.domain;

import jakarta.persistence.*;
import java.math.BigDecimal;
import java.time.Instant;

@Entity
@Table(name = "stock_level", uniqueConstraints = {
    @UniqueConstraint(name = "uq_stock", columnNames = {"product_id", "warehouse_id", "bin_id"})
})
public class StockLevel {

    @Id
    @Column(name = "id", length = 36)
    private String id;

    @Column(name = "product_id", length = 36, nullable = false)
    private String productId;

    @Column(name = "warehouse_id", length = 36, nullable = false)
    private String warehouseId;

    @Column(name = "bin_id", length = 36)
    private String binId;

    @Column(name = "on_hand", precision = 19, scale = 3, nullable = false)
    private BigDecimal onHand = BigDecimal.ZERO;

    @Column(name = "reserved", precision = 19, scale = 3, nullable = false)
    private BigDecimal reserved = BigDecimal.ZERO;

    @Version
    @Column(name = "version")
    private Long version = 0L;

    @Column(name = "updated_at", nullable = false)
    private Instant updatedAt;

    public StockLevel() {}

    public StockLevel(String id, String productId, String warehouseId, String binId, BigDecimal onHand, BigDecimal reserved) {
        this.id = id;
        this.productId = productId;
        this.warehouseId = warehouseId;
        this.binId = binId;
        this.onHand = onHand != null ? onHand : BigDecimal.ZERO;
        this.reserved = reserved != null ? reserved : BigDecimal.ZERO;
        this.updatedAt = Instant.now();
    }

    public BigDecimal getAvailable() {
        return onHand.subtract(reserved);
    }

    public String getId() { return id; }
    public void setId(String id) { this.id = id; }
    public String getProductId() { return productId; }
    public void setProductId(String productId) { this.productId = productId; }
    public String getWarehouseId() { return warehouseId; }
    public void setWarehouseId(String warehouseId) { this.warehouseId = warehouseId; }
    public String getBinId() { return binId; }
    public void setBinId(String binId) { this.binId = binId; }
    public BigDecimal getOnHand() { return onHand; }
    public void setOnHand(BigDecimal onHand) { this.onHand = onHand; }
    public BigDecimal getReserved() { return reserved; }
    public void setReserved(BigDecimal reserved) { this.reserved = reserved; }
    public Long getVersion() { return version; }
    public void setVersion(Long version) { this.version = version; }
    public Instant getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(Instant updatedAt) { this.updatedAt = updatedAt; }
}
