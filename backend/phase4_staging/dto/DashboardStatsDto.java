package com.flowdesk.dto;

import java.util.List;
import java.util.Map;

public class DashboardStatsDto {
    private long openTicketsCount;
    private long openTicketsTodayDelta;
    private long slaRiskCount;
    private long slaRiskCriticalCount;
    private long escalatedCount;
    private long escalatedNewCount;
    private long resolvedCount;
    private double slaComplianceRate;

    private Map<String, Long> workloadBySeverity;
    private List<TicketResponseDto> slaRiskQueue;
    private List<TicketResponseDto> recentTickets;

    public DashboardStatsDto() {}

    public long getOpenTicketsCount() { return openTicketsCount; }
    public void setOpenTicketsCount(long openTicketsCount) { this.openTicketsCount = openTicketsCount; }

    public long getOpenTicketsTodayDelta() { return openTicketsTodayDelta; }
    public void setOpenTicketsTodayDelta(long openTicketsTodayDelta) { this.openTicketsTodayDelta = openTicketsTodayDelta; }

    public long getSlaRiskCount() { return slaRiskCount; }
    public void setSlaRiskCount(long slaRiskCount) { this.slaRiskCount = slaRiskCount; }

    public long getSlaRiskCriticalCount() { return slaRiskCriticalCount; }
    public void setSlaRiskCriticalCount(long slaRiskCriticalCount) { this.slaRiskCriticalCount = slaRiskCriticalCount; }

    public long getEscalatedCount() { return escalatedCount; }
    public void setEscalatedCount(long escalatedCount) { this.escalatedCount = escalatedCount; }

    public long getEscalatedNewCount() { return escalatedNewCount; }
    public void setEscalatedNewCount(long escalatedNewCount) { this.escalatedNewCount = escalatedNewCount; }

    public long getResolvedCount() { return resolvedCount; }
    public void setResolvedCount(long resolvedCount) { this.resolvedCount = resolvedCount; }

    public double getSlaComplianceRate() { return slaComplianceRate; }
    public void setSlaComplianceRate(double slaComplianceRate) { this.slaComplianceRate = slaComplianceRate; }

    public Map<String, Long> getWorkloadBySeverity() { return workloadBySeverity; }
    public void setWorkloadBySeverity(Map<String, Long> workloadBySeverity) { this.workloadBySeverity = workloadBySeverity; }

    public List<TicketResponseDto> getSlaRiskQueue() { return slaRiskQueue; }
    public void setSlaRiskQueue(List<TicketResponseDto> slaRiskQueue) { this.slaRiskQueue = slaRiskQueue; }

    public List<TicketResponseDto> getRecentTickets() { return recentTickets; }
    public void setRecentTickets(List<TicketResponseDto> recentTickets) { this.recentTickets = recentTickets; }
}
