package com.infy.scm360.analytics.repository;

import com.infy.scm360.analytics.domain.DimProduct;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface DimProductRepository extends JpaRepository<DimProduct, String> {
    Optional<DimProduct> findBySku(String sku);
}
