package com.infy.scm360.inventory.repository;

import com.infy.scm360.inventory.domain.StockLedger;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.util.List;

@Repository
public interface StockLedgerRepository extends JpaRepository<StockLedger, String> {
    Page<StockLedger> findByProductIdAndWarehouseIdOrderByOccurredAtDesc(String productId, String warehouseId, Pageable pageable);

    @Query("SELECT COALESCE(SUM(l.quantityDelta), 0) FROM StockLedger l WHERE l.productId = :prodId AND l.warehouseId = :whId")
    BigDecimal sumQuantityDelta(@Param("prodId") String prodId, @Param("whId") String whId);
}
