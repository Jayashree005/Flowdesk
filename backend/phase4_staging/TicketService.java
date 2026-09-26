package com.flowdesk.service;

import com.flowdesk.dto.*;
import com.flowdesk.entity.*;
import com.flowdesk.enums.Category;
import com.flowdesk.enums.Role;
import com.flowdesk.enums.Severity;
import com.flowdesk.enums.Status;
import com.flowdesk.exception.BusinessException;
import com.flowdesk.exception.ResourceNotFoundException;
import com.flowdesk.repository.*;
import com.flowdesk.workflow.WorkflowEngine;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class TicketService {

    private final TicketRepository ticketRepository;
    private final UserRepository userRepository;
    private final DepartmentRepository departmentRepository;
    private final CommentRepository commentRepository;
    private final TicketHistoryRepository ticketHistoryRepository;
    private final WorkflowRuleRepository workflowRuleRepository;
    private final WorkflowEngine workflowEngine;
    private final SlaService slaService;

    public TicketService(
            TicketRepository ticketRepository,
            UserRepository userRepository,
            DepartmentRepository departmentRepository,
            CommentRepository commentRepository,
            TicketHistoryRepository ticketHistoryRepository,
            WorkflowRuleRepository workflowRuleRepository,
            WorkflowEngine workflowEngine,
            SlaService slaService) {
        this.ticketRepository = ticketRepository;
        this.userRepository = userRepository;
        this.departmentRepository = departmentRepository;
        this.commentRepository = commentRepository;
        this.ticketHistoryRepository = ticketHistoryRepository;
        this.workflowRuleRepository = workflowRuleRepository;
        this.workflowEngine = workflowEngine;
        this.slaService = slaService;
    }

    @Transactional
    public TicketResponseDto createTicket(TicketCreateDto dto) {
        // Resolve requester
        User requester;
        if (dto.getRequesterId() != null) {
            requester = userRepository.findById(dto.getRequesterId())
                    .orElseThrow(() -> new ResourceNotFoundException("Requester not found with id " + dto.getRequesterId()));
        } else {
            // Default requester fallback (e.g. Rahul)
            requester = userRepository.findAll().stream()
                    .filter(u -> u.getRole() == Role.REQUESTER)
                    .findFirst()
                    .orElseGet(() -> userRepository.findAll().stream().findFirst()
                            .orElseThrow(() -> new BusinessException("No users registered in system.")));
        }

        // Department determination
        Department department = null;
        if (dto.getDepartmentId() != null) {
            department = departmentRepository.findById(dto.getDepartmentId()).orElse(null);
        }

        // Evaluate Workflow Rule for SLA & Escalation target
        int slaHours = dto.getSeverity().getDefaultSlaHours();
        String escalationTarget = "Team Lead";

        Optional<WorkflowRule> matchedRule = Optional.empty();
        if (department != null) {
            matchedRule = workflowRuleRepository.findByCategoryAndSeverityAndDepartment(
                    dto.getCategory(), dto.getSeverity(), department);
        }
        if (matchedRule.isEmpty()) {
            matchedRule = workflowRuleRepository.findFirstByCategoryAndSeverity(dto.getCategory(), dto.getSeverity());
        }

        if (matchedRule.isPresent()) {
            WorkflowRule rule = matchedRule.get();
            slaHours = rule.getSlaHours();
            escalationTarget = rule.getEscalationTarget();
            if (department == null && rule.getDepartment() != null) {
                department = rule.getDepartment();
            }
        } else if (department == null) {
            // Default to IT Support or first matching department
            department = departmentRepository.findByCodeIgnoreCase(dto.getCategory().name())
                    .orElseGet(() -> departmentRepository.findAll().stream().findFirst().orElse(null));
        }

        LocalDateTime now = LocalDateTime.now();
        LocalDateTime deadline = slaService.calculateDeadline(now, slaHours);

        // Generate Ticket Number (e.g. FD-1045)
        long count = ticketRepository.count();
        String ticketNumber = "FD-" + (1000 + count + 1);

        Ticket ticket = new Ticket();
        ticket.setTicketNumber(ticketNumber);
        ticket.setTitle(dto.getTitle());
        ticket.setDescription(dto.getDescription());
        ticket.setCategory(dto.getCategory());
        ticket.setSeverity(dto.getSeverity());
        ticket.setStatus(Status.OPEN);
        ticket.setRequester(requester);
        ticket.setDepartment(department);
        ticket.setSlaDeadline(deadline);
        ticket.setEscalationTarget(escalationTarget);
        ticket.setCreatedAt(now);
        ticket.setUpdatedAt(now);

        Ticket saved = ticketRepository.save(ticket);

        // Record history
        TicketHistory history = new TicketHistory(
                saved,
                "TICKET_CREATED",
                null,
                Status.OPEN,
                requester.getName(),
                "Ticket created under category " + dto.getCategory() + " with " + dto.getSeverity() + " severity. SLA: " + slaHours + " hours."
        );
        ticketHistoryRepository.save(history);

        return mapToDto(saved, true);
    }

    @Transactional
    public TicketResponseDto updateStatus(Long ticketId, StatusUpdateDto dto) {
        Ticket ticket = ticketRepository.findById(ticketId)
                .orElseThrow(() -> new ResourceNotFoundException("Ticket not found with id " + ticketId));

        Status currentStatus = ticket.getStatus();
        Status requestedStatus = dto.getStatus();

        // Strict state machine validation via WorkflowEngine!
        workflowEngine.validateTransition(currentStatus, requestedStatus);

        ticket.setStatus(requestedStatus);
        ticket.setUpdatedAt(LocalDateTime.now());
        Ticket updated = ticketRepository.save(ticket);

        String actor = dto.getPerformedBy() != null && !dto.getPerformedBy().isBlank() ? dto.getPerformedBy() : "Admin";
        String details = "Status changed: " + currentStatus + " -> " + requestedStatus;
        if (dto.getComment() != null && !dto.getComment().isBlank()) {
            details += ". Note: " + dto.getComment();
        }

        TicketHistory history = new TicketHistory(
                updated,
                "STATUS_CHANGED",
                currentStatus,
                requestedStatus,
                actor,
                details
        );
        ticketHistoryRepository.save(history);

        return mapToDto(updated, true);
    }

    @Transactional
    public TicketResponseDto assignTicket(Long ticketId, AssignDto dto) {
        Ticket ticket = ticketRepository.findById(ticketId)
                .orElseThrow(() -> new ResourceNotFoundException("Ticket not found with id " + ticketId));

        User agent = userRepository.findById(dto.getAgentId())
                .orElseThrow(() -> new ResourceNotFoundException("Agent not found with id " + dto.getAgentId()));

        Department department = ticket.getDepartment();
        if (dto.getDepartmentId() != null) {
            department = departmentRepository.findById(dto.getDepartmentId()).orElse(department);
        } else if (agent.getDepartment() != null) {
            department = agent.getDepartment();
        }

        ticket.setAssignedAgent(agent);
        ticket.setDepartment(department);

        Status prevStatus = ticket.getStatus();
        // If ticket is OPEN, assigning it moves it to ASSIGNED via workflow engine
        if (ticket.getStatus() == Status.OPEN) {
            workflowEngine.validateTransition(Status.OPEN, Status.ASSIGNED);
            ticket.setStatus(Status.ASSIGNED);
        }

        ticket.setUpdatedAt(LocalDateTime.now());
        Ticket updated = ticketRepository.save(ticket);

        String actor = dto.getPerformedBy() != null && !dto.getPerformedBy().isBlank() ? dto.getPerformedBy() : "Admin";
        TicketHistory history = new TicketHistory(
                updated,
                "TICKET_ASSIGNED",
                prevStatus,
                updated.getStatus(),
                actor,
                "Ticket assigned to " + agent.getName() + (department != null ? " (" + department.getName() + ")" : "")
        );
        ticketHistoryRepository.save(history);

        return mapToDto(updated, true);
    }

    @Transactional
    public CommentDto addComment(Long ticketId, CommentCreateDto dto) {
        Ticket ticket = ticketRepository.findById(ticketId)
                .orElseThrow(() -> new ResourceNotFoundException("Ticket not found with id " + ticketId));

        User author = userRepository.findById(dto.getAuthorId())
                .orElseThrow(() -> new ResourceNotFoundException("Author not found with id " + dto.getAuthorId()));

        Comment comment = new Comment(ticket, author, dto.getContent());
        Comment saved = commentRepository.save(comment);

        ticket.setUpdatedAt(LocalDateTime.now());
        ticketRepository.save(ticket);

        TicketHistory history = new TicketHistory(
                ticket,
                "COMMENT_ADDED",
                ticket.getStatus(),
                ticket.getStatus(),
                author.getName(),
                "Added comment: \"" + (dto.getContent().length() > 60 ? dto.getContent().substring(0, 57) + "..." : dto.getContent()) + "\""
        );
        ticketHistoryRepository.save(history);

        return mapCommentToDto(saved);
    }

    @Transactional
    public int checkAndEscalateAllBreached() {
        LocalDateTime now = LocalDateTime.now();
        List<Ticket> allTickets = ticketRepository.findAll();
        int escalatedCount = 0;
        for (Ticket ticket : allTickets) {
            if (slaService.checkAndEscalate(ticket, now)) {
                escalatedCount++;
            }
        }
        return escalatedCount;
    }

    public List<TicketResponseDto> searchTickets(Category category, Severity severity, Status status, String search) {
        List<Ticket> tickets = ticketRepository.searchTickets(category, severity, status, search);
        return tickets.stream().map(t -> mapToDto(t, false)).collect(Collectors.toList());
    }

    public TicketResponseDto getTicketById(Long id) {
        Ticket ticket = ticketRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Ticket not found with id " + id));
        return mapToDto(ticket, true);
    }

    public TicketResponseDto getTicketByNumber(String ticketNumber) {
        Ticket ticket = ticketRepository.findByTicketNumberIgnoreCase(ticketNumber)
                .orElseThrow(() -> new ResourceNotFoundException("Ticket not found with number " + ticketNumber));
        return mapToDto(ticket, true);
    }

    public List<TicketHistoryDto> getTicketHistory(Long ticketId) {
        return ticketHistoryRepository.findByTicketIdOrderByTimestampDesc(ticketId).stream()
                .map(this::mapHistoryToDto)
                .collect(Collectors.toList());
    }

    public List<TicketHistoryDto> getRecentAuditHistory() {
        return ticketHistoryRepository.findTop50ByOrderByTimestampDesc().stream()
                .map(this::mapHistoryToDto)
                .collect(Collectors.toList());
    }

    public TicketResponseDto mapToDto(Ticket ticket, boolean includeRelations) {
        LocalDateTime now = LocalDateTime.now();
        TicketResponseDto dto = new TicketResponseDto();
        dto.setId(ticket.getId());
        dto.setTicketNumber(ticket.getTicketNumber());
        dto.setTitle(ticket.getTitle());
        dto.setDescription(ticket.getDescription());
        dto.setCategory(ticket.getCategory());
        dto.setSeverity(ticket.getSeverity());
        dto.setStatus(ticket.getStatus());

        if (ticket.getRequester() != null) {
            dto.setRequester(mapUserToDto(ticket.getRequester()));
        }
        if (ticket.getAssignedAgent() != null) {
            dto.setAssignedAgent(mapUserToDto(ticket.getAssignedAgent()));
        }
        if (ticket.getDepartment() != null) {
            dto.setDepartment(new DepartmentDto(
                    ticket.getDepartment().getId(),
                    ticket.getDepartment().getName(),
                    ticket.getDepartment().getCode(),
                    ticket.getDepartment().getLeadName()
            ));
        }

        dto.setSlaDeadline(ticket.getSlaDeadline());
        dto.setSlaStatus(slaService.computeSlaStatus(ticket, now));
        dto.setSlaRemainingMinutes(slaService.computeRemainingMinutes(ticket, now));
        dto.setSlaRemainingText(slaService.computeRemainingText(ticket, now));
        dto.setSlaProgressPercent(slaService.computeProgressPercent(ticket, now));
        dto.setEscalationTarget(ticket.getEscalationTarget());
        dto.setEscalatedAt(ticket.getEscalatedAt());
        dto.setEscalatedBy(ticket.getEscalatedBy());
        dto.setCreatedAt(ticket.getCreatedAt());
        dto.setUpdatedAt(ticket.getUpdatedAt());

        // Allowed transitions calculated by WorkflowEngine
        dto.setAllowedTransitions(workflowEngine.getAllowedNextStatuses(ticket.getStatus()));

        if (includeRelations) {
            List<Comment> comments = commentRepository.findByTicketIdOrderByCreatedAtAsc(ticket.getId());
            dto.setComments(comments.stream().map(this::mapCommentToDto).collect(Collectors.toList()));

            List<TicketHistory> histories = ticketHistoryRepository.findByTicketIdOrderByTimestampDesc(ticket.getId());
            dto.setHistory(histories.stream().map(this::mapHistoryToDto).collect(Collectors.toList()));
        }

        return dto;
    }

    private UserDto mapUserToDto(User u) {
        return new UserDto(
                u.getId(),
                u.getName(),
                u.getEmail(),
                u.getRole(),
                u.getDepartment() != null ? u.getDepartment().getName() : null
        );
    }

    private CommentDto mapCommentToDto(Comment c) {
        return new CommentDto(
                c.getId(),
                c.getTicket().getId(),
                c.getAuthor() != null ? mapUserToDto(c.getAuthor()) : null,
                c.getContent(),
                c.getCreatedAt()
        );
    }

    private TicketHistoryDto mapHistoryToDto(TicketHistory h) {
        return new TicketHistoryDto(
                h.getId(),
                h.getTicket().getId(),
                h.getAction(),
                h.getFromStatus(),
                h.getToStatus(),
                h.getPerformedBy(),
                h.getDetails(),
                h.getTimestamp()
        );
    }
}
