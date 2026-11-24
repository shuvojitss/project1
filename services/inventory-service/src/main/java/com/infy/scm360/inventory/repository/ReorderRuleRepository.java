package com.infy.scm360.inventory.repository;

import com.infy.scm360.inventory.domain.ReorderRule;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface ReorderRuleRepository extends JpaRepository<ReorderRule, String> {
    Optional<ReorderRule> findByProductIdAndWarehouseId(String productId, String warehouseId);
}
