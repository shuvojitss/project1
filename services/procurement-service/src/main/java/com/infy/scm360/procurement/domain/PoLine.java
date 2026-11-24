package com.infy.scm360.procurement.domain;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import java.math.BigDecimal;
import java.util.UUID;

@Entity
@Table(name = "po_lines")
public class PoLine {

    @Id
    @Column(name = "id", length = 36)
    private String id;

    @JsonIgnore
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "po_id", nullable = false)
    private PurchaseOrder purchaseOrder;

    @Column(name = "product_id", nullable = false, length = 36)
    private String productId;

    @Column(name = "product_sku", nullable = false, length = 64)
    private String productSku;

    @Column(name = "product_name", nullable = false, length = 255)
    private String productName;

    @Column(name = "ordered_quantity", nullable = false, precision = 12, scale = 3)
    private BigDecimal orderedQuantity;

    @Column(name = "received_quantity", nullable = false, precision = 12, scale = 3)
    private BigDecimal receivedQuantity = BigDecimal.ZERO;

    @Column(name = "unit_price", nullable = false, precision = 12, scale = 2)
    private BigDecimal unitPrice;

    @Column(name = "line_total", nullable = false, precision = 12, scale = 2)
    private BigDecimal lineTotal;

    public PoLine() {
        this.id = UUID.randomUUID().toString();
    }

    public PoLine(String productId, String productSku, String productName, BigDecimal orderedQuantity, BigDecimal unitPrice) {
        this();
        this.productId = productId;
        this.productSku = productSku;
        this.productName = productName;
        this.orderedQuantity = orderedQuantity;
        this.unitPrice = unitPrice;
        this.lineTotal = orderedQuantity.multiply(unitPrice);
        this.receivedQuantity = BigDecimal.ZERO;
    }

    public String getId() { return id; }
    public void setId(String id) { this.id = id; }
    public PurchaseOrder getPurchaseOrder() { return purchaseOrder; }
    public void setPurchaseOrder(PurchaseOrder purchaseOrder) { this.purchaseOrder = purchaseOrder; }
    public String getProductId() { return productId; }
    public void setProductId(String productId) { this.productId = productId; }
    public String getProductSku() { return productSku; }
    public void setProductSku(String productSku) { this.productSku = productSku; }
    public String getProductName() { return productName; }
    public void setProductName(String productName) { this.productName = productName; }
    public BigDecimal getOrderedQuantity() { return orderedQuantity; }
    public void setOrderedQuantity(BigDecimal orderedQuantity) { this.orderedQuantity = orderedQuantity; }
    public BigDecimal getReceivedQuantity() { return receivedQuantity; }
    public void setReceivedQuantity(BigDecimal receivedQuantity) { this.receivedQuantity = receivedQuantity; }
    public BigDecimal getUnitPrice() { return unitPrice; }
    public void setUnitPrice(BigDecimal unitPrice) { this.unitPrice = unitPrice; }
    public BigDecimal getLineTotal() { return lineTotal; }
    public void setLineTotal(BigDecimal lineTotal) { this.lineTotal = lineTotal; }
}
