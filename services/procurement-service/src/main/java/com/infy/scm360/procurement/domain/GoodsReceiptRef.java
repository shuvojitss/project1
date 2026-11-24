package com.infy.scm360.procurement.domain;

import jakarta.persistence.*;
import java.time.Instant;
import java.util.UUID;

@Entity
@Table(name = "goods_receipt_ref")
public class GoodsReceiptRef {

    @Id
    @Column(name = "id", length = 36)
    private String id;

    @Column(name = "po_id", nullable = false, length = 36)
    private String poId;

    @Column(name = "receipt_number", nullable = false, length = 64)
    private String receiptNumber;

    @Column(name = "warehouse_id", nullable = false, length = 36)
    private String warehouseId;

    @Column(name = "received_date", nullable = false)
    private Instant receivedDate = Instant.now();

    @Column(name = "received_by", nullable = false, length = 128)
    private String receivedBy;

    public GoodsReceiptRef() {
        this.id = UUID.randomUUID().toString();
    }

    public GoodsReceiptRef(String poId, String receiptNumber, String warehouseId, String receivedBy) {
        this();
        this.poId = poId;
        this.receiptNumber = receiptNumber;
        this.warehouseId = warehouseId;
        this.receivedBy = receivedBy;
        this.receivedDate = Instant.now();
    }

    public String getId() { return id; }
    public void setId(String id) { this.id = id; }
    public String getPoId() { return poId; }
    public void setPoId(String poId) { this.poId = poId; }
    public String getReceiptNumber() { return receiptNumber; }
    public void setReceiptNumber(String receiptNumber) { this.receiptNumber = receiptNumber; }
    public String getWarehouseId() { return warehouseId; }
    public void setWarehouseId(String warehouseId) { this.warehouseId = warehouseId; }
    public Instant getReceivedDate() { return receivedDate; }
    public void setReceivedDate(Instant receivedDate) { this.receivedDate = receivedDate; }
    public String getReceivedBy() { return receivedBy; }
    public void setReceivedBy(String receivedBy) { this.receivedBy = receivedBy; }
}
