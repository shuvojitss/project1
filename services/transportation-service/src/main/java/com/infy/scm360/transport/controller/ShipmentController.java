package com.infy.scm360.transport.controller;

import com.infy.scm360.common.dto.PageResponse;
import com.infy.scm360.transport.domain.ProofOfDelivery;
import com.infy.scm360.transport.domain.ShipmentStatus;
import com.infy.scm360.transport.domain.TrackingEvent;
import com.infy.scm360.transport.dto.*;
import com.infy.scm360.transport.service.ShipmentService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/shipments")
@Tag(name = "Transportation Shipments", description = "Shipment lifecycle, carrier assignment, dispatch, tracking, and POD")
public class ShipmentController {

    private final ShipmentService shipmentService;

    public ShipmentController(ShipmentService shipmentService) {
        this.shipmentService = shipmentService;
    }

    @PostMapping
    @PreAuthorize("hasAnyRole('LOGISTICS_PLANNER', 'ADMIN')")
    @Operation(summary = "Plan new freight shipment")
    public ResponseEntity<ShipmentResponse> createShipment(@Valid @RequestBody CreateShipmentRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(shipmentService.createShipment(request));
    }

    @GetMapping
    @PreAuthorize("isAuthenticated()")
    @Operation(summary = "List shipments with filtering and pagination")
    public ResponseEntity<PageResponse<ShipmentResponse>> listShipments(
            @RequestParam(required = false) ShipmentStatus status,
            @RequestParam(required = false) String carrierId,
            @RequestParam(required = false) String orderId,
            Pageable pageable
    ) {
        Page<ShipmentResponse> page = shipmentService.listShipments(status, carrierId, orderId, pageable);
        return ResponseEntity.ok(PageResponse.from(page));
    }

    @GetMapping("/{id}")
    @PreAuthorize("isAuthenticated()")
    @Operation(summary = "Get shipment details by id")
    public ResponseEntity<ShipmentResponse> getShipment(@PathVariable String id) {
        return ResponseEntity.ok(shipmentService.getShipment(id));
    }

    @PostMapping("/{id}/assign-carrier")
    @PreAuthorize("hasAnyRole('LOGISTICS_PLANNER', 'ADMIN')")
    @Operation(summary = "Assign carrier and rate schedule to shipment")
    public ResponseEntity<ShipmentResponse> assignCarrier(
            @PathVariable String id,
            @Valid @RequestBody AssignCarrierRequest request
    ) {
        return ResponseEntity.ok(shipmentService.assignCarrier(id, request));
    }

    @PostMapping("/{id}/dispatch")
    @PreAuthorize("hasAnyRole('LOGISTICS_PLANNER', 'ADMIN')")
    @Operation(summary = "Dispatch shipment with tracking number")
    public ResponseEntity<ShipmentResponse> dispatchShipment(@PathVariable String id) {
        return ResponseEntity.ok(shipmentService.dispatchShipment(id));
    }

    @PostMapping("/{id}/tracking-event")
    @PreAuthorize("hasAnyRole('LOGISTICS_PLANNER', 'ADMIN', 'WAREHOUSE_OPERATOR')")
    @Operation(summary = "Record carrier tracking checkpoint event")
    public ResponseEntity<ShipmentResponse> addTrackingEvent(
            @PathVariable String id,
            @Valid @RequestBody AddTrackingEventRequest request
    ) {
        return ResponseEntity.ok(shipmentService.addTrackingEvent(id, request));
    }

    @GetMapping("/{id}/tracking")
    @PreAuthorize("isAuthenticated()")
    @Operation(summary = "Get shipment tracking journey history")
    public ResponseEntity<List<TrackingEvent>> getTrackingHistory(@PathVariable String id) {
        return ResponseEntity.ok(shipmentService.getTrackingHistory(id));
    }

    @PostMapping("/{id}/proof-of-delivery")
    @PreAuthorize("hasAnyRole('LOGISTICS_PLANNER', 'ADMIN')")
    @Operation(summary = "Record electronic proof of delivery (ePOD)")
    public ResponseEntity<ShipmentResponse> recordProofOfDelivery(
            @PathVariable String id,
            @Valid @RequestBody ProofOfDeliveryRequest request
    ) {
        return ResponseEntity.ok(shipmentService.recordProofOfDelivery(id, request));
    }

    @GetMapping("/{id}/proof-of-delivery")
    @PreAuthorize("isAuthenticated()")
    @Operation(summary = "Retrieve proof of delivery details")
    public ResponseEntity<ProofOfDelivery> getPod(@PathVariable String id) {
        return ResponseEntity.ok(shipmentService.getPod(id));
    }
}
