package com.flowdesk.entity;

import com.flowdesk.enums.Severity;
import com.flowdesk.enums.TicketCategory;
import com.flowdesk.enums.TicketStatus;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "workflow_rules")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class WorkflowRule {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private TicketCategory category;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Severity severity;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private TicketStatus fromStatus;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private TicketStatus toStatus;

    @Column(nullable = false)
    private boolean allowed;

    public WorkflowRule() {}

    public WorkflowRule(Long id, TicketCategory category, Severity severity,
                        TicketStatus fromStatus, TicketStatus toStatus, boolean allowed) {
        this.id = id;
        this.category = category;
        this.severity = severity;
        this.fromStatus = fromStatus;
        this.toStatus = toStatus;
        this.allowed = allowed;
    }

    public WorkflowRule(TicketCategory category, Severity severity,
                        TicketStatus fromStatus, TicketStatus toStatus, boolean allowed) {
        this.category = category;
        this.severity = severity;
        this.fromStatus = fromStatus;
        this.toStatus = toStatus;
        this.allowed = allowed;
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public TicketCategory getCategory() { return category; }
    public void setCategory(TicketCategory category) { this.category = category; }

    public Severity getSeverity() { return severity; }
    public void setSeverity(Severity severity) { this.severity = severity; }

    public TicketStatus getFromStatus() { return fromStatus; }
    public void setFromStatus(TicketStatus fromStatus) { this.fromStatus = fromStatus; }

    public TicketStatus getToStatus() { return toStatus; }
    public void setToStatus(TicketStatus toStatus) { this.toStatus = toStatus; }

    public boolean isAllowed() { return allowed; }
    public void setAllowed(boolean allowed) { this.allowed = allowed; }
}
