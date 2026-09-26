package com.flowdesk.dto;

import com.flowdesk.enums.Category;
import com.flowdesk.enums.Severity;
import com.flowdesk.enums.Status;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;

public class TicketResponseDto {

    private Long id;
    private String ticketNumber;
    private String title;
    private String description;
    private Category category;
    private Severity severity;
    private Status status;

    private UserDto requester;
    private UserDto assignedAgent;
    private DepartmentDto department;

    private LocalDateTime slaDeadline;
    private String slaStatus;           // HEALTHY, AT_RISK, BREACHED, COMPLETED
    private long slaRemainingMinutes;
    private String slaRemainingText;    // e.g. "18m remaining", "4h remaining", "Breached by 25m"
    private int slaProgressPercent;     // 0 - 100%
    private String escalationTarget;
    private LocalDateTime escalatedAt;
    private String escalatedBy;

    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    private Set<Status> allowedTransitions;
    private List<CommentDto> comments;
    private List<TicketHistoryDto> history;

    public TicketResponseDto() {}

    // Getters and Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getTicketNumber() { return ticketNumber; }
    public void setTicketNumber(String ticketNumber) { this.ticketNumber = ticketNumber; }

    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public Category getCategory() { return category; }
    public void setCategory(Category category) { this.category = category; }

    public Severity getSeverity() { return severity; }
    public void setSeverity(Severity severity) { this.severity = severity; }

    public Status getStatus() { return status; }
    public void setStatus(Status status) { this.status = status; }

    public UserDto getRequester() { return requester; }
    public void setRequester(UserDto requester) { this.requester = requester; }

    public UserDto getAssignedAgent() { return assignedAgent; }
    public void setAssignedAgent(UserDto assignedAgent) { this.assignedAgent = assignedAgent; }

    public DepartmentDto getDepartment() { return department; }
    public void setDepartment(DepartmentDto department) { this.department = department; }

    public LocalDateTime getSlaDeadline() { return slaDeadline; }
    public void setSlaDeadline(LocalDateTime slaDeadline) { this.slaDeadline = slaDeadline; }

    public String getSlaStatus() { return slaStatus; }
    public void setSlaStatus(String slaStatus) { this.slaStatus = slaStatus; }

    public long getSlaRemainingMinutes() { return slaRemainingMinutes; }
    public void setSlaRemainingMinutes(long slaRemainingMinutes) { this.slaRemainingMinutes = slaRemainingMinutes; }

    public String getSlaRemainingText() { return slaRemainingText; }
    public void setSlaRemainingText(String slaRemainingText) { this.slaRemainingText = slaRemainingText; }

    public int getSlaProgressPercent() { return slaProgressPercent; }
    public void setSlaProgressPercent(int slaProgressPercent) { this.slaProgressPercent = slaProgressPercent; }

    public String getEscalationTarget() { return escalationTarget; }
    public void setEscalationTarget(String escalationTarget) { this.escalationTarget = escalationTarget; }

    public LocalDateTime getEscalatedAt() { return escalatedAt; }
    public void setEscalatedAt(LocalDateTime escalatedAt) { this.escalatedAt = escalatedAt; }

    public String getEscalatedBy() { return escalatedBy; }
    public void setEscalatedBy(String escalatedBy) { this.escalatedBy = escalatedBy; }

    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }

    public LocalDateTime getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(LocalDateTime updatedAt) { this.updatedAt = updatedAt; }

    public Set<Status> getAllowedTransitions() { return allowedTransitions; }
    public void setAllowedTransitions(Set<Status> allowedTransitions) { this.allowedTransitions = allowedTransitions; }

    public List<CommentDto> getComments() { return comments; }
    public void setComments(List<CommentDto> comments) { this.comments = comments; }

    public List<TicketHistoryDto> getHistory() { return history; }
    public void setHistory(List<TicketHistoryDto> history) { this.history = history; }
}
