package com.infy.scm360.transport.repository;

import com.infy.scm360.transport.domain.Carrier;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface CarrierRepository extends JpaRepository<Carrier, String> {
    Optional<Carrier> findByCarrierCode(String carrierCode);
    List<Carrier> findByActiveTrue();
    Page<Carrier> findByActiveTrue(Pageable pageable);
}
