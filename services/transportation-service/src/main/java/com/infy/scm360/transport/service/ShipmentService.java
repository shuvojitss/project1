package com.infy.scm360.transport.service;

import com.infy.scm360.common.exception.ResourceNotFoundException;
import com.infy.scm360.common.exception.StateConflictException;
import com.infy.scm360.transport.domain.*;
import com.infy.scm360.transport.dto.*;
import com.infy.scm360.transport.messaging.TransportEventPublisher;
import com.infy.scm360.transport.repository.CarrierRepository;
import com.infy.scm360.transport.repository.ProofOfDeliveryRepository;
import com.infy.scm360.transport.repository.ShipmentRepository;
import com.infy.scm360.transport.repository.TrackingEventRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.Instant;
import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ThreadLocalRandom;
import java.util.stream.Collectors;

@Service
@Transactional
public class ShipmentService {

    private final ShipmentRepository shipmentRepository;
    private final CarrierRepository carrierRepository;
    private final TrackingEventRepository trackingRepository;
    private final ProofOfDeliveryRepository podRepository;
    private final RateService rateService;
    private final TransportEventPublisher eventPublisher;

    public ShipmentService(
            ShipmentRepository shipmentRepository,
            CarrierRepository carrierRepository,
            TrackingEventRepository trackingRepository,
            ProofOfDeliveryRepository podRepository,
            RateService rateService,
            TransportEventPublisher eventPublisher
    ) {
        this.shipmentRepository = shipmentRepository;
        this.carrierRepository = carrierRepository;
        this.trackingRepository = trackingRepository;
        this.podRepository = podRepository;
        this.rateService = rateService;
        this.eventPublisher = eventPublisher;
    }

    public ShipmentResponse createShipment(CreateShipmentRequest request) {
        Shipment shipment = new Shipment();
        shipment.setShipmentNumber(generateShipmentNumber());
        shipment.setOrderId(request.orderId());
        shipment.setOrderNumber(request.orderNumber());
        shipment.setOriginAddress(request.originAddress());
        shipment.setOriginRegion(request.originRegion());
        shipment.setDestinationAddress(request.destinationAddress());
        shipment.setDestinationRegion(request.destinationRegion());
        if (request.currency() != null && !request.currency().isBlank()) {
            shipment.setCurrency(request.currency());
        }
        shipment.setStatus(ShipmentStatus.PLANNED);

        for (ShipmentItemDto itemDto : request.items()) {
            ShipmentItem item = new ShipmentItem(
                    itemDto.productId(),
                    itemDto.productSku(),
                    itemDto.productName(),
                    itemDto.quantity(),
                    itemDto.weightKg()
            );
            shipment.addItem(item);
        }

        if (request.carrierId() != null && !request.carrierId().isBlank()) {
            Carrier carrier = carrierRepository.findById(request.carrierId())
                    .orElseThrow(() -> new ResourceNotFoundException("Carrier not found with id: " + request.carrierId()));
            shipment.setCarrierId(carrier.getId());
            shipment.setCarrierName(carrier.getName());
            shipment.setStatus(ShipmentStatus.ASSIGNED);

            List<RateQuoteDto> rates = rateService.calculateBestRates(new RateCalculationRequest(
                    shipment.getOriginRegion(), shipment.getDestinationRegion(), shipment.getTotalWeightKg(), carrier.getServiceLevel()
            ));
            rates.stream()
                    .filter(r -> r.carrierId().equals(carrier.getId()))
                    .findFirst()
                    .ifPresent(r -> {
                        shipment.setShippingCost(r.estimatedCost());
                        shipment.setEstimatedDeliveryDate(LocalDate.now().plusDays(r.transitDays()));
                    });
        }

        Shipment saved = shipmentRepository.save(shipment);
        trackingRepository.save(new TrackingEvent(
                saved.getId(), "PLANNED", saved.getOriginRegion(), "Shipment planned and items staged"
        ));

        eventPublisher.publishShipmentCreated(saved.getId(), saved.getShipmentNumber(), toResponse(saved));
        return toResponse(saved);
    }

