package com.flowdesk.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "ticket_history")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class TicketHistory {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "ticket_id", nullable = false)
    private Ticket ticket;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "changed_by")
    private User changedBy;

    @Column(nullable = false)
    private String action;

    private String oldValue;

    private String newValue;

    @Column(nullable = false)
    private LocalDateTime changedAt;

    public TicketHistory() {}

    public TicketHistory(Long id, Ticket ticket, User changedBy, String action, String oldValue, String newValue, LocalDateTime changedAt) {
        this.id = id;
        this.ticket = ticket;
        this.changedBy = changedBy;
        this.action = action;
        this.oldValue = oldValue;
        this.newValue = newValue;
        this.changedAt = changedAt;
    }

    public TicketHistory(Ticket ticket, User changedBy, String action, String oldValue, String newValue) {
        this.ticket = ticket;
        this.changedBy = changedBy;
        this.action = action;
        this.oldValue = oldValue;
        this.newValue = newValue;
        this.changedAt = LocalDateTime.now();
    }

    public static TicketHistoryBuilder builder() {
        return new TicketHistoryBuilder();
    }

    public static class TicketHistoryBuilder {
        private Long id;
        private Ticket ticket;
        private User changedBy;
        private String action;
        private String oldValue;
        private String newValue;
        private LocalDateTime changedAt;

        public TicketHistoryBuilder id(Long id) { this.id = id; return this; }
        public TicketHistoryBuilder ticket(Ticket ticket) { this.ticket = ticket; return this; }
        public TicketHistoryBuilder changedBy(User changedBy) { this.changedBy = changedBy; return this; }
        public TicketHistoryBuilder action(String action) { this.action = action; return this; }
        public TicketHistoryBuilder oldValue(String oldValue) { this.oldValue = oldValue; return this; }
        public TicketHistoryBuilder newValue(String newValue) { this.newValue = newValue; return this; }
        public TicketHistoryBuilder changedAt(LocalDateTime changedAt) { this.changedAt = changedAt; return this; }

        public TicketHistory build() {
            return new TicketHistory(id, ticket, changedBy, action, oldValue, newValue, changedAt);
        }
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public Ticket getTicket() { return ticket; }
    public void setTicket(Ticket ticket) { this.ticket = ticket; }

    public User getChangedBy() { return changedBy; }
    public void setChangedBy(User changedBy) { this.changedBy = changedBy; }

    public String getAction() { return action; }
    public void setAction(String action) { this.action = action; }

    public String getOldValue() { return oldValue; }
    public void setOldValue(String oldValue) { this.oldValue = oldValue; }

    public String getNewValue() { return newValue; }
    public void setNewValue(String newValue) { this.newValue = newValue; }

    public LocalDateTime getChangedAt() { return changedAt; }
    public void setChangedAt(LocalDateTime changedAt) { this.changedAt = changedAt; }
}
