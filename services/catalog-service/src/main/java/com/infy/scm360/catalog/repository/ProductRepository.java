package com.infy.scm360.catalog.repository;

import com.infy.scm360.catalog.domain.Product;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface ProductRepository extends JpaRepository<Product, String> {
    Optional<Product> findBySku(String sku);

    Page<Product> findAllByDeletedAtIsNull(Pageable pageable);

    Page<Product> findByCategoryIdAndDeletedAtIsNull(String categoryId, Pageable pageable);

    @Query("SELECT p FROM Product p WHERE p.deletedAt IS NULL AND (LOWER(p.name) LIKE LOWER(CONCAT('%', :query, '%')) OR LOWER(p.sku) LIKE LOWER(CONCAT('%', :query, '%')))")
    Page<Product> searchProducts(@Param("query") String query, Pageable pageable);
}