    public ShipmentResponse assignCarrier(String id, AssignCarrierRequest request) {
        Shipment shipment = shipmentRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Shipment not found with id: " + id));

        Carrier carrier = carrierRepository.findById(request.carrierId())
                .orElseThrow(() -> new ResourceNotFoundException("Carrier not found with id: " + request.carrierId()));

        shipment.setCarrierId(carrier.getId());
        shipment.setCarrierName(carrier.getName());
        shipment.setStatus(ShipmentStatus.ASSIGNED);

        List<RateQuoteDto> rates = rateService.calculateBestRates(new RateCalculationRequest(
                shipment.getOriginRegion(), shipment.getDestinationRegion(), shipment.getTotalWeightKg(), carrier.getServiceLevel()
        ));
        rates.stream()
                .filter(r -> r.carrierId().equals(carrier.getId()))
                .findFirst()
                .ifPresentOrElse(
                        r -> {
                            shipment.setShippingCost(r.estimatedCost());
                            shipment.setEstimatedDeliveryDate(request.estimatedDeliveryDate() != null ? request.estimatedDeliveryDate() : LocalDate.now().plusDays(r.transitDays()));
                        },
                        () -> {
                            if (request.estimatedDeliveryDate() != null) {
                                shipment.setEstimatedDeliveryDate(request.estimatedDeliveryDate());
                            }
                        }
                );

        Shipment saved = shipmentRepository.save(shipment);
        trackingRepository.save(new TrackingEvent(
                saved.getId(), "ASSIGNED", saved.getOriginRegion(), "Carrier " + carrier.getName() + " assigned to shipment"
        ));

        return toResponse(saved);
    }

    public ShipmentResponse dispatchShipment(String id) {
        Shipment shipment = shipmentRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Shipment not found with id: " + id));

        if (shipment.getStatus() != ShipmentStatus.ASSIGNED && shipment.getStatus() != ShipmentStatus.PLANNED) {
            throw new StateConflictException("Cannot dispatch shipment in status: " + shipment.getStatus());
        }

        if (shipment.getTrackingNumber() == null || shipment.getTrackingNumber().isBlank()) {
            String carrierCode = shipment.getCarrierName() != null ? shipment.getCarrierName().replaceAll("\\s+", "").toUpperCase() : "CARRIER";
            int rand = ThreadLocalRandom.current().nextInt(100000, 999999);
            shipment.setTrackingNumber("TRK-" + carrierCode.substring(0, Math.min(5, carrierCode.length())) + "-" + rand);
        }

        shipment.setStatus(ShipmentStatus.DISPATCHED);
        shipment.setDispatchedAt(Instant.now());
        if (shipment.getEstimatedDeliveryDate() == null) {
            shipment.setEstimatedDeliveryDate(LocalDate.now().plusDays(3));
        }

        Shipment saved = shipmentRepository.save(shipment);
        trackingRepository.save(new TrackingEvent(
                saved.getId(), "DISPATCHED", saved.getOriginRegion(),
                "Shipment dispatched with tracking number: " + saved.getTrackingNumber()
        ));

        eventPublisher.publishShipmentDispatched(saved.getId(), saved.getShipmentNumber(), Map.of(
                "shipmentId", saved.getId(),
                "shipmentNumber", saved.getShipmentNumber(),
                "orderId", saved.getOrderId() != null ? saved.getOrderId() : "",
                "carrierId", saved.getCarrierId() != null ? saved.getCarrierId() : "",
                "trackingNumber", saved.getTrackingNumber(),
                "estimatedDeliveryDate", saved.getEstimatedDeliveryDate().toString()
        ));

        return toResponse(saved);
    }

    public ShipmentResponse addTrackingEvent(String id, AddTrackingEventRequest request) {
        Shipment shipment = shipmentRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Shipment not found with id: " + id));

        TrackingEvent event = new TrackingEvent(
                shipment.getId(), request.status(), request.location(), request.description()
        );
        trackingRepository.save(event);

        if ("DELAYED".equalsIgnoreCase(request.status())) {
            shipment.setStatus(ShipmentStatus.DELAYED);
            shipmentRepository.save(shipment);
            eventPublisher.publishShipmentDelayed(shipment.getId(), shipment.getShipmentNumber(), Map.of(
                    "shipmentId", shipment.getId(),
                    "reason", request.description(),
                    "orderId", shipment.getOrderId() != null ? shipment.getOrderId() : ""
            ));
        } else if ("IN_TRANSIT".equalsIgnoreCase(request.status()) && shipment.getStatus() != ShipmentStatus.DELIVERED) {
            shipment.setStatus(ShipmentStatus.IN_TRANSIT);
            shipmentRepository.save(shipment);
        }

        return toResponse(shipment);
    }

