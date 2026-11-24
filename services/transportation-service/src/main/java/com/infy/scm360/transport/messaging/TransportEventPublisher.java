package com.infy.scm360.transport.messaging;

import com.infy.scm360.common.event.EventEnvelope;
import com.infy.scm360.common.event.EventTypes;
import com.infy.scm360.common.outbox.OutboxService;
import org.springframework.stereotype.Component;

@Component
public class TransportEventPublisher {

    private final OutboxService outboxService;

    public TransportEventPublisher(OutboxService outboxService) {
        this.outboxService = outboxService;
    }

    public void publishShipmentCreated(String shipmentId, String shipmentNumber, Object payload) {
        EventEnvelope<?> event = EventEnvelope.of(EventTypes.TRANSPORT_SHIPMENT_CREATED, "transportation-service", payload);
        outboxService.publish("transport.events", EventTypes.TRANSPORT_SHIPMENT_CREATED, "Shipment", shipmentId, event);
    }

    public void publishShipmentDispatched(String shipmentId, String shipmentNumber, Object payload) {
        EventEnvelope<?> event = EventEnvelope.of(EventTypes.TRANSPORT_SHIPMENT_DISPATCHED, "transportation-service", payload);
        outboxService.publish("transport.events", EventTypes.TRANSPORT_SHIPMENT_DISPATCHED, "Shipment", shipmentId, event);
    }

    public void publishShipmentDelayed(String shipmentId, String shipmentNumber, Object payload) {
        EventEnvelope<?> event = EventEnvelope.of(EventTypes.TRANSPORT_SHIPMENT_DELAYED, "transportation-service", payload);
        outboxService.publish("transport.events", EventTypes.TRANSPORT_SHIPMENT_DELAYED, "Shipment", shipmentId, event);
    }

    public void publishShipmentDelivered(String shipmentId, String shipmentNumber, Object payload) {
        EventEnvelope<?> event = EventEnvelope.of(EventTypes.TRANSPORT_SHIPMENT_DELIVERED, "transportation-service", payload);
        outboxService.publish("transport.events", EventTypes.TRANSPORT_SHIPMENT_DELIVERED, "Shipment", shipmentId, event);
    }

    public void publishDeliveryFailed(String shipmentId, String shipmentNumber, Object payload) {
        EventEnvelope<?> event = EventEnvelope.of(EventTypes.TRANSPORT_DELIVERY_FAILED, "transportation-service", payload);
        outboxService.publish("transport.events", EventTypes.TRANSPORT_DELIVERY_FAILED, "Shipment", shipmentId, event);
    }
}
