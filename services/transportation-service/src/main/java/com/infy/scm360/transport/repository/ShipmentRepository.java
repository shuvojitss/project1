package com.infy.scm360.transport.repository;

import com.infy.scm360.transport.domain.Shipment;
import com.infy.scm360.transport.domain.ShipmentStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface ShipmentRepository extends JpaRepository<Shipment, String> {
    Optional<Shipment> findByShipmentNumber(String shipmentNumber);
    Optional<Shipment> findByTrackingNumber(String trackingNumber);
    Page<Shipment> findByStatus(ShipmentStatus status, Pageable pageable);
    Page<Shipment> findByCarrierId(String carrierId, Pageable pageable);
    Page<Shipment> findByOrderId(String orderId, Pageable pageable);
    long countByStatus(ShipmentStatus status);
}
