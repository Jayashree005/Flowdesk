package com.flowdesk.service;

import com.flowdesk.entity.*;
import com.flowdesk.enums.Role;
import com.flowdesk.enums.Severity;
import com.flowdesk.enums.TicketCategory;
import com.flowdesk.enums.TicketStatus;
import com.flowdesk.exception.BusinessException;
import com.flowdesk.repository.*;
import com.flowdesk.workflow.WorkflowEngine;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class TicketServiceTest {

    @Mock
    private TicketRepository ticketRepository;
    @Mock
    private UserRepository userRepository;
    @Mock
    private DepartmentRepository departmentRepository;
    @Mock
    private HistoryService historyService;
    @Mock
    private SlaService slaService;
    @Mock
    private WorkflowEngine workflowEngine;

    @InjectMocks
    private TicketService ticketService;

    private User requester;
    private User agent;
    private Department department;

    @BeforeEach
    void setUp() {
        department = new Department(1L, "IT Support");
        requester = new User(1L, "Rahul Verma", "rahul@company.io", Role.REQUESTER, null);
        agent = new User(2L, "Arun Sharma", "arun@flowdesk.io", Role.AGENT, department);
    }

    @Test
    @DisplayName("4.12: Ticket creation generates ticket number, calculates SLA, and records CREATED history")
    void testCreateTicket() {
        when(userRepository.findById(1L)).thenReturn(Optional.of(requester));
        when(departmentRepository.findById(1L)).thenReturn(Optional.of(department));
        when(ticketRepository.count()).thenReturn(0L);
        when(slaService.calculateDeadline(any(), any())).thenReturn(LocalDateTime.now().plusHours(2));
        when(ticketRepository.save(any(Ticket.class))).thenAnswer(i -> {
            Ticket t = i.getArgument(0);
            t.setId(10L);
            return t;
        });

        Ticket ticket = ticketService.createTicket(
                "Production API failure",
                "Gateway 502 error",
                TicketCategory.IT,
                Severity.CRITICAL,
                1L,
                1L
        );

        assertNotNull(ticket);
        assertEquals("FD-1001", ticket.getTicketNumber());
        assertEquals(TicketStatus.OPEN, ticket.getStatus());
        verify(historyService).record(any(Ticket.class), eq(requester), eq("CREATED"), isNull(), eq("OPEN"));
    }

    @Test
    @DisplayName("4.17 & 4.18: Assignment automatically moves ticket from OPEN to ASSIGNED")
    void testAssignTicketAutoStatusChange() {
        Ticket ticket = Ticket.builder()
                .id(1L)
                .ticketNumber("FD-1001")
                .category(TicketCategory.IT)
                .severity(Severity.CRITICAL)
                .status(TicketStatus.OPEN)
                .build();

        when(ticketRepository.findById(1L)).thenReturn(Optional.of(ticket));
        when(userRepository.findById(2L)).thenReturn(Optional.of(agent));
        when(userRepository.findById(1L)).thenReturn(Optional.of(requester));
        when(ticketRepository.save(any(Ticket.class))).thenAnswer(i -> i.getArgument(0));

        Ticket updated = ticketService.assignTicket(1L, 2L, 1L);

        assertEquals(TicketStatus.ASSIGNED, updated.getStatus());
        assertEquals(agent, updated.getAssignedAgent());
        verify(historyService).record(eq(ticket), eq(requester), eq("STATUS_CHANGED"), eq("OPEN"), eq("ASSIGNED"));
        verify(historyService).record(eq(ticket), eq(requester), eq("ASSIGNED"), isNull(), eq("Arun Sharma"));
    }

    @Test
    @DisplayName("4.14: Status change invokes WorkflowEngine validation before saving and recording history")
    void testChangeStatusWithWorkflowValidation() {
        Ticket ticket = Ticket.builder()
                .id(1L)
                .category(TicketCategory.IT)
                .severity(Severity.CRITICAL)
                .status(TicketStatus.IN_PROGRESS)
                .build();

        when(ticketRepository.findById(1L)).thenReturn(Optional.of(ticket));
        when(userRepository.findById(2L)).thenReturn(Optional.of(agent));
        when(ticketRepository.save(any(Ticket.class))).thenAnswer(i -> i.getArgument(0));

        Ticket updated = ticketService.changeStatus(1L, TicketStatus.RESOLVED, 2L);

        verify(workflowEngine).validateTransition(
                TicketCategory.IT,
                Severity.CRITICAL,
                TicketStatus.IN_PROGRESS,
                TicketStatus.RESOLVED
        );
        assertEquals(TicketStatus.RESOLVED, updated.getStatus());
        verify(historyService).record(eq(ticket), eq(agent), eq("STATUS_CHANGED"), eq("IN_PROGRESS"), eq("RESOLVED"));
    }

    @Test
    @DisplayName("4.19 & 4.20: Escalation sets status to ESCALATED and records history with changedBy=null (SYSTEM)")
    void testSlaEscalation() {
        LocalDateTime pastDeadline = LocalDateTime.now().minusMinutes(30);
        Ticket ticket = Ticket.builder()
                .id(1L)
                .status(TicketStatus.IN_PROGRESS)
                .slaDeadline(pastDeadline)
                .build();

        when(ticketRepository.findById(1L)).thenReturn(Optional.of(ticket));
        when(slaService.isBreached(pastDeadline)).thenReturn(true);
        when(ticketRepository.save(any(Ticket.class))).thenAnswer(i -> i.getArgument(0));

        Ticket escalated = ticketService.checkAndEscalate(1L);

        assertEquals(TicketStatus.ESCALATED, escalated.getStatus());
        assertNotNull(escalated.getEscalatedAt());
        verify(historyService).record(eq(ticket), isNull(), eq("SLA_BREACHED"), eq("IN_PROGRESS"), eq("ESCALATED"));
    }
}
