package com.flowdesk.controller;

import com.flowdesk.dto.*;
import com.flowdesk.enums.Category;
import com.flowdesk.enums.Severity;
import com.flowdesk.enums.Status;
import com.flowdesk.service.TicketService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/tickets")
@CrossOrigin(origins = "*")
public class TicketController {

    private final TicketService ticketService;

    public TicketController(TicketService ticketService) {
        this.ticketService = ticketService;
    }

    @PostMapping
    public ResponseEntity<TicketResponseDto> createTicket(@Valid @RequestBody TicketCreateDto dto) {
        TicketResponseDto created = ticketService.createTicket(dto);
        return ResponseEntity.status(HttpStatus.CREATED).body(created);
    }

    @GetMapping
    public ResponseEntity<List<TicketResponseDto>> getTickets(
            @RequestParam(required = false) Category category,
            @RequestParam(required = false) Severity severity,
            @RequestParam(required = false) Status status,
            @RequestParam(required = false) String search) {
        List<TicketResponseDto> tickets = ticketService.searchTickets(category, severity, status, search);
        return ResponseEntity.ok(tickets);
    }

    @GetMapping("/{id}")
    public ResponseEntity<TicketResponseDto> getTicketById(@PathVariable Long id) {
        return ResponseEntity.ok(ticketService.getTicketById(id));
    }

    @GetMapping("/number/{ticketNumber}")
    public ResponseEntity<TicketResponseDto> getTicketByNumber(@PathVariable String ticketNumber) {
        return ResponseEntity.ok(ticketService.getTicketByNumber(ticketNumber));
    }

    @PatchMapping("/{id}/status")
    public ResponseEntity<TicketResponseDto> updateStatus(
            @PathVariable Long id,
            @Valid @RequestBody StatusUpdateDto dto) {
        TicketResponseDto updated = ticketService.updateStatus(id, dto);
        return ResponseEntity.ok(updated);
    }

    @PatchMapping("/{id}/assign")
    public ResponseEntity<TicketResponseDto> assignTicket(
            @PathVariable Long id,
            @Valid @RequestBody AssignDto dto) {
        TicketResponseDto updated = ticketService.assignTicket(id, dto);
        return ResponseEntity.ok(updated);
    }

    @PostMapping("/{id}/comments")
    public ResponseEntity<CommentDto> addComment(
            @PathVariable Long id,
            @Valid @RequestBody CommentCreateDto dto) {
        CommentDto created = ticketService.addComment(id, dto);
        return ResponseEntity.status(HttpStatus.CREATED).body(created);
    }

    @GetMapping("/{id}/history")
    public ResponseEntity<List<TicketHistoryDto>> getTicketHistory(@PathVariable Long id) {
        return ResponseEntity.ok(ticketService.getTicketHistory(id));
    }

    @PostMapping("/escalate-check")
    public ResponseEntity<Map<String, Object>> runEscalationCheck() {
        int escalated = ticketService.checkAndEscalateAllBreached();
        return ResponseEntity.ok(Map.of(
                "success", true,
                "escalatedCount", escalated,
                "message", escalated > 0 ? escalated + " ticket(s) automatically escalated" : "All tickets within SLA compliance"
        ));
    }
}
