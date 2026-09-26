package com.flowdesk.service;

import com.flowdesk.dto.DashboardStatsDto;
import com.flowdesk.dto.TicketResponseDto;
import com.flowdesk.entity.Ticket;
import com.flowdesk.enums.Severity;
import com.flowdesk.enums.Status;
import com.flowdesk.repository.TicketRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class DashboardService {

    private final TicketRepository ticketRepository;
    private final TicketService ticketService;

    public DashboardService(TicketRepository ticketRepository, TicketService ticketService) {
        this.ticketRepository = ticketRepository;
        this.ticketService = ticketService;
    }

    public DashboardStatsDto getDashboardMetrics() {
        LocalDateTime now = LocalDateTime.now();
        LocalDateTime startOfToday = LocalDateTime.of(LocalDate.now(), LocalTime.MIN);

        // Run auto escalation check to keep dashboard fresh
        ticketService.checkAndEscalateAllBreached();

        List<Ticket> allTickets = ticketRepository.findAll();

        long openCount = allTickets.stream()
                .filter(t -> t.getStatus() != Status.RESOLVED && t.getStatus() != Status.CLOSED)
                .count();

        long openTodayDelta = ticketRepository.countCreatedSince(startOfToday);

        // SLA Risk: Tickets not resolved/closed that have <= 2 hours remaining or breached
        List<Ticket> activeTickets = allTickets.stream()
                .filter(t -> t.getStatus() != Status.RESOLVED && t.getStatus() != Status.CLOSED)
                .collect(Collectors.toList());

        List<Ticket> slaRiskTickets = activeTickets.stream()
                .filter(t -> t.getStatus() == Status.ESCALATED || t.getSlaDeadline().isBefore(now.plusHours(2)))
                .sorted(Comparator.comparing(Ticket::getSlaDeadline))
                .collect(Collectors.toList());

        long slaRiskCount = slaRiskTickets.size();
        long slaRiskCritical = slaRiskTickets.stream()
                .filter(t -> t.getSeverity() == Severity.CRITICAL)
                .count();

        long escalatedCount = allTickets.stream()
                .filter(t -> t.getStatus() == Status.ESCALATED)
                .count();

        long escalatedNewCount = allTickets.stream()
                .filter(t -> t.getStatus() == Status.ESCALATED && t.getEscalatedAt() != null && t.getEscalatedAt().isAfter(startOfToday))
                .count();

        long resolvedCount = allTickets.stream()
                .filter(t -> t.getStatus() == Status.RESOLVED || t.getStatus() == Status.CLOSED)
                .count();

        // SLA compliance rate: percentage of resolved tickets resolved before deadline
        double complianceRate = allTickets.isEmpty() ? 100.0 :
                Math.round(((double) (allTickets.size() - escalatedCount) / allTickets.size()) * 1000.0) / 10.0;

        // Workload breakdown by severity
        Map<String, Long> workload = new LinkedHashMap<>();
        workload.put("CRITICAL", activeTickets.stream().filter(t -> t.getSeverity() == Severity.CRITICAL).count());
        workload.put("HIGH", activeTickets.stream().filter(t -> t.getSeverity() == Severity.HIGH).count());
        workload.put("MEDIUM", activeTickets.stream().filter(t -> t.getSeverity() == Severity.MEDIUM).count());
        workload.put("LOW", activeTickets.stream().filter(t -> t.getSeverity() == Severity.LOW).count());

        // SLA Risk Queue: top 5 urgent tickets
        List<TicketResponseDto> slaRiskQueue = slaRiskTickets.stream()
                .limit(5)
                .map(t -> ticketService.mapToDto(t, false))
                .collect(Collectors.toList());

        // Recent tickets: top 6 latest
        List<TicketResponseDto> recentTickets = allTickets.stream()
                .sorted(Comparator.comparing(Ticket::getCreatedAt).reversed())
                .limit(6)
                .map(t -> ticketService.mapToDto(t, false))
                .collect(Collectors.toList());

        DashboardStatsDto dto = new DashboardStatsDto();
        dto.setOpenTicketsCount(openCount);
        dto.setOpenTicketsTodayDelta(openTodayDelta);
        dto.setSlaRiskCount(slaRiskCount);
        dto.setSlaRiskCriticalCount(slaRiskCritical);
        dto.setEscalatedCount(escalatedCount);
        dto.setEscalatedNewCount(escalatedNewCount);
        dto.setResolvedCount(resolvedCount);
        dto.setSlaComplianceRate(complianceRate);
        dto.setWorkloadBySeverity(workload);
        dto.setSlaRiskQueue(slaRiskQueue);
        dto.setRecentTickets(recentTickets);

        return dto;
    }
}
