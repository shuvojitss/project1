package com.infy.scm360.transport.repository;

import com.infy.scm360.transport.domain.RateCardEntry;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface RateCardEntryRepository extends JpaRepository<RateCardEntry, String> {
    List<RateCardEntry> findByOriginRegionAndDestinationRegion(String originRegion, String destinationRegion);
    List<RateCardEntry> findByCarrierId(String carrierId);
}
