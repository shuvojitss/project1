package com.infy.scm360.warehouse.domain;

import jakarta.persistence.*;
import java.math.BigDecimal;

@Entity
@Table(name = "receipt_lines")
public class ReceiptLine {

    @Id
    @Column(name = "id", length = 36)
    private String id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "receipt_id", nullable = false)
    private InboundReceipt receipt;

    @Column(name = "product_id", length = 36, nullable = false)
    private String productId;

    @Column(name = "expected_qty", precision = 19, scale = 3, nullable = false)
    private BigDecimal expectedQty;

    @Column(name = "accepted_qty", precision = 19, scale = 3, nullable = false)
    private BigDecimal acceptedQty = BigDecimal.ZERO;

    @Column(name = "rejected_qty", precision = 19, scale = 3, nullable = false)
    private BigDecimal rejectedQty = BigDecimal.ZERO;

    @Column(name = "damaged_qty", precision = 19, scale = 3, nullable = false)
    private BigDecimal damagedQty = BigDecimal.ZERO;

    @Column(name = "suggested_bin_id", length = 36)
    private String suggestedBinId;

    public ReceiptLine() {}

    public String getId() { return id; }
    public void setId(String id) { this.id = id; }
    public InboundReceipt getReceipt() { return receipt; }
    public void setReceipt(InboundReceipt receipt) { this.receipt = receipt; }
    public String getProductId() { return productId; }
    public void setProductId(String productId) { this.productId = productId; }
    public BigDecimal getExpectedQty() { return expectedQty; }
    public void setExpectedQty(BigDecimal expectedQty) { this.expectedQty = expectedQty; }
    public BigDecimal getAcceptedQty() { return acceptedQty; }
    public void setAcceptedQty(BigDecimal acceptedQty) { this.acceptedQty = acceptedQty; }
    public BigDecimal getRejectedQty() { return rejectedQty; }
    public void setRejectedQty(BigDecimal rejectedQty) { this.rejectedQty = rejectedQty; }
    public BigDecimal getDamagedQty() { return damagedQty; }
    public void setDamagedQty(BigDecimal damagedQty) { this.damagedQty = damagedQty; }
    public String getSuggestedBinId() { return suggestedBinId; }
    public void setSuggestedBinId(String suggestedBinId) { this.suggestedBinId = suggestedBinId; }
}
