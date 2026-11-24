package com.infy.scm360.procurement.repository;

import com.infy.scm360.procurement.domain.ApprovalRule;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface ApprovalRuleRepository extends JpaRepository<ApprovalRule, String> {
    Optional<ApprovalRule> findByRoleAndActiveTrue(String role);
}
