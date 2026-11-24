package com.infy.scm360.order.repository;

import com.infy.scm360.order.domain.CustomerRef;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface CustomerRefRepository extends JpaRepository<CustomerRef, String> {
    Optional<CustomerRef> findByCustomerCode(String customerCode);
}
