package com.infy.scm360.procurement.repository;

import com.infy.scm360.procurement.domain.PoStatusHistory;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PoStatusHistoryRepository extends JpaRepository<PoStatusHistory, String> {
    List<PoStatusHistory> findByPoIdOrderByChangedAtAsc(String poId);
}
