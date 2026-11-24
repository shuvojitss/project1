package com.infy.scm360.transport.domain;

import jakarta.persistence.*;
import java.time.Instant;
import java.util.UUID;

@Entity
@Table(name = "proof_of_delivery")
public class ProofOfDelivery {

    @Id
    @Column(name = "id", length = 36)
    private String id;

    @Column(name = "shipment_id", nullable = false, unique = true, length = 36)
    private String shipmentId;

    @Column(name = "recipient_name", nullable = false, length = 128)
    private String recipientName;

    @Column(name = "signature_data", columnDefinition = "TEXT")
    private String signatureData;

    @Column(name = "photo_url", length = 512)
    private String photoUrl;

    @Column(name = "notes", length = 512)
    private String notes;

    @Column(name = "delivered_at", nullable = false)
    private Instant deliveredAt = Instant.now();

    public ProofOfDelivery() {
        this.id = UUID.randomUUID().toString();
    }

    public ProofOfDelivery(String shipmentId, String recipientName, String signatureData, String photoUrl, String notes) {
        this();
        this.shipmentId = shipmentId;
        this.recipientName = recipientName;
        this.signatureData = signatureData;
        this.photoUrl = photoUrl;
        this.notes = notes;
        this.deliveredAt = Instant.now();
    }

    public String getId() { return id; }
    public void setId(String id) { this.id = id; }
    public String getShipmentId() { return shipmentId; }
    public void setShipmentId(String shipmentId) { this.shipmentId = shipmentId; }
    public String getRecipientName() { return recipientName; }
    public void setRecipientName(String recipientName) { this.recipientName = recipientName; }
    public String getSignatureData() { return signatureData; }
    public void setSignatureData(String signatureData) { this.signatureData = signatureData; }
    public String getPhotoUrl() { return photoUrl; }
    public void setPhotoUrl(String photoUrl) { this.photoUrl = photoUrl; }
    public String getNotes() { return notes; }
    public void setNotes(String notes) { this.notes = notes; }
    public Instant getDeliveredAt() { return deliveredAt; }
    public void setDeliveredAt(Instant deliveredAt) { this.deliveredAt = deliveredAt; }
}
