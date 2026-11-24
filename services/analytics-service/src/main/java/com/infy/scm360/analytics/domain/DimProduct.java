package com.infy.scm360.analytics.domain;

import jakarta.persistence.*;
import java.math.BigDecimal;

@Entity
@Table(name = "dim_product")
public class DimProduct {

    @Id
    @Column(name = "id", length = 36)
    private String id;

    @Column(name = "sku", nullable = false, unique = true, length = 64)
    private String sku;

    @Column(name = "name", nullable = false)
    private String name;

    @Column(name = "category_name", nullable = false, length = 128)
    private String categoryName;

    @Column(name = "unit_cost", nullable = false, precision = 12, scale = 2)
    private BigDecimal unitCost;

    @Column(name = "active", nullable = false)
    private boolean active = true;

    public DimProduct() {}

    public DimProduct(String id, String sku, String name, String categoryName, BigDecimal unitCost, boolean active) {
        this.id = id;
        this.sku = sku;
        this.name = name;
        this.categoryName = categoryName;
        this.unitCost = unitCost;
        this.active = active;
    }

    public String getId() { return id; }
    public void setId(String id) { this.id = id; }
    public String getSku() { return sku; }
    public void setSku(String sku) { this.sku = sku; }
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    public String getCategoryName() { return categoryName; }
    public void setCategoryName(String categoryName) { this.categoryName = categoryName; }
    public BigDecimal getUnitCost() { return unitCost; }
    public void setUnitCost(BigDecimal unitCost) { this.unitCost = unitCost; }
    public boolean isActive() { return active; }
    public void setActive(boolean active) { this.active = active; }
}
