package com.infy.scm360.warehouse.repository;

import com.infy.scm360.warehouse.domain.TransferOrder;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface TransferOrderRepository extends JpaRepository<TransferOrder, String> {
    Page<TransferOrder> findAll(Pageable pageable);
}
