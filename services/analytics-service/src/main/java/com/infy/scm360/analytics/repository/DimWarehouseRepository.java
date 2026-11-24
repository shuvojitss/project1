package com.infy.scm360.analytics.repository;

import com.infy.scm360.analytics.domain.DimWarehouse;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface DimWarehouseRepository extends JpaRepository<DimWarehouse, String> {
    Optional<DimWarehouse> findByCode(String code);
}
