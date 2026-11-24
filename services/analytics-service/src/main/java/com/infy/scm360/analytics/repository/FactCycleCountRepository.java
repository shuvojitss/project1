package com.infy.scm360.analytics.repository;

import com.infy.scm360.analytics.domain.FactCycleCount;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface FactCycleCountRepository extends JpaRepository<FactCycleCount, String> {
    @Query("SELECT SUM(f.accurateItemsCounted), SUM(f.totalItemsCounted) FROM FactCycleCount f")
    List<Object[]> getCycleCountAccuracyTotals();
}
