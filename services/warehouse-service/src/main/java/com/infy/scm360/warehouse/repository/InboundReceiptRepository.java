package com.infy.scm360.warehouse.repository;

import com.infy.scm360.warehouse.domain.InboundReceipt;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface InboundReceiptRepository extends JpaRepository<InboundReceipt, String> {
    Page<InboundReceipt> findByWarehouseId(String warehouseId, Pageable pageable);
    List<InboundReceipt> findByPoId(String poId);
}
