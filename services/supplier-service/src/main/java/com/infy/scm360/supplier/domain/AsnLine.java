package com.infy.scm360.supplier.domain;

import jakarta.persistence.*;
import java.math.BigDecimal;

@Entity
@Table(name = "asn_lines")
public class AsnLine {

    @Id
    @Column(name = "id", length = 36)
    private String id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "asn_id", nullable = false)
    private Asn asn;

    @Column(name = "product_id", length = 36, nullable = false)
    private String productId;

    @Column(name = "quantity", precision = 19, scale = 3, nullable = false)
    private BigDecimal quantity;

    public AsnLine() {}

    public AsnLine(String id, Asn asn, String productId, BigDecimal quantity) {
        this.id = id;
        this.asn = asn;
        this.productId = productId;
        this.quantity = quantity;
    }

    public String getId() { return id; }
    public void setId(String id) { this.id = id; }
    public Asn getAsn() { return asn; }
    public void setAsn(Asn asn) { this.asn = asn; }
    public String getProductId() { return productId; }
    public void setProductId(String productId) { this.productId = productId; }
    public BigDecimal getQuantity() { return quantity; }
    public void setQuantity(BigDecimal quantity) { this.quantity = quantity; }
}