    public ShipmentResponse recordProofOfDelivery(String id, ProofOfDeliveryRequest request) {
        Shipment shipment = shipmentRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Shipment not found with id: " + id));

        if (shipment.getStatus() == ShipmentStatus.DELIVERED) {
            return toResponse(shipment);
        }

        ProofOfDelivery pod = new ProofOfDelivery(
                shipment.getId(),
                request.recipientName(),
                request.signatureData(),
                request.photoUrl(),
                request.notes()
        );
        podRepository.save(pod);

        shipment.setStatus(ShipmentStatus.DELIVERED);
        shipment.setActualDeliveryDate(Instant.now());

        Shipment saved = shipmentRepository.save(shipment);
        trackingRepository.save(new TrackingEvent(
                saved.getId(), "DELIVERED", saved.getDestinationRegion(),
                "Shipment delivered and signed by " + request.recipientName()
        ));

        eventPublisher.publishShipmentDelivered(saved.getId(), saved.getShipmentNumber(), Map.of(
                "shipmentId", saved.getId(),
                "shipmentNumber", saved.getShipmentNumber(),
                "orderId", saved.getOrderId() != null ? saved.getOrderId() : "",
                "recipientName", request.recipientName(),
                "deliveredAt", saved.getActualDeliveryDate().toString()
        ));

        return toResponse(saved);
    }

    @Transactional(readOnly = true)
    public ShipmentResponse getShipment(String id) {
        return shipmentRepository.findById(id)
                .map(this::toResponse)
                .orElseThrow(() -> new ResourceNotFoundException("Shipment not found with id: " + id));
    }

    @Transactional(readOnly = true)
    public Page<ShipmentResponse> listShipments(ShipmentStatus status, String carrierId, String orderId, Pageable pageable) {
        if (status != null) {
            return shipmentRepository.findByStatus(status, pageable).map(this::toResponse);
        }
        if (carrierId != null && !carrierId.isBlank()) {
            return shipmentRepository.findByCarrierId(carrierId, pageable).map(this::toResponse);
        }
        if (orderId != null && !orderId.isBlank()) {
            return shipmentRepository.findByOrderId(orderId, pageable).map(this::toResponse);
        }
        return shipmentRepository.findAll(pageable).map(this::toResponse);
    }

    @Transactional(readOnly = true)
    public List<TrackingEvent> getTrackingHistory(String shipmentId) {
        return trackingRepository.findByShipmentIdOrderByTimestampAsc(shipmentId);
    }

    @Transactional(readOnly = true)
    public ProofOfDelivery getPod(String shipmentId) {
        return podRepository.findByShipmentId(shipmentId)
                .orElseThrow(() -> new ResourceNotFoundException("POD not found for shipment: " + shipmentId));
    }

    private String generateShipmentNumber() {
        int year = LocalDate.now().getYear();
        int rand = ThreadLocalRandom.current().nextInt(1000, 9999);
        return String.format("SHP-%d-%04d", year, rand);
    }

    public ShipmentResponse toResponse(Shipment s) {
        List<ShipmentItemDto> items = s.getItems().stream()
                .map(i -> new ShipmentItemDto(
                        i.getId(),
                        i.getProductId(),
                        i.getProductSku(),
                        i.getProductName(),
                        i.getQuantity(),
                        i.getWeightKg()
                )).collect(Collectors.toList());

        return new ShipmentResponse(
                s.getId(),
                s.getShipmentNumber(),
                s.getOrderId(),
                s.getOrderNumber(),
                s.getCarrierId(),
                s.getCarrierName(),
                s.getStatus(),
                s.getOriginAddress(),
                s.getOriginRegion(),
                s.getDestinationAddress(),
                s.getDestinationRegion(),
                s.getTotalWeightKg(),
                s.getShippingCost(),
                s.getCurrency(),
                s.getTrackingNumber(),
                s.getEstimatedDeliveryDate(),
                s.getActualDeliveryDate(),
                s.getDispatchedAt(),
                s.getCreatedAt(),
                s.getUpdatedAt(),
                items
        );
    }
}
