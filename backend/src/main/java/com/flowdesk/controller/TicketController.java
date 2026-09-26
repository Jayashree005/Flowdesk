package com.flowdesk.controller;

import com.flowdesk.dto.AssignTicketRequest;
import com.flowdesk.dto.CreateTicketRequest;
import com.flowdesk.dto.HistoryResponse;
import com.flowdesk.dto.TicketResponse;
import com.flowdesk.dto.UpdateStatusRequest;
import com.flowdesk.entity.Ticket;
import com.flowdesk.service.HistoryService;
import com.flowdesk.service.TicketService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/tickets")
@CrossOrigin(origins = "*")
public class TicketController {

    private final TicketService ticketService;
    private final HistoryService historyService;

    public TicketController(TicketService ticketService, HistoryService historyService) {
        this.ticketService = ticketService;
        this.historyService = historyService;
    }

    @PostMapping
    public ResponseEntity<TicketResponse> createTicket(
            @Valid @RequestBody CreateTicketRequest request
    ) {
        Ticket ticket = ticketService.createTicket(
                request.getTitle(),
                request.getDescription(),
                request.getCategory(),
                request.getSeverity(),
                request.getRequesterId(),
                request.getDepartmentId()
        );
        return ResponseEntity.ok(ticketService.toResponse(ticket));
    }

    @GetMapping
    public ResponseEntity<List<TicketResponse>> getAllTickets() {
        return ResponseEntity.ok(ticketService.getAllTicketResponses());
    }

    @GetMapping("/{id}")
    public ResponseEntity<TicketResponse> getTicket(@PathVariable Long id) {
        return ResponseEntity.ok(ticketService.getTicketResponse(id));
    }

    @PatchMapping("/{id}/status")
    public ResponseEntity<TicketResponse> changeStatus(
            @PathVariable Long id,
            @Valid @RequestBody UpdateStatusRequest request
    ) {
        Ticket updated = ticketService.changeStatus(
                id,
                request.getStatus(),
                request.getChangedBy()
        );
        return ResponseEntity.ok(ticketService.toResponse(updated));
    }

    @PatchMapping("/{id}/assign")
    public ResponseEntity<TicketResponse> assignTicket(
            @PathVariable Long id,
            @Valid @RequestBody AssignTicketRequest request
    ) {
        Ticket updated = ticketService.assignTicket(
                id,
                request.getAgentId(),
                request.getChangedBy()
        );
        return ResponseEntity.ok(ticketService.toResponse(updated));
    }

    @PostMapping("/{id}/check-sla")
    public ResponseEntity<TicketResponse> checkSla(@PathVariable Long id) {
        Ticket ticket = ticketService.checkAndEscalate(id);
        return ResponseEntity.ok(ticketService.toResponse(ticket));
    }

    @GetMapping("/{id}/sla-status")
    public ResponseEntity<String> getSlaStatus(@PathVariable Long id) {
        return ResponseEntity.ok(ticketService.getSlaStatus(id));
    }

    @GetMapping("/{id}/history")
    public ResponseEntity<List<HistoryResponse>> getTicketHistory(@PathVariable Long id) {
        return ResponseEntity.ok(historyService.getHistory(id));
    }
}
