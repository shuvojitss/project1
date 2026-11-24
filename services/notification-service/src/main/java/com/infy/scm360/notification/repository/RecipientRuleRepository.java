package com.infy.scm360.notification.repository;

import com.infy.scm360.notification.domain.RecipientRule;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface RecipientRuleRepository extends JpaRepository<RecipientRule, String> {
    List<RecipientRule> findByEventType(String eventType);
}
