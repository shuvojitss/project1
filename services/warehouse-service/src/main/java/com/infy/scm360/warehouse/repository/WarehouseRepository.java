package com.infy.scm360.warehouse.repository;

import com.infy.scm360.warehouse.domain.*;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface WarehouseRepository extends JpaRepository<Warehouse, String> {
    Optional<Warehouse> findByCode(String code);
}
