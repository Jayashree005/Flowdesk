package com.flowdesk.repository;

import com.flowdesk.entity.WorkflowRule;
import com.flowdesk.enums.Severity;
import com.flowdesk.enums.TicketCategory;
import com.flowdesk.enums.TicketStatus;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface WorkflowRuleRepository
        extends JpaRepository<WorkflowRule, Long> {

    Optional<WorkflowRule> findByCategoryAndSeverityAndFromStatusAndToStatus(
            TicketCategory category,
            Severity severity,
            TicketStatus fromStatus,
            TicketStatus toStatus
    );
}
