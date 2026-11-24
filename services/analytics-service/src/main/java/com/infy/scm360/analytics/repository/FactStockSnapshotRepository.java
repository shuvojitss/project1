package com.infy.scm360.analytics.repository;

import com.infy.scm360.analytics.domain.FactStockSnapshot;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface FactStockSnapshotRepository extends JpaRepository<FactStockSnapshot, String> {
    @Query("SELECT AVG(f.totalInventoryValue) FROM FactStockSnapshot f")
    Double getAverageInventoryValue();

    @Query("SELECT SUM(f.totalCogs) FROM FactStockSnapshot f")
    Double getTotalCogs();

    @Query("SELECT SUM(f.occupiedBins), SUM(f.totalBins) FROM FactStockSnapshot f")
    List<Object[]> getBinOccupancyTotals();
}
