package com.infy.scm360.analytics.domain;

import jakarta.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.UUID;

@Entity
@Table(name = "fact_stock_snapshot")
public class FactStockSnapshot {

    @Id
    @Column(name = "id", length = 36)
    private String id;

    @Column(name = "snapshot_date", nullable = false)
    private LocalDate snapshotDate;

    @Column(name = "warehouse_id", nullable = false, length = 36)
    private String warehouseId;

    @Column(name = "total_inventory_value", nullable = false, precision = 14, scale = 2)
    private BigDecimal totalInventoryValue;

    @Column(name = "total_cogs", nullable = false, precision = 14, scale = 2)
    private BigDecimal totalCogs;

    @Column(name = "occupied_bins", nullable = false)
    private int occupiedBins;

    @Column(name = "total_bins", nullable = false)
    private int totalBins;

    public FactStockSnapshot() {
        this.id = UUID.randomUUID().toString();
    }

    public String getId() { return id; }
    public void setId(String id) { this.id = id; }
    public LocalDate getSnapshotDate() { return snapshotDate; }
    public void setSnapshotDate(LocalDate snapshotDate) { this.snapshotDate = snapshotDate; }
    public String getWarehouseId() { return warehouseId; }
    public void setWarehouseId(String warehouseId) { this.warehouseId = warehouseId; }
    public BigDecimal getTotalInventoryValue() { return totalInventoryValue; }
    public void setTotalInventoryValue(BigDecimal totalInventoryValue) { this.totalInventoryValue = totalInventoryValue; }
    public BigDecimal getTotalCogs() { return totalCogs; }
    public void setTotalCogs(BigDecimal totalCogs) { this.totalCogs = totalCogs; }
    public int getOccupiedBins() { return occupiedBins; }
    public void setOccupiedBins(int occupiedBins) { this.occupiedBins = occupiedBins; }
    public int getTotalBins() { return totalBins; }
    public void setTotalBins(int totalBins) { this.totalBins = totalBins; }
}
