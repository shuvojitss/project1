package com.infy.scm360.supplier.domain;

import jakarta.persistence.*;
import java.time.Instant;

@Entity
@Table(name = "supplier_contacts")
public class SupplierContact {

    @Id
    @Column(name = "id", length = 36)
    private String id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "supplier_id", nullable = false)
    private Supplier supplier;

    @Column(name = "name", length = 128, nullable = false)
    private String name;

    @Column(name = "email", length = 128, nullable = false)
    private String email;

    @Column(name = "phone", length = 32)
    private String phone;

    @Column(name = "role", length = 64)
    private String role;

    @Column(name = "created_at", nullable = false)
    private Instant createdAt;

    public SupplierContact() {}

    public String getId() { return id; }
    public void setId(String id) { this.id = id; }
    public Supplier getSupplier() { return supplier; }
    public void setSupplier(Supplier supplier) { this.supplier = supplier; }
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }
    public String getPhone() { return phone; }
    public void setPhone(String phone) { this.phone = phone; }
    public String getRole() { return role; }
    public void setRole(String role) { this.role = role; }
    public Instant getCreatedAt() { return createdAt; }
    public void setCreatedAt(Instant createdAt) { this.createdAt = createdAt; }
}
