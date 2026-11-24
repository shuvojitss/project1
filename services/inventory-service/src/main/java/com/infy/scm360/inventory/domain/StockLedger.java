package com.infy.scm360.inventory.domain;

import jakarta.persistence.*;
import java.math.BigDecimal;
import java.time.Instant;

@Entity
@Table(name = "stock_ledger", indexes = {
    @Index(name = "idx_ledger_product_wh", columnList = "product_id, warehouse_id, occurred_at")
})
public class StockLedger {

    @Id
    @Column(name = "id", length = 36)
    private String id;

    @Column(name = "product_id", length = 36, nullable = false)
    private String productId;

    @Column(name = "warehouse_id", length = 36, nullable = false)
    private String warehouseId;

    @Column(name = "movement_type", length = 32, nullable = false)
    private String movementType; // RECEIPT, RESERVE, RELEASE, ISSUE, TRANSFER_OUT, TRANSFER_IN, ADJUSTMENT

    @Column(name = "quantity_delta", precision = 19, scale = 3, nullable = false)
    private BigDecimal quantityDelta;

    @Column(name = "reference_type", length = 32, nullable = false)
    private String referenceType;

    @Column(name = "reference_id", length = 36, nullable = false)
    private String referenceId;

    @Column(name = "occurred_at", nullable = false)
    private Instant occurredAt;

    public StockLedger() {}

    public StockLedger(String id, String productId, String warehouseId, String movementType,
                       BigDecimal quantityDelta, String referenceType, String referenceId, Instant occurredAt) {
        this.id = id;
        this.productId = productId;
        this.warehouseId = warehouseId;
        this.movementType = movementType;
        this.quantityDelta = quantityDelta;
        this.referenceType = referenceType;
        this.referenceId = referenceId;
        this.occurredAt = occurredAt != null ? occurredAt : Instant.now();
    }

    public String getId() { return id; }
    public void setId(String id) { this.id = id; }
    public String getProductId() { return productId; }
    public void setProductId(String productId) { this.productId = productId; }
    public String getWarehouseId() { return warehouseId; }
    public void setWarehouseId(String warehouseId) { this.warehouseId = warehouseId; }
    public String getMovementType() { return movementType; }
    public void setMovementType(String movementType) { this.movementType = movementType; }
    public BigDecimal getQuantityDelta() { return quantityDelta; }
    public void setQuantityDelta(BigDecimal quantityDelta) { this.quantityDelta = quantityDelta; }
    public String getReferenceType() { return referenceType; }
    public void setReferenceType(String referenceType) { this.referenceType = referenceType; }
    public String getReferenceId() { return referenceId; }
    public void setReferenceId(String referenceId) { this.referenceId = referenceId; }
    public Instant getOccurredAt() { return occurredAt; }
    public void setOccurredAt(Instant occurredAt) { this.occurredAt = occurredAt; }
}
