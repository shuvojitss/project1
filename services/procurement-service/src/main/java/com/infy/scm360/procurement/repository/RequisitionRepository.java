package com.infy.scm360.procurement.repository;

import com.infy.scm360.procurement.domain.Requisition;
import com.infy.scm360.procurement.domain.RequisitionStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface RequisitionRepository extends JpaRepository<Requisition, String> {
    Optional<Requisition> findByRequisitionNumber(String requisitionNumber);
    Page<Requisition> findByStatus(RequisitionStatus status, Pageable pageable);
    long countByStatus(RequisitionStatus status);
}
