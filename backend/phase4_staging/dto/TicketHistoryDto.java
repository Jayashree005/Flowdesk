package com.flowdesk.dto;

import com.flowdesk.enums.Status;
import java.time.LocalDateTime;

public class TicketHistoryDto {
    private Long id;
    private Long ticketId;
    private String action;
    private Status fromStatus;
    private Status toStatus;
    private String performedBy;
    private String details;
    private LocalDateTime timestamp;

    public TicketHistoryDto() {}

    public TicketHistoryDto(Long id, Long ticketId, String action, Status fromStatus, Status toStatus, String performedBy, String details, LocalDateTime timestamp) {
        this.id = id;
        this.ticketId = ticketId;
        this.action = action;
        this.fromStatus = fromStatus;
        this.toStatus = toStatus;
        this.performedBy = performedBy;
        this.details = details;
        this.timestamp = timestamp;
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public Long getTicketId() { return ticketId; }
    public void setTicketId(Long ticketId) { this.ticketId = ticketId; }

    public String getAction() { return action; }
    public void setAction(String action) { this.action = action; }

    public Status getFromStatus() { return fromStatus; }
    public void setFromStatus(Status fromStatus) { this.fromStatus = fromStatus; }

    public Status getToStatus() { return toStatus; }
    public void setToStatus(Status toStatus) { this.toStatus = toStatus; }

    public String getPerformedBy() { return performedBy; }
    public void setPerformedBy(String performedBy) { this.performedBy = performedBy; }

    public String getDetails() { return details; }
    public void setDetails(String details) { this.details = details; }

    public LocalDateTime getTimestamp() { return timestamp; }
    public void setTimestamp(LocalDateTime timestamp) { this.timestamp = timestamp; }
}
