package com.infy.scm360.analytics.repository;

import com.infy.scm360.analytics.domain.DimSupplier;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface DimSupplierRepository extends JpaRepository<DimSupplier, String> {
    Optional<DimSupplier> findBySupplierCode(String supplierCode);
}
