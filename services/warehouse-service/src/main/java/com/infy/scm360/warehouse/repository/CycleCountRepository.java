package com.infy.scm360.warehouse.repository;

import com.infy.scm360.warehouse.domain.CycleCount;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CycleCountRepository extends JpaRepository<CycleCount, String> {
}
