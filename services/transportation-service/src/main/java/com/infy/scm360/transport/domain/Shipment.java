package com.infy.scm360.transport.domain;

import jakarta.persistence.*;
import java.math.BigDecimal;
import java.time.Instant;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Entity
@Table(name = "shipments")
public class Shipment {

    @Id
    @Column(name = "id", length = 36)
    private String id;

    @Column(name = "shipment_number", nullable = false, unique = true, length = 64)
    private String shipmentNumber;

    @Column(name = "order_id", length = 36)
    private String orderId;

    @Column(name = "order_number", length = 64)
    private String orderNumber;

    @Column(name = "carrier_id", length = 36)
    private String carrierId;

    @Column(name = "carrier_name", length = 255)
    private String carrierName;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false, length = 32)
    private ShipmentStatus status = ShipmentStatus.PLANNED;

    @Column(name = "origin_address", nullable = false, columnDefinition = "TEXT")
    private String originAddress;

    @Column(name = "origin_region", nullable = false, length = 64)
    private String originRegion;

    @Column(name = "destination_address", nullable = false, columnDefinition = "TEXT")
    private String destinationAddress;

    @Column(name = "destination_region", nullable = false, length = 64)
    private String destinationRegion;

    @Column(name = "total_weight_kg", nullable = false, precision = 10, scale = 2)
    private BigDecimal totalWeightKg = BigDecimal.ZERO;

    @Column(name = "shipping_cost", nullable = false, precision = 10, scale = 2)
    private BigDecimal shippingCost = BigDecimal.ZERO;

    @Column(name = "currency", nullable = false, length = 10)
    private String currency = "USD";

    @Column(name = "tracking_number", length = 128)
    private String trackingNumber;

    @Column(name = "estimated_delivery_date")
    private LocalDate estimatedDeliveryDate;

    @Column(name = "actual_delivery_date")
    private Instant actualDeliveryDate;

    @Column(name = "dispatched_at")
    private Instant dispatchedAt;

    @Column(name = "created_at", nullable = false, updatable = false)
    private Instant createdAt = Instant.now();

    @Column(name = "updated_at", nullable = false)
    private Instant updatedAt = Instant.now();

    @Version
    @Column(name = "version", nullable = false)
    private Long version = 0L;

    @OneToMany(mappedBy = "shipment", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.EAGER)
    private List<ShipmentItem> items = new ArrayList<>();

    public Shipment() {
        this.id = UUID.randomUUID().toString();
    }

    public void addItem(ShipmentItem item) {
        items.add(item);
        item.setShipment(this);
        calculateWeight();
    }

    public void calculateWeight() {
        this.totalWeightKg = items.stream()
                .map(ShipmentItem::getWeightKg)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
        this.updatedAt = Instant.now();
    }

    public String getId() { return id; }
    public void setId(String id) { this.id = id; }
    public String getShipmentNumber() { return shipmentNumber; }
    public void setShipmentNumber(String shipmentNumber) { this.shipmentNumber = shipmentNumber; }
    public String getOrderId() { return orderId; }
    public void setOrderId(String orderId) { this.orderId = orderId; }
    public String getOrderNumber() { return orderNumber; }
    public void setOrderNumber(String orderNumber) { this.orderNumber = orderNumber; }
    public String getCarrierId() { return carrierId; }
    public void setCarrierId(String carrierId) { this.carrierId = carrierId; }
    public String getCarrierName() { return carrierName; }
    public void setCarrierName(String carrierName) { this.carrierName = carrierName; }
    public ShipmentStatus getStatus() { return status; }
    public void setStatus(ShipmentStatus status) { this.status = status; this.updatedAt = Instant.now(); }
    public String getOriginAddress() { return originAddress; }
    public void setOriginAddress(String originAddress) { this.originAddress = originAddress; }
    public String getOriginRegion() { return originRegion; }
    public void setOriginRegion(String originRegion) { this.originRegion = originRegion; }
    public String getDestinationAddress() { return destinationAddress; }
    public void setDestinationAddress(String destinationAddress) { this.destinationAddress = destinationAddress; }
    public String getDestinationRegion() { return destinationRegion; }
    public void setDestinationRegion(String destinationRegion) { this.destinationRegion = destinationRegion; }
    public BigDecimal getTotalWeightKg() { return totalWeightKg; }
    public void setTotalWeightKg(BigDecimal totalWeightKg) { this.totalWeightKg = totalWeightKg; }
    public BigDecimal getShippingCost() { return shippingCost; }
    public void setShippingCost(BigDecimal shippingCost) { this.shippingCost = shippingCost; }
    public String getCurrency() { return currency; }
    public void setCurrency(String currency) { this.currency = currency; }
    public String getTrackingNumber() { return trackingNumber; }
    public void setTrackingNumber(String trackingNumber) { this.trackingNumber = trackingNumber; }
    public LocalDate getEstimatedDeliveryDate() { return estimatedDeliveryDate; }
    public void setEstimatedDeliveryDate(LocalDate estimatedDeliveryDate) { this.estimatedDeliveryDate = estimatedDeliveryDate; }
    public Instant getActualDeliveryDate() { return actualDeliveryDate; }
    public void setActualDeliveryDate(Instant actualDeliveryDate) { this.actualDeliveryDate = actualDeliveryDate; }
    public Instant getDispatchedAt() { return dispatchedAt; }
    public void setDispatchedAt(Instant dispatchedAt) { this.dispatchedAt = dispatchedAt; }
    public Instant getCreatedAt() { return createdAt; }
    public void setCreatedAt(Instant createdAt) { this.createdAt = createdAt; }
    public Instant getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(Instant updatedAt) { this.updatedAt = updatedAt; }
    public Long getVersion() { return version; }
    public void setVersion(Long version) { this.version = version; }
    public List<ShipmentItem> getItems() { return items; }
    public void setItems(List<ShipmentItem> items) { this.items = items; }
}
