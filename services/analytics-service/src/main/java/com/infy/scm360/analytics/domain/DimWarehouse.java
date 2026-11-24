package com.infy.scm360.analytics.domain;

import jakarta.persistence.*;

@Entity
@Table(name = "dim_warehouse")
public class DimWarehouse {

    @Id
    @Column(name = "id", length = 36)
    private String id;

    @Column(name = "code", nullable = false, unique = true, length = 64)
    private String code;

    @Column(name = "name", nullable = false)
    private String name;

    @Column(name = "region", nullable = false, length = 64)
    private String region;

    @Column(name = "total_bins", nullable = false)
    private int totalBins;

    @Column(name = "active", nullable = false)
    private boolean active = true;

    public DimWarehouse() {}

    public DimWarehouse(String id, String code, String name, String region, int totalBins, boolean active) {
        this.id = id;
        this.code = code;
        this.name = name;
        this.region = region;
        this.totalBins = totalBins;
        this.active = active;
    }

    public String getId() { return id; }
    public void setId(String id) { this.id = id; }
    public String getCode() { return code; }
    public void setCode(String code) { this.code = code; }
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    public String getRegion() { return region; }
    public void setRegion(String region) { this.region = region; }
    public int getTotalBins() { return totalBins; }
    public void setTotalBins(int totalBins) { this.totalBins = totalBins; }
    public boolean isActive() { return active; }
    public void setActive(boolean active) { this.active = active; }
}
