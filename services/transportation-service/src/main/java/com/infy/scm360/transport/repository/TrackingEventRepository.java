package com.infy.scm360.transport.repository;

import com.infy.scm360.transport.domain.TrackingEvent;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface TrackingEventRepository extends JpaRepository<TrackingEvent, String> {
    List<TrackingEvent> findByShipmentIdOrderByTimestampAsc(String shipmentId);
}
