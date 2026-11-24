package com.infy.scm360.procurement.repository;

import com.infy.scm360.procurement.domain.PoStatus;
import com.infy.scm360.procurement.domain.PurchaseOrder;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface PurchaseOrderRepository extends JpaRepository<PurchaseOrder, String> {
    Optional<PurchaseOrder> findByPoNumber(String poNumber);
    Page<PurchaseOrder> findByStatus(PoStatus status, Pageable pageable);
    Page<PurchaseOrder> findBySupplierId(String supplierId, Pageable pageable);
    List<PurchaseOrder> findBySupplierIdAndStatus(String supplierId, PoStatus status);
    long countByStatus(PoStatus status);
}
