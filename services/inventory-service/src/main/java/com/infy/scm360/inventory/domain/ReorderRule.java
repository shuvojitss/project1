package com.infy.scm360.inventory.domain;

import jakarta.persistence.*;
import java.math.BigDecimal;

@Entity
@Table(name = "reorder_rule", uniqueConstraints = {
    @UniqueConstraint(name = "uq_reorder", columnNames = {"product_id", "warehouse_id"})
})
public class ReorderRule {

    @Id
    @Column(name = "id", length = 36)
    private String id;

    @Column(name = "product_id", length = 36, nullable = false)
    private String productId;

    @Column(name = "warehouse_id", length = 36, nullable = false)
    private String warehouseId;

    @Column(name = "min_stock", precision = 19, scale = 3, nullable = false)
    private BigDecimal minStock;

    @Column(name = "max_stock", precision = 19, scale = 3, nullable = false)
    private BigDecimal maxStock;

    @Column(name = "reorder_point", precision = 19, scale = 3, nullable = false)
    private BigDecimal reorderPoint;

    @Column(name = "safety_stock", precision = 19, scale = 3, nullable = false)
    private BigDecimal safetyStock;

    @Column(name = "preferred_supplier_id", length = 36)
    private String preferredSupplierId;

    public ReorderRule() {}

    public String getId() { return id; }
    public void setId(String id) { this.id = id; }
    public String getProductId() { return productId; }
    public void setProductId(String productId) { this.productId = productId; }
    public String getWarehouseId() { return warehouseId; }
    public void setWarehouseId(String warehouseId) { this.warehouseId = warehouseId; }
    public BigDecimal getMinStock() { return minStock; }
    public void setMinStock(BigDecimal minStock) { this.minStock = minStock; }
    public BigDecimal getMaxStock() { return maxStock; }
    public void setMaxStock(BigDecimal maxStock) { this.maxStock = maxStock; }
    public BigDecimal getReorderPoint() { return reorderPoint; }
    public void setReorderPoint(BigDecimal reorderPoint) { this.reorderPoint = reorderPoint; }
    public BigDecimal getSafetyStock() { return safetyStock; }
    public void setSafetyStock(BigDecimal safetyStock) { this.safetyStock = safetyStock; }
    public String getPreferredSupplierId() { return preferredSupplierId; }
    public void setPreferredSupplierId(String preferredSupplierId) { this.preferredSupplierId = preferredSupplierId; }
}
