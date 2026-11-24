package com.infy.scm360.analytics.repository;

import com.infy.scm360.analytics.domain.FactShipment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface FactShipmentRepository extends JpaRepository<FactShipment, String> {
    Optional<FactShipment> findByShipmentId(String shipmentId);

    @Query("SELECT SUM(f.shippingCost) FROM FactShipment f")
    Double getTotalFreightSpend();

    @Query("SELECT SUM(f.totalWeightKg) FROM FactShipment f")
    Double getTotalWeightShippedKg();

    @Query("SELECT COUNT(f) FROM FactShipment f WHERE f.onTime = true AND f.inFull = true")
    long countOtifShipments();

    @Query("SELECT f.carrierName, AVG(f.transitDays) FROM FactShipment f WHERE f.transitDays IS NOT NULL GROUP BY f.carrierName")
    List<Object[]> getAverageTransitDaysByCarrier();
}
