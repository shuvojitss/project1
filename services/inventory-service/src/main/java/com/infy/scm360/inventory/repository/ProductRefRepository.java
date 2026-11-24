package com.infy.scm360.inventory.repository;

import com.infy.scm360.inventory.domain.ProductRef;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ProductRefRepository extends JpaRepository<ProductRef, String> {
}
