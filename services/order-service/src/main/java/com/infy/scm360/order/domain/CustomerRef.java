package com.infy.scm360.order.domain;

import jakarta.persistence.*;
import java.time.Instant;
import java.util.UUID;

@Entity
@Table(name = "customer_ref")
public class CustomerRef {

    @Id
    @Column(name = "id", length = 36)
    private String id;

    @Column(name = "customer_code", nullable = false, unique = true, length = 64)
    private String customerCode;

    @Column(name = "name", nullable = false)
    private String name;

    @Column(name = "email", nullable = false, length = 128)
    private String email;

    @Column(name = "phone", length = 64)
    private String phone;

    @Column(name = "shipping_address", nullable = false, columnDefinition = "TEXT")
    private String shippingAddress;

    @Column(name = "billing_address", columnDefinition = "TEXT")
    private String billingAddress;

    @Column(name = "created_at", nullable = false)
    private Instant createdAt = Instant.now();

    public CustomerRef() {
        this.id = UUID.randomUUID().toString();
    }

    public CustomerRef(String customerCode, String name, String email, String phone, String shippingAddress, String billingAddress) {
        this();
        this.customerCode = customerCode;
        this.name = name;
        this.email = email;
        this.phone = phone;
        this.shippingAddress = shippingAddress;
        this.billingAddress = billingAddress;
    }

    public String getId() { return id; }
    public void setId(String id) { this.id = id; }
    public String getCustomerCode() { return customerCode; }
    public void setCustomerCode(String customerCode) { this.customerCode = customerCode; }
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }
    public String getPhone() { return phone; }
    public void setPhone(String phone) { this.phone = phone; }
    public String getShippingAddress() { return shippingAddress; }
    public void setShippingAddress(String shippingAddress) { this.shippingAddress = shippingAddress; }
    public String getBillingAddress() { return billingAddress; }
    public void setBillingAddress(String billingAddress) { this.billingAddress = billingAddress; }
    public Instant getCreatedAt() { return createdAt; }
    public void setCreatedAt(Instant createdAt) { this.createdAt = createdAt; }
}
