package com.flowdesk.entity;

import com.flowdesk.enums.Severity;
import com.flowdesk.enums.TicketCategory;
import com.flowdesk.enums.TicketStatus;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "tickets")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Ticket {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private String ticketNumber;

    @Column(nullable = false)
    private String title;

    @Column(nullable = false, columnDefinition = "TEXT")
    private String description;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private TicketCategory category;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Severity severity;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private TicketStatus status;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "requester_id", nullable = false)
    private User requester;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "assigned_agent_id")
    private User assignedAgent;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "department_id")
    private Department department;

    @Column(nullable = false)
    private LocalDateTime slaDeadline;

    private LocalDateTime escalatedAt;

    @Column(nullable = false)
    private LocalDateTime createdAt;

    @Column(nullable = false)
    private LocalDateTime updatedAt;

    public Ticket() {}

    public Ticket(Long id, String ticketNumber, String title, String description,
                  TicketCategory category, Severity severity, TicketStatus status,
                  User requester, User assignedAgent, Department department,
                  LocalDateTime slaDeadline, LocalDateTime escalatedAt,
                  LocalDateTime createdAt, LocalDateTime updatedAt) {
        this.id = id;
        this.ticketNumber = ticketNumber;
        this.title = title;
        this.description = description;
        this.category = category;
        this.severity = severity;
        this.status = status;
        this.requester = requester;
        this.assignedAgent = assignedAgent;
        this.department = department;
        this.slaDeadline = slaDeadline;
        this.escalatedAt = escalatedAt;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
    }

    public static TicketBuilder builder() {
        return new TicketBuilder();
    }

    public static class TicketBuilder {
        private Long id;
        private String ticketNumber;
        private String title;
        private String description;
        private TicketCategory category;
        private Severity severity;
        private TicketStatus status;
        private User requester;
        private User assignedAgent;
        private Department department;
        private LocalDateTime slaDeadline;
        private LocalDateTime escalatedAt;
        private LocalDateTime createdAt;
        private LocalDateTime updatedAt;

        public TicketBuilder id(Long id) { this.id = id; return this; }
        public TicketBuilder ticketNumber(String ticketNumber) { this.ticketNumber = ticketNumber; return this; }
        public TicketBuilder title(String title) { this.title = title; return this; }
        public TicketBuilder description(String description) { this.description = description; return this; }
        public TicketBuilder category(TicketCategory category) { this.category = category; return this; }
        public TicketBuilder severity(Severity severity) { this.severity = severity; return this; }
        public TicketBuilder status(TicketStatus status) { this.status = status; return this; }
        public TicketBuilder requester(User requester) { this.requester = requester; return this; }
        public TicketBuilder assignedAgent(User assignedAgent) { this.assignedAgent = assignedAgent; return this; }
        public TicketBuilder department(Department department) { this.department = department; return this; }
        public TicketBuilder slaDeadline(LocalDateTime slaDeadline) { this.slaDeadline = slaDeadline; return this; }
        public TicketBuilder escalatedAt(LocalDateTime escalatedAt) { this.escalatedAt = escalatedAt; return this; }
        public TicketBuilder createdAt(LocalDateTime createdAt) { this.createdAt = createdAt; return this; }
        public TicketBuilder updatedAt(LocalDateTime updatedAt) { this.updatedAt = updatedAt; return this; }

        public Ticket build() {
            return new Ticket(id, ticketNumber, title, description, category, severity, status,
                    requester, assignedAgent, department, slaDeadline, escalatedAt, createdAt, updatedAt);
        }
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getTicketNumber() { return ticketNumber; }
    public void setTicketNumber(String ticketNumber) { this.ticketNumber = ticketNumber; }

    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public TicketCategory getCategory() { return category; }
    public void setCategory(TicketCategory category) { this.category = category; }

    public Severity getSeverity() { return severity; }
    public void setSeverity(Severity severity) { this.severity = severity; }

    public TicketStatus getStatus() { return status; }
    public void setStatus(TicketStatus status) { this.status = status; }

    public User getRequester() { return requester; }
    public void setRequester(User requester) { this.requester = requester; }

    public User getAssignedAgent() { return assignedAgent; }
    public void setAssignedAgent(User assignedAgent) { this.assignedAgent = assignedAgent; }

    public Department getDepartment() { return department; }
    public void setDepartment(Department department) { this.department = department; }

    public LocalDateTime getSlaDeadline() { return slaDeadline; }
    public void setSlaDeadline(LocalDateTime slaDeadline) { this.slaDeadline = slaDeadline; }

    public LocalDateTime getEscalatedAt() { return escalatedAt; }
    public void setEscalatedAt(LocalDateTime escalatedAt) { this.escalatedAt = escalatedAt; }

    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }

    public LocalDateTime getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(LocalDateTime updatedAt) { this.updatedAt = updatedAt; }
}
