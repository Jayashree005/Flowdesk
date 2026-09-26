package com.flowdesk.service;

import com.flowdesk.dto.CreateTicketRequest;
import com.flowdesk.dto.TicketResponse;
import com.flowdesk.entity.*;
import com.flowdesk.enums.Role;
import com.flowdesk.enums.Severity;
import com.flowdesk.enums.TicketCategory;
import com.flowdesk.enums.TicketStatus;
import com.flowdesk.exception.BusinessException;
import com.flowdesk.exception.ResourceNotFoundException;
import com.flowdesk.repository.*;
import com.flowdesk.workflow.WorkflowEngine;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class TicketService {

    private final TicketRepository ticketRepository;
    private final UserRepository userRepository;
    private final DepartmentRepository departmentRepository;
    private final HistoryService historyService;
    private final SlaService slaService;
    private final WorkflowEngine workflowEngine;

    public TicketService(
            TicketRepository ticketRepository,
            UserRepository userRepository,
            DepartmentRepository departmentRepository,
            HistoryService historyService,
            SlaService slaService,
            WorkflowEngine workflowEngine) {
        this.ticketRepository = ticketRepository;
        this.userRepository = userRepository;
        this.departmentRepository = departmentRepository;
        this.historyService = historyService;
        this.slaService = slaService;
        this.workflowEngine = workflowEngine;
    }

    @Transactional
    public Ticket createTicket(
            String title,
            String description,
            TicketCategory category,
            Severity severity,
            Long requesterId,
            Long departmentId
    ) {

        User requester = userRepository.findById(requesterId)
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                "Requester not found"
                        ));

        Department department = departmentRepository.findById(departmentId)
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                "Department not found"
                        ));

        LocalDateTime now = LocalDateTime.now();

        Ticket ticket = Ticket.builder()
                .ticketNumber(generateTicketNumber())
                .title(title)
                .description(description)
                .category(category)
                .severity(severity)
                .status(TicketStatus.OPEN)
                .requester(requester)
                .department(department)
                .slaDeadline(
                        slaService.calculateDeadline(severity, now)
                )
                .createdAt(now)
                .updatedAt(now)
                .build();

        Ticket savedTicket = ticketRepository.save(ticket);

        historyService.record(
                savedTicket,
                requester,
                "CREATED",
                null,
                "OPEN"
        );

        return savedTicket;
    }

    private String generateTicketNumber() {
        long nextNumber = ticketRepository.count() + 1001;
        return "FD-" + nextNumber;
    }

    @Transactional
    public Ticket changeStatus(
            Long ticketId,
            TicketStatus newStatus,
            Long changedByUserId
    ) {

        Ticket ticket = ticketRepository.findById(ticketId)
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                "Ticket not found"
                        ));

        User changedBy = userRepository.findById(changedByUserId)
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                "User not found"
                        ));

        TicketStatus oldStatus = ticket.getStatus();

        workflowEngine.validateTransition(
                ticket.getCategory(),
                ticket.getSeverity(),
                oldStatus,
                newStatus
        );

        ticket.setStatus(newStatus);
        ticket.setUpdatedAt(LocalDateTime.now());

        Ticket savedTicket = ticketRepository.save(ticket);

        historyService.record(
                savedTicket,
                changedBy,
                "STATUS_CHANGED",
                oldStatus.name(),
                newStatus.name()
        );

        return savedTicket;
    }

    @Transactional
    public Ticket assignTicket(
            Long ticketId,
            Long agentId,
            Long changedByUserId
    ) {

        Ticket ticket = ticketRepository.findById(ticketId)
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                "Ticket not found"
                        ));

        User agent = userRepository.findById(agentId)
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                "Agent not found"
                        ));

        User changedBy = userRepository.findById(changedByUserId)
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                "User not found"
                        ));

        if (agent.getRole() != Role.AGENT) {
            throw new BusinessException(
                    "Only an agent can be assigned to a ticket."
            );
        }

        String oldAgent = ticket.getAssignedAgent() == null
                ? null
                : ticket.getAssignedAgent().getName();

        if (ticket.getStatus() == TicketStatus.OPEN) {
            workflowEngine.validateTransition(
                    ticket.getCategory(),
                    ticket.getSeverity(),
                    TicketStatus.OPEN,
                    TicketStatus.ASSIGNED
            );

            ticket.setStatus(TicketStatus.ASSIGNED);

            historyService.record(
                    ticket,
                    changedBy,
                    "STATUS_CHANGED",
                    TicketStatus.OPEN.name(),
                    TicketStatus.ASSIGNED.name()
            );
        }

        ticket.setAssignedAgent(agent);
        ticket.setUpdatedAt(LocalDateTime.now());

        Ticket savedTicket = ticketRepository.save(ticket);

        historyService.record(
                savedTicket,
                changedBy,
                "ASSIGNED",
                oldAgent,
                agent.getName()
        );

        return savedTicket;
    }

    @Transactional
    public Ticket checkAndEscalate(Long ticketId) {

        Ticket ticket = ticketRepository.findById(ticketId)
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                "Ticket not found"
                        ));

        if (!slaService.isBreached(ticket.getSlaDeadline())) {
            return ticket;
        }

        if (ticket.getStatus() == TicketStatus.CLOSED) {
            return ticket;
        }

        if (ticket.getStatus() == TicketStatus.ESCALATED) {
            return ticket;
        }

        TicketStatus oldStatus = ticket.getStatus();

        ticket.setStatus(TicketStatus.ESCALATED);
        ticket.setEscalatedAt(LocalDateTime.now());
        ticket.setUpdatedAt(LocalDateTime.now());

        Ticket savedTicket = ticketRepository.save(ticket);

        historyService.record(
                savedTicket,
                null,
                "SLA_BREACHED",
                oldStatus.name(),
                "ESCALATED"
        );

        return savedTicket;
    }

    @Transactional
    public Ticket createTicket(CreateTicketRequest request) {
        return createTicket(
                request.getTitle(),
                request.getDescription(),
                request.getCategory(),
                request.getSeverity(),
                request.getRequesterId(),
                request.getDepartmentId()
        );
    }

    public List<Ticket> getAllTickets() {
        return ticketRepository.findAll();
    }

    public Ticket getTicket(Long id) {
        return ticketRepository.findById(id)
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                "Ticket not found"
                        ));
    }

    public String getSlaStatus(Long ticketId) {
        Ticket ticket = getTicket(ticketId);
        return slaService.getSlaStatus(
                ticket.getSlaDeadline()
        );
    }

    public TicketResponse toResponse(Ticket ticket) {
        return TicketResponse.builder()
                .id(ticket.getId())
                .ticketNumber(ticket.getTicketNumber())
                .title(ticket.getTitle())
                .description(ticket.getDescription())
                .category(ticket.getCategory())
                .severity(ticket.getSeverity())
                .status(ticket.getStatus())
                .requesterId(ticket.getRequester() != null ? ticket.getRequester().getId() : null)
                .requesterName(ticket.getRequester() != null ? ticket.getRequester().getName() : null)
                .assignedAgentId(ticket.getAssignedAgent() != null ? ticket.getAssignedAgent().getId() : null)
                .assignedAgentName(ticket.getAssignedAgent() != null ? ticket.getAssignedAgent().getName() : null)
                .departmentId(ticket.getDepartment() != null ? ticket.getDepartment().getId() : null)
                .departmentName(ticket.getDepartment() != null ? ticket.getDepartment().getName() : null)
                .slaDeadline(ticket.getSlaDeadline())
                .escalatedAt(ticket.getEscalatedAt())
                .createdAt(ticket.getCreatedAt())
                .updatedAt(ticket.getUpdatedAt())
                .slaStatus(slaService.getSlaStatus(ticket.getSlaDeadline()))
                .build();
    }

    public List<TicketResponse> getAllTicketResponses() {
        return ticketRepository.findAll().stream()
                .map(this::toResponse)
                .toList();
    }

    public TicketResponse getTicketResponse(Long id) {
        Ticket ticket = getTicket(id);
        return toResponse(ticket);
    }
}
