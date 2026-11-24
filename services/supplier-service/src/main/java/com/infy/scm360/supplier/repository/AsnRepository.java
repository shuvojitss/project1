package com.infy.scm360.supplier.repository;

import com.infy.scm360.supplier.domain.Asn;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface AsnRepository extends JpaRepository<Asn, String> {
    Page<Asn> findBySupplierId(String supplierId, Pageable pageable);
    Optional<Asn> findByAsnNumber(String asnNumber);
}
