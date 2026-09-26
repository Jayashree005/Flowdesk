package com.flowdesk.workflow;

import com.flowdesk.entity.WorkflowRule;
import com.flowdesk.enums.Severity;
import com.flowdesk.enums.TicketCategory;
import com.flowdesk.enums.TicketStatus;
import com.flowdesk.exception.BusinessException;
import com.flowdesk.repository.WorkflowRuleRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class WorkflowEngine {

    private final WorkflowRuleRepository workflowRuleRepository;

    public WorkflowEngine(WorkflowRuleRepository workflowRuleRepository) {
        this.workflowRuleRepository = workflowRuleRepository;
    }

    public void validateTransition(
            TicketCategory category,
            Severity severity,
            TicketStatus currentStatus,
            TicketStatus requestedStatus
    ) {

        if (currentStatus == requestedStatus) {
            throw new BusinessException(
                    "Ticket is already in " + currentStatus + " status."
            );
        }

        WorkflowRule rule =
                workflowRuleRepository
                        .findByCategoryAndSeverityAndFromStatusAndToStatus(
                                category,
                                severity,
                                currentStatus,
                                requestedStatus
                        )
                        .orElseThrow(() ->
                                new BusinessException(
                                        "Invalid workflow transition: "
                                                + currentStatus
                                                + " → "
                                                + requestedStatus
                                )
                        );

        if (!rule.isAllowed()) {
            throw new BusinessException(
                    "This workflow transition is not allowed."
            );
        }
    }
}
