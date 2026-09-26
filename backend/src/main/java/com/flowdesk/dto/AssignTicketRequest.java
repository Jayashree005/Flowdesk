package com.flowdesk.dto;

import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class AssignTicketRequest {

    @NotNull
    private Long agentId;

    private Long changedBy;
    private Long changedByUserId;
    private Long userId;

    public AssignTicketRequest() {}

    public AssignTicketRequest(Long agentId, Long changedBy) {
        this.agentId = agentId;
        this.changedBy = changedBy;
        this.changedByUserId = changedBy;
        this.userId = changedBy;
    }

    public Long getAgentId() { return agentId; }
    public void setAgentId(Long agentId) { this.agentId = agentId; }

    public Long getChangedBy() {
        if (changedBy != null) return changedBy;
        if (changedByUserId != null) return changedByUserId;
        return userId;
    }
    public void setChangedBy(Long changedBy) {
        this.changedBy = changedBy;
        this.changedByUserId = changedBy;
        this.userId = changedBy;
    }

    public Long getChangedByUserId() { return getChangedBy(); }
    public void setChangedByUserId(Long changedByUserId) { setChangedBy(changedByUserId); }

    public Long getUserId() { return getChangedBy(); }
    public void setUserId(Long userId) { setChangedBy(userId); }
}
