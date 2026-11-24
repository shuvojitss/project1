package com.infy.scm360.transport.repository;

import com.infy.scm360.transport.domain.ProofOfDelivery;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface ProofOfDeliveryRepository extends JpaRepository<ProofOfDelivery, String> {
    Optional<ProofOfDelivery> findByShipmentId(String shipmentId);
}
