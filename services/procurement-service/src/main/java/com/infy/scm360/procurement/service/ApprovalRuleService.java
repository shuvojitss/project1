package com.infy.scm360.procurement.service;

import com.infy.scm360.procurement.domain.ApprovalRule;
import com.infy.scm360.procurement.repository.ApprovalRuleRepository;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.Collection;

@Service
@Transactional(readOnly = true)
public class ApprovalRuleService {

    private final ApprovalRuleRepository approvalRuleRepository;

    public ApprovalRuleService(ApprovalRuleRepository approvalRuleRepository) {
        this.approvalRuleRepository = approvalRuleRepository;
    }

    public void validateApprovalLimit(Collection<String> roles, BigDecimal amount) {
        if (roles.contains("ADMIN") || roles.contains("ROLE_ADMIN")) {
            return;
        }

        BigDecimal maxAllowed = BigDecimal.ZERO;
        for (String role : roles) {
            String cleanRole = role.startsWith("ROLE_") ? role.substring(5) : role;
            var ruleOpt = approvalRuleRepository.findByRoleAndActiveTrue(cleanRole);
            if (ruleOpt.isPresent()) {
                BigDecimal ruleLimit = ruleOpt.get().getMaxAmount();
                if (ruleLimit.compareTo(maxAllowed) > 0) {
                    maxAllowed = ruleLimit;
                }
            }
        }

        if (maxAllowed.compareTo(BigDecimal.ZERO) == 0 || amount.compareTo(maxAllowed) > 0) {
            throw new AccessDeniedException(
                    "User roles " + roles + " do not have authorization to approve amount $" + amount +
                    " (maximum allowed threshold: $" + maxAllowed + ")"
            );
        }
    }
}
