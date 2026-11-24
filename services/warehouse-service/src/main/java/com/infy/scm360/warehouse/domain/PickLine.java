package com.infy.scm360.warehouse.domain;

import jakarta.persistence.*;
import java.math.BigDecimal;

@Entity
@Table(name = "pick_lines")
public class PickLine {

    @Id
    @Column(name = "id", length = 36)
    private String id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "pick_list_id", nullable = false)
    private PickList pickList;

    @Column(name = "product_id", length = 36, nullable = false)
    private String productId;

    @Column(name = "bin_id", length = 36, nullable = false)
    private String binId;

    @Column(name = "quantity", precision = 19, scale = 3, nullable = false)
    private BigDecimal quantity;

    @Column(name = "picked_qty", precision = 19, scale = 3, nullable = false)
    private BigDecimal pickedQty = BigDecimal.ZERO;

    @Column(name = "status", length = 20, nullable = false)
    private String status = "PENDING"; // PENDING, PICKED

    public PickLine() {}

    public String getId() { return id; }
    public void setId(String id) { this.id = id; }
    public PickList getPickList() { return pickList; }
    public void setPickList(PickList pickList) { this.pickList = pickList; }
    public String getProductId() { return productId; }
    public void setProductId(String productId) { this.productId = productId; }
    public String getBinId() { return binId; }
    public void setBinId(String binId) { this.binId = binId; }
    public BigDecimal getQuantity() { return quantity; }
    public void setQuantity(BigDecimal quantity) { this.quantity = quantity; }
    public BigDecimal getPickedQty() { return pickedQty; }
    public void setPickedQty(BigDecimal pickedQty) { this.pickedQty = pickedQty; }
    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
}
