package com.infy.scm360.procurement.domain;

import jakarta.persistence.*;
import java.math.BigDecimal;
import java.util.UUID;

@Entity
@Table(name = "approval_rules")
public class ApprovalRule {

    @Id
    @Column(name = "id", length = 36)
    private String id;

    @Column(name = "role", nullable = false, unique = true, length = 64)
    private String role;

    @Column(name = "max_amount", nullable = false, precision = 12, scale = 2)
    private BigDecimal maxAmount;

    @Column(name = "active", nullable = false)
    private boolean active = true;

    public ApprovalRule() {
        this.id = UUID.randomUUID().toString();
    }

    public ApprovalRule(String role, BigDecimal maxAmount) {
        this();
        this.role = role;
        this.maxAmount = maxAmount;
    }

    public String getId() { return id; }
    public void setId(String id) { this.id = id; }
    public String getRole() { return role; }
    public void setRole(String role) { this.role = role; }
    public BigDecimal getMaxAmount() { return maxAmount; }
    public void setMaxAmount(BigDecimal maxAmount) { this.maxAmount = maxAmount; }
    public boolean isActive() { return active; }
    public void setActive(boolean active) { this.active = active; }
}
