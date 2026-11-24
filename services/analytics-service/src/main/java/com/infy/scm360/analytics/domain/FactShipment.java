package com.infy.scm360.analytics.domain;

import jakarta.persistence.*;
import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;

@Entity
@Table(name = "fact_shipment")
public class FactShipment {

    @Id
    @Column(name = "id", length = 36)
    private String id;

    @Column(name = "shipment_id", nullable = false, unique = true, length = 36)
    private String shipmentId;

    @Column(name = "shipment_number", nullable = false, length = 64)
    private String shipmentNumber;

    @Column(name = "carrier_id", nullable = false, length = 36)
    private String carrierId;

    @Column(name = "carrier_name", nullable = false)
    private String carrierName;

    @Column(name = "origin_region", nullable = false, length = 64)
    private String originRegion;

    @Column(name = "destination_region", nullable = false, length = 64)
    private String destinationRegion;

    @Column(name = "total_weight_kg", nullable = false, precision = 10, scale = 2)
    private BigDecimal totalWeightKg = BigDecimal.ZERO;

    @Column(name = "shipping_cost", nullable = false, precision = 10, scale = 2)
    private BigDecimal shippingCost = BigDecimal.ZERO;

    @Column(name = "status", nullable = false, length = 32)
    private String status;

    @Column(name = "dispatched_at")
    private Instant dispatchedAt;

    @Column(name = "delivered_at")
    private Instant deliveredAt;

    @Column(name = "transit_days")
    private Integer transitDays;

    @Column(name = "is_on_time", nullable = false)
    private boolean onTime = true;

    @Column(name = "is_in_full", nullable = false)
    private boolean inFull = true;

    public FactShipment() {
        this.id = UUID.randomUUID().toString();
    }

    public String getId() { return id; }
    public void setId(String id) { this.id = id; }
    public String getShipmentId() { return shipmentId; }
    public void setShipmentId(String shipmentId) { this.shipmentId = shipmentId; }
    public String getShipmentNumber() { return shipmentNumber; }
    public void setShipmentNumber(String shipmentNumber) { this.shipmentNumber = shipmentNumber; }
    public String getCarrierId() { return carrierId; }
    public void setCarrierId(String carrierId) { this.carrierId = carrierId; }
    public String getCarrierName() { return carrierName; }
    public void setCarrierName(String carrierName) { this.carrierName = carrierName; }
    public String getOriginRegion() { return originRegion; }
    public void setOriginRegion(String originRegion) { this.originRegion = originRegion; }
    public String getDestinationRegion() { return destinationRegion; }
    public void setDestinationRegion(String destinationRegion) { this.destinationRegion = destinationRegion; }
    public BigDecimal getTotalWeightKg() { return totalWeightKg; }
    public void setTotalWeightKg(BigDecimal totalWeightKg) { this.totalWeightKg = totalWeightKg; }
    public BigDecimal getShippingCost() { return shippingCost; }
    public void setShippingCost(BigDecimal shippingCost) { this.shippingCost = shippingCost; }
    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
    public Instant getDispatchedAt() { return dispatchedAt; }
    public void setDispatchedAt(Instant dispatchedAt) { this.dispatchedAt = dispatchedAt; }
    public Instant getDeliveredAt() { return deliveredAt; }
    public void setDeliveredAt(Instant deliveredAt) { this.deliveredAt = deliveredAt; }
    public Integer getTransitDays() { return transitDays; }
    public void setTransitDays(Integer transitDays) { this.transitDays = transitDays; }
    public boolean isOnTime() { return onTime; }
    public void setOnTime(boolean onTime) { this.onTime = onTime; }
    public boolean isInFull() { return inFull; }
    public void setInFull(boolean inFull) { this.inFull = inFull; }
}
