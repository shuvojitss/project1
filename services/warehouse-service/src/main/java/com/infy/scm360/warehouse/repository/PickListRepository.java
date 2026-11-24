package com.infy.scm360.warehouse.repository;

import com.infy.scm360.warehouse.domain.PickList;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PickListRepository extends JpaRepository<PickList, String> {
    List<PickList> findByOrderId(String orderId);
}
