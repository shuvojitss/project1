package com.infy.scm360.warehouse.service;

import com.infy.scm360.common.dto.PageResponse;
import com.infy.scm360.common.event.EventEnvelope;
import com.infy.scm360.common.event.EventTypes;
import com.infy.scm360.common.exception.ResourceNotFoundException;
import com.infy.scm360.common.outbox.OutboxService;
import com.infy.scm360.common.security.SecurityUtils;
import com.infy.scm360.warehouse.domain.*;
import com.infy.scm360.warehouse.dto.*;
import com.infy.scm360.warehouse.repository.*;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.*;

@Service
public class WarehouseService {

    private final WarehouseRepository warehouseRepository;
    private final BinRepository binRepository;
    private final InboundReceiptRepository receiptRepository;
    private final PickListRepository pickListRepository;
    private final TransferOrderRepository transferOrderRepository;
    private final CycleCountRepository cycleCountRepository;
    private final OutboxService outboxService;

    public WarehouseService(
            WarehouseRepository warehouseRepository,
            BinRepository binRepository,
            InboundReceiptRepository receiptRepository,
            PickListRepository pickListRepository,
            TransferOrderRepository transferOrderRepository,
            CycleCountRepository cycleCountRepository,
            OutboxService outboxService) {
        this.warehouseRepository = warehouseRepository;
        this.binRepository = binRepository;
        this.receiptRepository = receiptRepository;
        this.pickListRepository = pickListRepository;
        this.transferOrderRepository = transferOrderRepository;
        this.cycleCountRepository = cycleCountRepository;
        this.outboxService = outboxService;
    }

    @Transactional(readOnly = true)
    public PageResponse<WarehouseDto> listWarehouses(Pageable pageable) {
        Page<Warehouse> page = warehouseRepository.findAll(pageable);
        return PageResponse.from(page.map(w -> new WarehouseDto(
            w.getId(), w.getCode(), w.getName(), w.getRegion(), w.getAddress(), w.getCapacity()
        )));
    }

    @Transactional(readOnly = true)
    public WarehouseDto getWarehouseById(String id) {
        Warehouse w = warehouseRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Warehouse", id));
        return new WarehouseDto(w.getId(), w.getCode(), w.getName(), w.getRegion(), w.getAddress(), w.getCapacity());
    }

    @Transactional
    public void receiveGoods(ReceiveGoodsRequest req) {
        InboundReceipt receipt = receiptRepository.findById(req.receiptId())
                .orElseThrow(() -> new ResourceNotFoundException("Receipt", req.receiptId()));

        receipt.setStatus("COMPLETED");
        receipt.setReceivedAt(Instant.now());

        List<Map<String, Object>> acceptedLines = new ArrayList<>();
        List<Map<String, Object>> rejectedLines = new ArrayList<>();

        for (var line : req.lines()) {
            acceptedLines.add(Map.of("productId", line.productId(), "quantity", line.acceptedQty()));
            if (line.rejectedQty().compareTo(BigDecimal.ZERO) > 0) {
                rejectedLines.add(Map.of("productId", line.productId(), "quantity", line.rejectedQty()));
            }
        }

        receiptRepository.save(receipt);

        var payload = Map.of(
            "receiptId", receipt.getId(),
            "poId", receipt.getPoId(),
            "supplierId", receipt.getSupplierId(),
            "warehouseId", receipt.getWarehouse().getId(),
            "acceptedLines", acceptedLines,
            "rejectedLines", rejectedLines
        );

        EventEnvelope<?> env = EventEnvelope.of(EventTypes.WAREHOUSE_GOODS_RECEIVED, "warehouse-service", payload);
        outboxService.publish(EventTypes.EXCHANGE_WAREHOUSE, EventTypes.WAREHOUSE_GOODS_RECEIVED, "InboundReceipt", receipt.getId(), env);
    }

    @Transactional
    public void completePick(String pickListId) {
        PickList pickList = pickListRepository.findById(pickListId)
                .orElseThrow(() -> new ResourceNotFoundException("PickList", pickListId));

        pickList.setStatus("COMPLETED");
        pickList.setCompletedAt(Instant.now());
        for (PickLine line : pickList.getLines()) {
            line.setStatus("PICKED");
            line.setPickedQty(line.getQuantity());
        }
        pickListRepository.save(pickList);

        var payload = Map.of(
            "pickListId", pickList.getId(),
            "orderId", pickList.getOrderId(),
            "warehouseId", pickList.getWarehouseId(),
            "status", "PICKED"
        );
        EventEnvelope<?> env = EventEnvelope.of(EventTypes.WAREHOUSE_ORDER_PICKED, "warehouse-service", payload);
        outboxService.publish(EventTypes.EXCHANGE_WAREHOUSE, EventTypes.WAREHOUSE_ORDER_PICKED, "Order", pickList.getOrderId(), env);
    }

