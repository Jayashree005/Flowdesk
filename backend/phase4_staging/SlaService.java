package com.flowdesk.service;

import com.flowdesk.entity.Ticket;
import com.flowdesk.entity.TicketHistory;
import com.flowdesk.enums.Severity;
import com.flowdesk.enums.Status;
import com.flowdesk.repository.TicketHistoryRepository;
import com.flowdesk.repository.TicketRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Duration;
import java.time.LocalDateTime;

@Service
public class SlaService {

    private final TicketRepository ticketRepository;
    private final TicketHistoryRepository ticketHistoryRepository;

    public SlaService(TicketRepository ticketRepository, TicketHistoryRepository ticketHistoryRepository) {
        this.ticketRepository = ticketRepository;
        this.ticketHistoryRepository = ticketHistoryRepository;
    }

    public LocalDateTime calculateDeadline(LocalDateTime startTime, int slaHours) {
        if (startTime == null) startTime = LocalDateTime.now();
        return startTime.plusHours(slaHours);
    }

    public String computeSlaStatus(Ticket ticket, LocalDateTime now) {
        if (ticket.getStatus() == Status.RESOLVED || ticket.getStatus() == Status.CLOSED) {
            return "COMPLETED";
        }
        if (ticket.getStatus() == Status.ESCALATED || now.isAfter(ticket.getSlaDeadline())) {
            return "BREACHED";
        }
        Duration remaining = Duration.between(now, ticket.getSlaDeadline());
        Duration total = Duration.between(ticket.getCreatedAt(), ticket.getSlaDeadline());

        if (total.isZero() || total.isNegative()) {
            return "BREACHED";
        }

        // At risk if less than 25% of total time left, or less than 60 minutes
        if (remaining.toMinutes() <= 60 || ((double) remaining.toMinutes() / total.toMinutes()) <= 0.25) {
            return "AT_RISK";
        }
        return "HEALTHY";
    }

    public long computeRemainingMinutes(Ticket ticket, LocalDateTime now) {
        if (ticket.getStatus() == Status.RESOLVED || ticket.getStatus() == Status.CLOSED) {
            return 0;
        }
        return Duration.between(now, ticket.getSlaDeadline()).toMinutes();
    }

    public String computeRemainingText(Ticket ticket, LocalDateTime now) {
        if (ticket.getStatus() == Status.RESOLVED) {
            return "Resolved within SLA";
        }
        if (ticket.getStatus() == Status.CLOSED) {
            return "Closed";
        }
        if (now.isAfter(ticket.getSlaDeadline())) {
            long overdueMinutes = Duration.between(ticket.getSlaDeadline(), now).toMinutes();
            if (overdueMinutes < 60) {
                return "Breached by " + overdueMinutes + "m";
            }
            long hours = overdueMinutes / 60;
            long mins = overdueMinutes % 60;
            return "Breached by " + hours + "h " + (mins > 0 ? mins + "m" : "");
        }

        long remainingMinutes = Duration.between(now, ticket.getSlaDeadline()).toMinutes();
        if (remainingMinutes < 60) {
            return remainingMinutes + "m remaining";
        }
        long hours = remainingMinutes / 60;
        long mins = remainingMinutes % 60;
        return hours + "h " + (mins > 0 ? mins + "m remaining" : "remaining");
    }

    public int computeProgressPercent(Ticket ticket, LocalDateTime now) {
        if (ticket.getStatus() == Status.RESOLVED || ticket.getStatus() == Status.CLOSED) {
            return 100;
        }
        LocalDateTime start = ticket.getCreatedAt();
        LocalDateTime deadline = ticket.getSlaDeadline();
        long totalSeconds = Duration.between(start, deadline).getSeconds();
        if (totalSeconds <= 0) return 100;

        long elapsedSeconds = Duration.between(start, now).getSeconds();
        if (elapsedSeconds <= 0) return 0;
        if (elapsedSeconds >= totalSeconds) return 100;

        return (int) Math.min(100, Math.max(0, (elapsedSeconds * 100) / totalSeconds));
    }

    @Transactional
    public boolean checkAndEscalate(Ticket ticket, LocalDateTime now) {
        if (ticket.getStatus() == Status.RESOLVED ||
            ticket.getStatus() == Status.CLOSED ||
            ticket.getStatus() == Status.ESCALATED) {
            return false;
        }

        if (now.isAfter(ticket.getSlaDeadline())) {
            Status prevStatus = ticket.getStatus();
            ticket.setStatus(Status.ESCALATED);
            ticket.setEscalatedAt(now);
            ticket.setEscalatedBy("SYSTEM");
            ticketRepository.save(ticket);

            TicketHistory history = new TicketHistory(
                    ticket,
                    "SLA_ESCALATED",
                    prevStatus,
                    Status.ESCALATED,
                    "SYSTEM",
                    "SLA deadline exceeded (" + ticket.getSlaDeadline() + "). Ticket escalated automatically to " +
                            (ticket.getEscalationTarget() != null ? ticket.getEscalationTarget() : "Team Lead") + "."
            );
            ticketHistoryRepository.save(history);
            return true;
        }
        return false;
    }
}
