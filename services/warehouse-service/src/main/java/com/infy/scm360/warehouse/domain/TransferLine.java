package com.infy.scm360.warehouse.domain;

import jakarta.persistence.*;
import java.math.BigDecimal;

@Entity
@Table(name = "transfer_lines")
public class TransferLine {

    @Id
    @Column(name = "id", length = 36)
    private String id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "transfer_order_id", nullable = false)
    private TransferOrder transferOrder;

    @Column(name = "product_id", length = 36, nullable = false)
    private String productId;

    @Column(name = "quantity", precision = 19, scale = 3, nullable = false)
    private BigDecimal quantity;

    public TransferLine() {}

    public TransferLine(String id, TransferOrder transferOrder, String productId, BigDecimal quantity) {
        this.id = id;
        this.transferOrder = transferOrder;
        this.productId = productId;
        this.quantity = quantity;
    }

    public String getId() { return id; }
    public void setId(String id) { this.id = id; }
    public TransferOrder getTransferOrder() { return transferOrder; }
    public void setTransferOrder(TransferOrder transferOrder) { this.transferOrder = transferOrder; }
    public String getProductId() { return productId; }
    public void setProductId(String productId) { this.productId = productId; }
    public BigDecimal getQuantity() { return quantity; }
    public void setQuantity(BigDecimal quantity) { this.quantity = quantity; }
}
