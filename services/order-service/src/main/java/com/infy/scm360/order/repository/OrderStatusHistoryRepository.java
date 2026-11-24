package com.infy.scm360.order.repository;

import com.infy.scm360.order.domain.OrderStatusHistory;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface OrderStatusHistoryRepository extends JpaRepository<OrderStatusHistory, String> {
    List<OrderStatusHistory> findByOrderIdOrderByChangedAtAsc(String orderId);
}
