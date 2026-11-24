package com.infy.scm360.inventory.repository;

import com.infy.scm360.inventory.domain.StockLevel;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;
import java.util.Optional;

@Repository
public interface StockLevelRepository extends JpaRepository<StockLevel, String> {

    List<StockLevel> findByProductIdAndWarehouseId(String productId, String warehouseId);

    Page<StockLevel> findAll(Pageable pageable);

    Page<StockLevel> findByWarehouseId(String warehouseId, Pageable pageable);

    Page<StockLevel> findByProductId(String productId, Pageable pageable);

    @Modifying
    @Query("UPDATE StockLevel s SET s.reserved = s.reserved + :qty, s.version = s.version + 1, s.updatedAt = :now " +
           "WHERE s.productId = :prodId AND s.warehouseId = :whId AND (s.onHand - s.reserved) >= :qty")
    int reserveStock(@Param("prodId") String prodId, @Param("whId") String whId,
                     @Param("qty") BigDecimal qty, @Param("now") Instant now);

    @Modifying
    @Query("UPDATE StockLevel s SET s.reserved = s.reserved - :qty, s.version = s.version + 1, s.updatedAt = :now " +
           "WHERE s.productId = :prodId AND s.warehouseId = :whId AND s.reserved >= :qty")
    int releaseReservation(@Param("prodId") String prodId, @Param("whId") String whId,
                           @Param("qty") BigDecimal qty, @Param("now") Instant now);

    @Modifying
    @Query("UPDATE StockLevel s SET s.onHand = s.onHand - :qty, s.reserved = s.reserved - :qty, s.version = s.version + 1, s.updatedAt = :now " +
           "WHERE s.productId = :prodId AND s.warehouseId = :whId AND s.reserved >= :qty")
    int commitReservation(@Param("prodId") String prodId, @Param("whId") String whId,
                          @Param("qty") BigDecimal qty, @Param("now") Instant now);

    @Modifying
    @Query("UPDATE StockLevel s SET s.onHand = s.onHand + :qty, s.version = s.version + 1, s.updatedAt = :now " +
           "WHERE s.productId = :prodId AND s.warehouseId = :whId")
    int addOnHand(@Param("prodId") String prodId, @Param("whId") String whId,
                  @Param("qty") BigDecimal qty, @Param("now") Instant now);
}
