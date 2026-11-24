package com.infy.scm360.analytics.repository;

import com.infy.scm360.analytics.domain.FactOrder;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface FactOrderRepository extends JpaRepository<FactOrder, String> {
    Optional<FactOrder> findByOrderId(String orderId);
    long countByStatus(String status);

    @Query("SELECT AVG(f.cycleTimeHours) FROM FactOrder f WHERE f.cycleTimeHours IS NOT NULL")
    Double calculateAverageCycleTimeHours();

    @Query("SELECT COUNT(f) FROM FactOrder f WHERE f.fulfilledInFull = true")
    long countFulfilledInFull();
}
