package com.infy.scm360.inventory.domain;

import jakarta.persistence.*;

@Entity
@Table(name = "product_ref")
public class ProductRef {

    @Id
    @Column(name = "product_id", length = 36)
    private String productId;

    @Column(name = "sku", length = 64, nullable = false)
    private String sku;

    @Column(name = "name", length = 128, nullable = false)
    private String name;

    @Column(name = "uom", length = 16, nullable = false)
    private String uom;

    @Column(name = "status", length = 20, nullable = false)
    private String status;

    @Column(name = "version")
    private Long version = 0L;

    public ProductRef() {}

    public ProductRef(String productId, String sku, String name, String uom, String status, Long version) {
        this.productId = productId;
        this.sku = sku;
        this.name = name;
        this.uom = uom;
        this.status = status;
        this.version = version;
    }

    public String getProductId() { return productId; }
    public void setProductId(String productId) { this.productId = productId; }
    public String getSku() { return sku; }
    public void setSku(String sku) { this.sku = sku; }
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    public String getUom() { return uom; }
    public void setUom(String uom) { this.uom = uom; }
    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
    public Long getVersion() { return version; }
    public void setVersion(Long version) { this.version = version; }
}
