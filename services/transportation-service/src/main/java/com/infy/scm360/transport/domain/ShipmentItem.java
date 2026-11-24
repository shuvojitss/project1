package com.infy.scm360.transport.domain;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import java.math.BigDecimal;
import java.util.UUID;

@Entity
@Table(name = "shipment_items")
public class ShipmentItem {

    @Id
    @Column(name = "id", length = 36)
    private String id;

    @JsonIgnore
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "shipment_id", nullable = false)
    private Shipment shipment;

    @Column(name = "product_id", nullable = false, length = 36)
    private String productId;

    @Column(name = "product_sku", nullable = false, length = 64)
    private String productSku;

    @Column(name = "product_name", nullable = false, length = 255)
    private String productName;

    @Column(name = "quantity", nullable = false, precision = 12, scale = 3)
    private BigDecimal quantity;

    @Column(name = "weight_kg", nullable = false, precision = 10, scale = 2)
    private BigDecimal weightKg;

    public ShipmentItem() {
        this.id = UUID.randomUUID().toString();
    }

    public ShipmentItem(String productId, String productSku, String productName, BigDecimal quantity, BigDecimal weightKg) {
        this();
        this.productId = productId;
        this.productSku = productSku;
        this.productName = productName;
        this.quantity = quantity;
        this.weightKg = weightKg;
    }

    public String getId() { return id; }
    public void setId(String id) { this.id = id; }
    public Shipment getShipment() { return shipment; }
    public void setShipment(Shipment shipment) { this.shipment = shipment; }
    public String getProductId() { return productId; }
    public void setProductId(String productId) { this.productId = productId; }
    public String getProductSku() { return productSku; }
    public void setProductSku(String productSku) { this.productSku = productSku; }
    public String getProductName() { return productName; }
    public void setProductName(String productName) { this.productName = productName; }
    public BigDecimal getQuantity() { return quantity; }
    public void setQuantity(BigDecimal quantity) { this.quantity = quantity; }
    public BigDecimal getWeightKg() { return weightKg; }
    public void setWeightKg(BigDecimal weightKg) { this.weightKg = weightKg; }
}
