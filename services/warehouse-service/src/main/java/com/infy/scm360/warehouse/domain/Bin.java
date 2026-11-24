package com.infy.scm360.warehouse.domain;

import jakarta.persistence.*;
import java.math.BigDecimal;

@Entity
@Table(name = "bins")
public class Bin {

    @Id
    @Column(name = "id", length = 36)
    private String id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "zone_id", nullable = false)
    private Zone zone;

    @Column(name = "code", length = 32, nullable = false)
    private String code;

    @Column(name = "capacity", precision = 19, scale = 3, nullable = false)
    private BigDecimal capacity = BigDecimal.valueOf(500);

    @Column(name = "status", length = 20, nullable = false)
    private String status = "EMPTY";

    public Bin() {}

    public String getId() { return id; }
    public void setId(String id) { this.id = id; }
    public Zone getZone() { return zone; }
    public void setZone(Zone zone) { this.zone = zone; }
    public String getCode() { return code; }
    public void setCode(String code) { this.code = code; }
    public BigDecimal getCapacity() { return capacity; }
    public void setCapacity(BigDecimal capacity) { this.capacity = capacity; }
    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
}