    @Transactional
    public void completePack(String orderId, String pickListId, String warehouseId) {
        var payload = Map.of(
            "orderId", orderId,
            "pickListId", pickListId,
            "warehouseId", warehouseId,
            "status", "PACKED"
        );
        EventEnvelope<?> env = EventEnvelope.of(EventTypes.WAREHOUSE_ORDER_PACKED, "warehouse-service", payload);
        outboxService.publish(EventTypes.EXCHANGE_WAREHOUSE, EventTypes.WAREHOUSE_ORDER_PACKED, "Order", orderId, env);
    }

    @Transactional
    public String createTransfer(CreateTransferRequest req) {
        TransferOrder order = new TransferOrder();
        order.setId(UUID.randomUUID().toString());
        order.setTransferNumber("TRF-" + System.currentTimeMillis());
        order.setSourceWarehouseId(req.sourceWarehouseId());
        order.setTargetWarehouseId(req.targetWarehouseId());
        order.setStatus("DRAFT");
        order.setCreatedAt(Instant.now());

        for (var line : req.lines()) {
            order.getLines().add(new TransferLine(UUID.randomUUID().toString(), order, line.productId(), line.quantity()));
        }

        transferOrderRepository.save(order);
        return order.getId();
    }

    @Transactional
    public void dispatchTransfer(String transferId) {
        TransferOrder order = transferOrderRepository.findById(transferId)
                .orElseThrow(() -> new ResourceNotFoundException("TransferOrder", transferId));

        order.setStatus("DISPATCHED");
        order.setDispatchedAt(Instant.now());
        transferOrderRepository.save(order);

        var payload = Map.of(
            "transferId", order.getId(),
            "transferNumber", order.getTransferNumber(),
            "sourceWarehouseId", order.getSourceWarehouseId(),
            "targetWarehouseId", order.getTargetWarehouseId()
        );
        EventEnvelope<?> env = EventEnvelope.of(EventTypes.WAREHOUSE_TRANSFER_DISPATCHED, "warehouse-service", payload);
        outboxService.publish(EventTypes.EXCHANGE_WAREHOUSE, EventTypes.WAREHOUSE_TRANSFER_DISPATCHED, "TransferOrder", order.getId(), env);
    }

    @Transactional
    public void receiveTransfer(String transferId) {
        TransferOrder order = transferOrderRepository.findById(transferId)
                .orElseThrow(() -> new ResourceNotFoundException("TransferOrder", transferId));

        order.setStatus("RECEIVED");
        order.setReceivedAt(Instant.now());
        transferOrderRepository.save(order);

        var payload = Map.of(
            "transferId", order.getId(),
            "transferNumber", order.getTransferNumber(),
            "sourceWarehouseId", order.getSourceWarehouseId(),
            "targetWarehouseId", order.getTargetWarehouseId()
        );
        EventEnvelope<?> env = EventEnvelope.of(EventTypes.WAREHOUSE_TRANSFER_RECEIVED, "warehouse-service", payload);
        outboxService.publish(EventTypes.EXCHANGE_WAREHOUSE, EventTypes.WAREHOUSE_TRANSFER_RECEIVED, "TransferOrder", order.getId(), env);
    }

    @Transactional
    public String submitCycleCount(SubmitCycleCountRequest req) {
        CycleCount count = new CycleCount();
        count.setId(UUID.randomUUID().toString());
        count.setCountNumber("CC-" + System.currentTimeMillis());
        count.setWarehouseId(req.warehouseId());
        count.setStatus("PENDING_APPROVAL");
        count.setCreatedAt(Instant.now());

        for (var line : req.lines()) {
            count.getLines().add(new CycleCountLine(
                UUID.randomUUID().toString(),
                count,
                line.productId(),
                line.binId(),
                line.systemQty(),
                line.countedQty()
            ));
        }

        cycleCountRepository.save(count);
        return count.getId();
    }

    @Transactional
    public void approveCycleCountVariance(String countId) {
        CycleCount count = cycleCountRepository.findById(countId)
                .orElseThrow(() -> new ResourceNotFoundException("CycleCount", countId));

        count.setStatus("APPROVED");
        count.setApprovedAt(Instant.now());
        cycleCountRepository.save(count);

        var payload = Map.of(
            "cycleCountId", count.getId(),
            "warehouseId", count.getWarehouseId(),
            "status", "APPROVED"
        );
        EventEnvelope<?> env = EventEnvelope.of(EventTypes.WAREHOUSE_COUNT_VARIANCE_APPROVED, "warehouse-service", payload);
        outboxService.publish(EventTypes.EXCHANGE_WAREHOUSE, EventTypes.WAREHOUSE_COUNT_VARIANCE_APPROVED, "CycleCount", count.getId(), env);
    }
}
