package com.infy.scm360.warehouse.domain;

import jakarta.persistence.*;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "inbound_receipts")
public class InboundReceipt {

    @Id
    @Column(name = "id", length = 36)
    private String id;

    @Column(name = "receipt_number", length = 64, nullable = false, unique = true)
    private String receiptNumber;

    @Column(name = "po_id", length = 36, nullable = false)
    private String poId;

    @Column(name = "supplier_id", length = 36, nullable = false)
    private String supplierId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "warehouse_id", nullable = false)
    private Warehouse warehouse;

    @Column(name = "status", length = 20, nullable = false)
    private String status = "EXPECTED";

    @Column(name = "received_at")
    private Instant receivedAt;

    @Column(name = "created_at", nullable = false)
    private Instant createdAt;

    @OneToMany(mappedBy = "receipt", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.EAGER)
    private List<ReceiptLine> lines = new ArrayList<>();

    public InboundReceipt() {}

    public String getId() { return id; }
    public void setId(String id) { this.id = id; }
    public String getReceiptNumber() { return receiptNumber; }
    public void setReceiptNumber(String receiptNumber) { this.receiptNumber = receiptNumber; }
    public String getPoId() { return poId; }
    public void setPoId(String poId) { this.poId = poId; }
    public String getSupplierId() { return supplierId; }
    public void setSupplierId(String supplierId) { this.supplierId = supplierId; }
    public Warehouse getWarehouse() { return warehouse; }
    public void setWarehouse(Warehouse warehouse) { this.warehouse = warehouse; }
    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
    public Instant getReceivedAt() { return receivedAt; }
    public void setReceivedAt(Instant receivedAt) { this.receivedAt = receivedAt; }
    public Instant getCreatedAt() { return createdAt; }
    public void setCreatedAt(Instant createdAt) { this.createdAt = createdAt; }
    public List<ReceiptLine> getLines() { return lines; }
    public void setLines(List<ReceiptLine> lines) { this.lines = lines; }
}
