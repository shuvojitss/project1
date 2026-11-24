package com.infy.scm360.warehouse.repository;

import com.infy.scm360.warehouse.domain.Bin;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface BinRepository extends JpaRepository<Bin, String> {
    List<Bin> findByZoneWarehouseId(String warehouseId);
}
