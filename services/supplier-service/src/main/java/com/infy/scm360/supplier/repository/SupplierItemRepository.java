package com.infy.scm360.supplier.repository;

import com.infy.scm360.supplier.domain.SupplierItem;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface SupplierItemRepository extends JpaRepository<SupplierItem, String> {
    List<SupplierItem> findBySupplierId(String supplierId);
    List<SupplierItem> findByProductIdOrderByPriorityAsc(String productId);
    Optional<SupplierItem> findBySupplierIdAndProductId(String supplierId, String productId);
}
