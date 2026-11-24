package com.infy.scm360.analytics.domain;

import jakarta.persistence.*;
import java.math.BigDecimal;

@Entity
@Table(name = "dim_supplier")
public class DimSupplier {

    @Id
    @Column(name = "id", length = 36)
    private String id;

    @Column(name = "supplier_code", nullable = false, unique = true, length = 64)
    private String supplierCode;

    @Column(name = "name", nullable = false)
    private String name;

    @Column(name = "tier", nullable = false, length = 32)
    private String tier;

    @Column(name = "rating", nullable = false, precision = 3, scale = 2)
    private BigDecimal rating = BigDecimal.valueOf(5.00);

    public DimSupplier() {}

    public DimSupplier(String id, String supplierCode, String name, String tier, BigDecimal rating) {
        this.id = id;
        this.supplierCode = supplierCode;
        this.name = name;
        this.tier = tier;
        this.rating = rating;
    }

    public String getId() { return id; }
    public void setId(String id) { this.id = id; }
    public String getSupplierCode() { return supplierCode; }
    public void setSupplierCode(String supplierCode) { this.supplierCode = supplierCode; }
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    public String getTier() { return tier; }
    public void setTier(String tier) { this.tier = tier; }
    public BigDecimal getRating() { return rating; }
    public void setRating(BigDecimal rating) { this.rating = rating; }
}
