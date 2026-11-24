package com.infy.scm360.transport;

import com.infy.scm360.transport.domain.ShipmentStatus;
import com.infy.scm360.transport.dto.*;
import com.infy.scm360.transport.service.CarrierService;
import com.infy.scm360.transport.service.RateService;
import com.infy.scm360.transport.service.ShipmentService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@ActiveProfiles("test")
@Transactional
class TransportationServiceApplicationTests {

    @Autowired
    private CarrierService carrierService;

    @Autowired
    private RateService rateService;

    @Autowired
    private ShipmentService shipmentService;

    @Test
    @DisplayName("Context loads and seed carriers/rate cards are present")
    void contextLoads() {
        assertNotNull(carrierService);
        assertNotNull(rateService);
        assertNotNull(shipmentService);

        List<CarrierDto> carriers = carrierService.listAllActiveCarriers();
        assertFalse(carriers.isEmpty());
        assertTrue(carriers.stream().anyMatch(c -> c.carrierCode().equals("FEDEX-FRT")));
    }

    @Test
    @DisplayName("Rate calculation selects best available rate for weight and region")
    void testRateCalculation() {
        RateCalculationRequest req = new RateCalculationRequest(
                "US_MIDWEST",
                "US_MIDWEST",
                BigDecimal.valueOf(250.00),
                "EXPRESS"
        );

        List<RateQuoteDto> quotes = rateService.calculateBestRates(req);
        assertFalse(quotes.isEmpty());

        RateQuoteDto bestQuote = quotes.get(0);
        assertEquals("FEDEX-FRT", bestQuote.carrierCode());
        // Base rate $75 + (250 * $0.45) = $75 + $112.50 = $187.50
        assertEquals(0, BigDecimal.valueOf(187.50).compareTo(bestQuote.estimatedCost()));
    }

    @Test
    @DisplayName("Full shipment lifecycle: Plan -> Assign -> Dispatch -> Track -> POD")
    void testShipmentLifecycle() {
        CreateShipmentRequest req = new CreateShipmentRequest(
                "ord-test-001",
                "ORD-2026-9999",
                "1000 Hub Way, Chicago, IL",
                "US_MIDWEST",
                "500 Market St, Chicago, IL",
                "US_MIDWEST",
                null,
                "USD",
                List.of(new ShipmentItemDto(
                        null,
                        "c0000000-0000-0000-0000-000000000001",
                        "SKU-RAW-001",
                        "Steel Sheet",
                        BigDecimal.valueOf(10),
                        BigDecimal.valueOf(100.00)
                ))
        );

        // 1. Plan shipment
        ShipmentResponse planned = shipmentService.createShipment(req);
        assertNotNull(planned.id());
        assertEquals(ShipmentStatus.PLANNED, planned.status());
        assertEquals(0, BigDecimal.valueOf(100.00).compareTo(planned.totalWeightKg()));

        // 2. Assign carrier
        ShipmentResponse assigned = shipmentService.assignCarrier(
                planned.id(), new AssignCarrierRequest("car00000-0000-0000-0000-000000000001", null)
        );
        assertEquals(ShipmentStatus.ASSIGNED, assigned.status());
        assertEquals("FedEx Freight", assigned.carrierName());
        assertTrue(assigned.shippingCost().compareTo(BigDecimal.ZERO) > 0);

        // 3. Dispatch shipment
        ShipmentResponse dispatched = shipmentService.dispatchShipment(assigned.id());
        assertEquals(ShipmentStatus.DISPATCHED, dispatched.status());
        assertNotNull(dispatched.trackingNumber());
        assertNotNull(dispatched.dispatchedAt());

        // 4. Tracking event: in transit
        ShipmentResponse inTransit = shipmentService.addTrackingEvent(
                dispatched.id(), new AddTrackingEventRequest("IN_TRANSIT", "Midway Sorting Hub", "In transit on route")
        );
        assertEquals(ShipmentStatus.IN_TRANSIT, inTransit.status());

        // 5. Proof of Delivery
        ShipmentResponse delivered = shipmentService.recordProofOfDelivery(
                inTransit.id(), new ProofOfDeliveryRequest("Jane Receiver", "SIG-DATA", "http://pod/image.jpg", "All intact")
        );
        assertEquals(ShipmentStatus.DELIVERED, delivered.status());
        assertNotNull(delivered.actualDeliveryDate());

        // Verify POD record
        assertNotNull(shipmentService.getPod(delivered.id()));
    }
}
