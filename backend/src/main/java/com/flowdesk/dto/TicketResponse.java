package com.flowdesk.dto;

import com.flowdesk.enums.Severity;
import com.flowdesk.enums.TicketCategory;
import com.flowdesk.enums.TicketStatus;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
@Builder
public class TicketResponse {

    private Long id;
    private String ticketNumber;
    private String title;
    private String description;

    private TicketCategory category;
    private Severity severity;
    private TicketStatus status;

    private Long requesterId;
    private String requesterName;

    private Long assignedAgentId;
    private String assignedAgentName;

    private Long departmentId;
    private String departmentName;

    private LocalDateTime slaDeadline;
    private LocalDateTime escalatedAt;

    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    private String slaStatus;

    public TicketResponse() {}

    public TicketResponse(Long id, String ticketNumber, String title, String description,
                          TicketCategory category, Severity severity, TicketStatus status,
                          Long requesterId, String requesterName,
                          Long assignedAgentId, String assignedAgentName,
                          Long departmentId, String departmentName,
                          LocalDateTime slaDeadline, LocalDateTime escalatedAt,
                          LocalDateTime createdAt, LocalDateTime updatedAt,
                          String slaStatus) {
        this.id = id;
        this.ticketNumber = ticketNumber;
        this.title = title;
        this.description = description;
        this.category = category;
        this.severity = severity;
        this.status = status;
        this.requesterId = requesterId;
        this.requesterName = requesterName;
        this.assignedAgentId = assignedAgentId;
        this.assignedAgentName = assignedAgentName;
        this.departmentId = departmentId;
        this.departmentName = departmentName;
        this.slaDeadline = slaDeadline;
        this.escalatedAt = escalatedAt;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
        this.slaStatus = slaStatus;
    }

    public static TicketResponseBuilder builder() {
        return new TicketResponseBuilder();
    }

    public static class TicketResponseBuilder {
        private Long id;
        private String ticketNumber;
        private String title;
        private String description;
        private TicketCategory category;
        private Severity severity;
        private TicketStatus status;
        private Long requesterId;
        private String requesterName;
        private Long assignedAgentId;
        private String assignedAgentName;
        private Long departmentId;
        private String departmentName;
        private LocalDateTime slaDeadline;
        private LocalDateTime escalatedAt;
        private LocalDateTime createdAt;
        private LocalDateTime updatedAt;
        private String slaStatus;

        public TicketResponseBuilder id(Long id) { this.id = id; return this; }
        public TicketResponseBuilder ticketNumber(String ticketNumber) { this.ticketNumber = ticketNumber; return this; }
        public TicketResponseBuilder title(String title) { this.title = title; return this; }
        public TicketResponseBuilder description(String description) { this.description = description; return this; }
        public TicketResponseBuilder category(TicketCategory category) { this.category = category; return this; }
        public TicketResponseBuilder severity(Severity severity) { this.severity = severity; return this; }
        public TicketResponseBuilder status(TicketStatus status) { this.status = status; return this; }
        public TicketResponseBuilder requesterId(Long requesterId) { this.requesterId = requesterId; return this; }
        public TicketResponseBuilder requesterName(String requesterName) { this.requesterName = requesterName; return this; }
        public TicketResponseBuilder assignedAgentId(Long assignedAgentId) { this.assignedAgentId = assignedAgentId; return this; }
        public TicketResponseBuilder assignedAgentName(String assignedAgentName) { this.assignedAgentName = assignedAgentName; return this; }
        public TicketResponseBuilder departmentId(Long departmentId) { this.departmentId = departmentId; return this; }
        public TicketResponseBuilder departmentName(String departmentName) { this.departmentName = departmentName; return this; }
        public TicketResponseBuilder slaDeadline(LocalDateTime slaDeadline) { this.slaDeadline = slaDeadline; return this; }
        public TicketResponseBuilder escalatedAt(LocalDateTime escalatedAt) { this.escalatedAt = escalatedAt; return this; }
        public TicketResponseBuilder createdAt(LocalDateTime createdAt) { this.createdAt = createdAt; return this; }
        public TicketResponseBuilder updatedAt(LocalDateTime updatedAt) { this.updatedAt = updatedAt; return this; }
        public TicketResponseBuilder slaStatus(String slaStatus) { this.slaStatus = slaStatus; return this; }

        public TicketResponse build() {
            return new TicketResponse(id, ticketNumber, title, description, category, severity, status,
                    requesterId, requesterName, assignedAgentId, assignedAgentName, departmentId, departmentName,
                    slaDeadline, escalatedAt, createdAt, updatedAt, slaStatus);
        }
    }

    public Long getId() { return id; }
    public String getTicketNumber() { return ticketNumber; }
    public String getTitle() { return title; }
    public String getDescription() { return description; }
    public TicketCategory getCategory() { return category; }
    public Severity getSeverity() { return severity; }
    public TicketStatus getStatus() { return status; }
    public Long getRequesterId() { return requesterId; }
    public String getRequesterName() { return requesterName; }
    public Long getAssignedAgentId() { return assignedAgentId; }
    public String getAssignedAgentName() { return assignedAgentName; }
    public Long getDepartmentId() { return departmentId; }
    public String getDepartmentName() { return departmentName; }
    public LocalDateTime getSlaDeadline() { return slaDeadline; }
    public LocalDateTime getEscalatedAt() { return escalatedAt; }
    public LocalDateTime getCreatedAt() { return createdAt; }
    public LocalDateTime getUpdatedAt() { return updatedAt; }
    public String getSlaStatus() { return slaStatus; }
}
