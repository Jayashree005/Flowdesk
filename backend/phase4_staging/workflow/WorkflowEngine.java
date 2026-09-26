package com.flowdesk.workflow;

import com.flowdesk.enums.TicketStatus;
import com.flowdesk.exception.BusinessException;
import org.springframework.stereotype.Component;

import java.util.*;

@Component
public class WorkflowEngine {

    private final Map<TicketStatus, Set<TicketStatus>> allowedTransitions = new EnumMap<>(TicketStatus.class);

    public WorkflowEngine() {
        initTransitions();
    }

    private void initTransitions() {
        allowedTransitions.put(TicketStatus.OPEN, EnumSet.of(TicketStatus.ASSIGNED, TicketStatus.ESCALATED));
        allowedTransitions.put(TicketStatus.ASSIGNED, EnumSet.of(TicketStatus.IN_PROGRESS, TicketStatus.OPEN, TicketStatus.ESCALATED));
        allowedTransitions.put(TicketStatus.IN_PROGRESS, EnumSet.of(TicketStatus.RESOLVED, TicketStatus.ASSIGNED, TicketStatus.ESCALATED));
        allowedTransitions.put(TicketStatus.RESOLVED, EnumSet.of(TicketStatus.CLOSED, TicketStatus.IN_PROGRESS));
        allowedTransitions.put(TicketStatus.ESCALATED, EnumSet.of(TicketStatus.ASSIGNED, TicketStatus.IN_PROGRESS, TicketStatus.RESOLVED));
        allowedTransitions.put(TicketStatus.CLOSED, Collections.emptySet());
    }

    public boolean isValidTransition(TicketStatus currentStatus, TicketStatus requestedStatus) {
        if (currentStatus == null || requestedStatus == null) return false;
        if (currentStatus == requestedStatus) return true;
        return allowedTransitions.getOrDefault(currentStatus, Collections.emptySet()).contains(requestedStatus);
    }

    public Set<TicketStatus> getAllowedNextStatuses(TicketStatus currentStatus) {
        if (currentStatus == null) return Collections.emptySet();
        return Collections.unmodifiableSet(allowedTransitions.getOrDefault(currentStatus, Collections.emptySet()));
    }

    public void validateTransition(TicketStatus currentStatus, TicketStatus requestedStatus) {
        if (currentStatus == requestedStatus) return;
        if (currentStatus == TicketStatus.CLOSED) {
            throw new BusinessException("Closed tickets are archived and cannot undergo further status transitions.");
        }
        if ((currentStatus == TicketStatus.OPEN || currentStatus == TicketStatus.ASSIGNED || currentStatus == TicketStatus.IN_PROGRESS)
                && requestedStatus == TicketStatus.CLOSED) {
            throw new BusinessException("Invalid workflow transition: " + currentStatus + " -> " + requestedStatus +
                    ". Enterprise policy requires tickets to be properly RESOLVED and verified before CLOSURE.");
        }
        if (!isValidTransition(currentStatus, requestedStatus)) {
            throw new BusinessException("Invalid workflow transition: Cannot move ticket from " +
                    currentStatus + " to " + requestedStatus + ". Allowed next states: " +
                    getAllowedNextStatuses(currentStatus));
        }
    }
}
