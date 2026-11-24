package com.infy.scm360.order.repository;

import com.infy.scm360.order.domain.Backorder;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface BackorderRepository extends JpaRepository<Backorder, String> {
    List<Backorder> findByOrderId(String orderId);
    List<Backorder> findByProductIdAndStatus(String productId, String status);
}
