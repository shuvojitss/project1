package com.infy.scm360.procurement.messaging;

import com.infy.scm360.common.event.EventEnvelope;
import com.infy.scm360.common.event.EventTypes;
import com.infy.scm360.common.outbox.OutboxService;
import org.springframework.stereotype.Component;

@Component
public class ProcurementEventPublisher {

    private final OutboxService outboxService;

    public ProcurementEventPublisher(OutboxService outboxService) {
        this.outboxService = outboxService;
    }

    public void publishRequisitionCreated(String requisitionId, String requisitionNumber, Object payload) {
        EventEnvelope<?> event = EventEnvelope.of(EventTypes.PROCUREMENT_REQUISITION_CREATED, "procurement-service", payload);
        outboxService.publish(EventTypes.EXCHANGE_PROCUREMENT, EventTypes.PROCUREMENT_REQUISITION_CREATED, "Requisition", requisitionId, event);
    }

    public void publishRequisitionApproved(String requisitionId, String requisitionNumber, Object payload) {
        EventEnvelope<?> event = EventEnvelope.of(EventTypes.PROCUREMENT_REQUISITION_CREATED, "procurement-service", payload);
        outboxService.publish(EventTypes.EXCHANGE_PROCUREMENT, "procurement.requisition.approved", "Requisition", requisitionId, event);
    }

    public void publishPoCreated(String poId, String poNumber, Object payload) {
        EventEnvelope<?> event = EventEnvelope.of("procurement.po.created", "procurement-service", payload);
        outboxService.publish(EventTypes.EXCHANGE_PROCUREMENT, "procurement.po.created", "PurchaseOrder", poId, event);
    }

    public void publishPoApproved(String poId, String poNumber, Object payload) {
        EventEnvelope<?> event = EventEnvelope.of(EventTypes.PROCUREMENT_PO_APPROVED, "procurement-service", payload);
        outboxService.publish(EventTypes.EXCHANGE_PROCUREMENT, EventTypes.PROCUREMENT_PO_APPROVED, "PurchaseOrder", poId, event);
    }

    public void publishPoIssued(String poId, String poNumber, Object payload) {
        EventEnvelope<?> event = EventEnvelope.of(EventTypes.PROCUREMENT_PO_ISSUED, "procurement-service", payload);
        outboxService.publish(EventTypes.EXCHANGE_PROCUREMENT, EventTypes.PROCUREMENT_PO_ISSUED, "PurchaseOrder", poId, event);
    }

    public void publishPoCancelled(String poId, String poNumber, Object payload) {
        EventEnvelope<?> event = EventEnvelope.of(EventTypes.PROCUREMENT_PO_CANCELLED, "procurement-service", payload);
        outboxService.publish(EventTypes.EXCHANGE_PROCUREMENT, EventTypes.PROCUREMENT_PO_CANCELLED, "PurchaseOrder", poId, event);
    }

    public void publishPoClosed(String poId, String poNumber, Object payload) {
        EventEnvelope<?> event = EventEnvelope.of(EventTypes.PROCUREMENT_PO_CLOSED, "procurement-service", payload);
        outboxService.publish(EventTypes.EXCHANGE_PROCUREMENT, EventTypes.PROCUREMENT_PO_CLOSED, "PurchaseOrder", poId, event);
    }
}
