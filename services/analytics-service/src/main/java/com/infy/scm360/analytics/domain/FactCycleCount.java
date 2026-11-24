package com.infy.scm360.analytics.domain;

import jakarta.persistence.*;
import java.time.LocalDate;
import java.util.UUID;

@Entity
@Table(name = "fact_cycle_count")
public class FactCycleCount {

    @Id
    @Column(name = "id", length = 36)
    private String id;

    @Column(name = "warehouse_id", nullable = false, length = 36)
    private String warehouseId;

    @Column(name = "total_items_counted", nullable = false)
    private int totalItemsCounted;

    @Column(name = "accurate_items_counted", nullable = false)
    private int accurateItemsCounted;

    @Column(name = "count_date", nullable = false)
    private LocalDate countDate = LocalDate.now();

    public FactCycleCount() {
        this.id = UUID.randomUUID().toString();
    }

    public FactCycleCount(String warehouseId, int totalItemsCounted, int accurateItemsCounted, LocalDate countDate) {
        this();
        this.warehouseId = warehouseId;
        this.totalItemsCounted = totalItemsCounted;
        this.accurateItemsCounted = accurateItemsCounted;
        this.countDate = countDate;
    }

    public String getId() { return id; }
    public void setId(String id) { this.id = id; }
    public String getWarehouseId() { return warehouseId; }
    public void setWarehouseId(String warehouseId) { this.warehouseId = warehouseId; }
    public int getTotalItemsCounted() { return totalItemsCounted; }
    public void setTotalItemsCounted(int totalItemsCounted) { this.totalItemsCounted = totalItemsCounted; }
    public int getAccurateItemsCounted() { return accurateItemsCounted; }
    public void setAccurateItemsCounted(int accurateItemsCounted) { this.accurateItemsCounted = accurateItemsCounted; }
    public LocalDate getCountDate() { return countDate; }
    public void setCountDate(LocalDate countDate) { this.countDate = countDate; }
}
