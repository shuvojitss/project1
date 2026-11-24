package com.infy.scm360.procurement.repository;

import com.infy.scm360.procurement.domain.GoodsReceiptRef;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface GoodsReceiptRefRepository extends JpaRepository<GoodsReceiptRef, String> {
    List<GoodsReceiptRef> findByPoId(String poId);
}
