package com.infy.scm360.transport.domain;

import jakarta.persistence.*;
import java.math.BigDecimal;
import java.util.UUID;

@Entity
@Table(name = "rate_card_entries")
public class RateCardEntry {

    @Id
    @Column(name = "id", length = 36)
    private String id;

    @Column(name = "carrier_id", nullable = false, length = 36)
    private String carrierId;

    @Column(name = "origin_region", nullable = false, length = 64)
    private String originRegion;

    @Column(name = "destination_region", nullable = false, length = 64)
    private String destinationRegion;

    @Column(name = "min_weight_kg", nullable = false, precision = 10, scale = 2)
    private BigDecimal minWeightKg = BigDecimal.ZERO;

    @Column(name = "max_weight_kg", nullable = false, precision = 10, scale = 2)
    private BigDecimal maxWeightKg;

    @Column(name = "base_rate", nullable = false, precision = 10, scale = 2)
    private BigDecimal baseRate;

    @Column(name = "per_kg_rate", nullable = false, precision = 10, scale = 2)
    private BigDecimal perKgRate = BigDecimal.ZERO;

    @Column(name = "currency", nullable = false, length = 10)
    private String currency = "USD";

    @Column(name = "transit_days", nullable = false)
    private int transitDays = 3;

    public RateCardEntry() {
        this.id = UUID.randomUUID().toString();
    }

    public BigDecimal calculateCost(BigDecimal weightKg) {
        BigDecimal billableWeight = weightKg.max(minWeightKg);
        return baseRate.add(billableWeight.multiply(perKgRate));
    }

    public String getId() { return id; }
    public void setId(String id) { this.id = id; }
    public String getCarrierId() { return carrierId; }
    public void setCarrierId(String carrierId) { this.carrierId = carrierId; }
    public String getOriginRegion() { return originRegion; }
    public void setOriginRegion(String originRegion) { this.originRegion = originRegion; }
    public String getDestinationRegion() { return destinationRegion; }
    public void setDestinationRegion(String destinationRegion) { this.destinationRegion = destinationRegion; }
    public BigDecimal getMinWeightKg() { return minWeightKg; }
    public void setMinWeightKg(BigDecimal minWeightKg) { this.minWeightKg = minWeightKg; }
    public BigDecimal getMaxWeightKg() { return maxWeightKg; }
    public void setMaxWeightKg(BigDecimal maxWeightKg) { this.maxWeightKg = maxWeightKg; }
    public BigDecimal getBaseRate() { return baseRate; }
    public void setBaseRate(BigDecimal baseRate) { this.baseRate = baseRate; }
    public BigDecimal getPerKgRate() { return perKgRate; }
    public void setPerKgRate(BigDecimal perKgRate) { this.perKgRate = perKgRate; }
    public String getCurrency() { return currency; }
    public void setCurrency(String currency) { this.currency = currency; }
    public int getTransitDays() { return transitDays; }
    public void setTransitDays(int transitDays) { this.transitDays = transitDays; }
}
