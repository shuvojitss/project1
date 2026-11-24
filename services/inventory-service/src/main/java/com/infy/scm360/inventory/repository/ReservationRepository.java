package com.infy.scm360.inventory.repository;

import com.infy.scm360.inventory.domain.Reservation;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.Instant;
import java.util.List;

@Repository
public interface ReservationRepository extends JpaRepository<Reservation, String> {
    List<Reservation> findByOrderId(String orderId);
    List<Reservation> findByStatusAndExpiresAtBefore(String status, Instant cutoff);
}
