package com.infy.scm360.procurement.domain;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import java.math.BigDecimal;
import java.util.UUID;

@Entity
@Table(name = "requisition_lines")
public class RequisitionLine {

    @Id
    @Column(name = "id", length = 36)
    private String id;

    @JsonIgnore
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "requisition_id", nullable = false)
    private Requisition requisition;

    @Column(name = "product_id", nullable = false, length = 36)
    private String productId;

    @Column(name = "product_sku", nullable = false, length = 64)
    private String productSku;

    @Column(name = "product_name", nullable = false, length = 255)
    private String productName;

    @Column(name = "quantity", nullable = false, precision = 12, scale = 3)
    private BigDecimal quantity;

    @Column(name = "estimated_unit_price", nullable = false, precision = 12, scale = 2)
    private BigDecimal estimatedUnitPrice;

    @Column(name = "line_total", nullable = false, precision = 12, scale = 2)
    private BigDecimal lineTotal;

    public RequisitionLine() {
        this.id = UUID.randomUUID().toString();
    }

    public RequisitionLine(String productId, String productSku, String productName, BigDecimal quantity, BigDecimal estimatedUnitPrice) {
        this();
        this.productId = productId;
        this.productSku = productSku;
        this.productName = productName;
        this.quantity = quantity;
        this.estimatedUnitPrice = estimatedUnitPrice;
        this.lineTotal = quantity.multiply(estimatedUnitPrice);
    }

    public String getId() { return id; }
    public void setId(String id) { this.id = id; }
    public Requisition getRequisition() { return requisition; }
    public void setRequisition(Requisition requisition) { this.requisition = requisition; }
    public String getProductId() { return productId; }
    public void setProductId(String productId) { this.productId = productId; }
    public String getProductSku() { return productSku; }
    public void setProductSku(String productSku) { this.productSku = productSku; }
    public String getProductName() { return productName; }
    public void setProductName(String productName) { this.productName = productName; }
    public BigDecimal getQuantity() { return quantity; }
    public void setQuantity(BigDecimal quantity) { this.quantity = quantity; }
    public BigDecimal getEstimatedUnitPrice() { return estimatedUnitPrice; }
    public void setEstimatedUnitPrice(BigDecimal estimatedUnitPrice) { this.estimatedUnitPrice = estimatedUnitPrice; }
    public BigDecimal getLineTotal() { return lineTotal; }
    public void setLineTotal(BigDecimal lineTotal) { this.lineTotal = lineTotal; }
}
