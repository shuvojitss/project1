package com.infy.scm360.supplier.repository;

import com.infy.scm360.supplier.domain.SupplierScorecard;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface SupplierScorecardRepository extends JpaRepository<SupplierScorecard, String> {
    Optional<SupplierScorecard> findBySupplierId(String supplierId);
}
