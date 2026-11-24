package com.infy.scm360.order.repository;

import com.infy.scm360.order.domain.CustomerOrder;
import com.infy.scm360.order.domain.OrderStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface CustomerOrderRepository extends JpaRepository<CustomerOrder, String> {
    Optional<CustomerOrder> findByOrderNumber(String orderNumber);
    Page<CustomerOrder> findByStatus(OrderStatus status, Pageable pageable);
    Page<CustomerOrder> findByCustomerId(String customerId, Pageable pageable);
    Page<CustomerOrder> findByOriginWarehouseId(String warehouseId, Pageable pageable);
    long countByStatus(OrderStatus status);
}
