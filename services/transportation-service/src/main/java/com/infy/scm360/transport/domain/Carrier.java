package com.infy.scm360.transport.domain;

import jakarta.persistence.*;
import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;

@Entity
@Table(name = "carriers")
public class Carrier {

    @Id
    @Column(name = "id", length = 36)
    private String id;

    @Column(name = "carrier_code", nullable = false, unique = true, length = 64)
    private String carrierCode;

    @Column(name = "name", nullable = false)
    private String name;

    @Column(name = "carrier_type", nullable = false, length = 64)
    private String carrierType;

    @Column(name = "contact_email", length = 128)
    private String contactEmail;

    @Column(name = "contact_phone", length = 64)
    private String contactPhone;

    @Column(name = "service_level", nullable = false, length = 64)
    private String serviceLevel = "STANDARD";

    @Column(name = "active", nullable = false)
    private boolean active = true;

    @Column(name = "rating", nullable = false, precision = 3, scale = 2)
    private BigDecimal rating = BigDecimal.valueOf(5.00);

    @Column(name = "created_at", nullable = false)
    private Instant createdAt = Instant.now();

    public Carrier() {
        this.id = UUID.randomUUID().toString();
    }

    public Carrier(String carrierCode, String name, String carrierType, String serviceLevel) {
        this();
        this.carrierCode = carrierCode;
        this.name = name;
        this.carrierType = carrierType;
        this.serviceLevel = serviceLevel;
    }

    public String getId() { return id; }
    public void setId(String id) { this.id = id; }
    public String getCarrierCode() { return carrierCode; }
    public void setCarrierCode(String carrierCode) { this.carrierCode = carrierCode; }
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    public String getCarrierType() { return carrierType; }
    public void setCarrierType(String carrierType) { this.carrierType = carrierType; }
    public String getContactEmail() { return contactEmail; }
    public void setContactEmail(String contactEmail) { this.contactEmail = contactEmail; }
    public String getContactPhone() { return contactPhone; }
    public void setContactPhone(String contactPhone) { this.contactPhone = contactPhone; }
    public String getServiceLevel() { return serviceLevel; }
    public void setServiceLevel(String serviceLevel) { this.serviceLevel = serviceLevel; }
    public boolean isActive() { return active; }
    public void setActive(boolean active) { this.active = active; }
    public BigDecimal getRating() { return rating; }
    public void setRating(BigDecimal rating) { this.rating = rating; }
    public Instant getCreatedAt() { return createdAt; }
    public void setCreatedAt(Instant createdAt) { this.createdAt = createdAt; }
}
