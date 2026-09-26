package com.flowdesk.dto;

import com.flowdesk.enums.TicketStatus;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class UpdateStatusRequest {

    @NotNull
    private TicketStatus status;

    private Long changedBy;
    private Long changedByUserId;
    private Long userId;

    public UpdateStatusRequest() {}

    public UpdateStatusRequest(TicketStatus status, Long changedBy) {
        this.status = status;
        this.changedBy = changedBy;
        this.changedByUserId = changedBy;
        this.userId = changedBy;
    }

    public TicketStatus getStatus() { return status; }
    public void setStatus(TicketStatus status) { this.status = status; }

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
