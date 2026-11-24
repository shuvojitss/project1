package com.infy.scm360.analytics.repository;

import com.infy.scm360.analytics.domain.FactPo;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface FactPoRepository extends JpaRepository<FactPo, String> {
    Optional<FactPo> findByPoId(String poId);

    @Query("SELECT AVG(f.cycleTimeDays) FROM FactPo f WHERE f.cycleTimeDays IS NOT NULL")
    Double calculateAveragePoCycleTimeDays();

    @Query("SELECT COUNT(f) FROM FactPo f WHERE f.deliveredOnTime = true")
    long countDeliveredOnTime();

    @Query("SELECT f.categoryName, SUM(f.totalAmount) FROM FactPo f GROUP BY f.categoryName")
    List<Object[]> getSpendByCategory();

    @Query("SELECT f.supplierName, SUM(f.totalAmount) FROM FactPo f GROUP BY f.supplierName")
    List<Object[]> getSpendBySupplier();
}
