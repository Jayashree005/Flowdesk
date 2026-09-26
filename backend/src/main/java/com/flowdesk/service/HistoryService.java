package com.flowdesk.service;

import com.flowdesk.entity.Ticket;
import com.flowdesk.entity.TicketHistory;
import com.flowdesk.entity.User;
import com.flowdesk.repository.TicketHistoryRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class HistoryService {

    private final TicketHistoryRepository historyRepository;

    public HistoryService(TicketHistoryRepository historyRepository) {
        this.historyRepository = historyRepository;
    }

    public void record(
            Ticket ticket,
            User user,
            String action,
            String oldValue,
            String newValue
    ) {

        TicketHistory history = TicketHistory.builder()
                .ticket(ticket)
                .changedBy(user)
                .action(action)
                .oldValue(oldValue)
                .newValue(newValue)
                .changedAt(LocalDateTime.now())
                .build();

        historyRepository.save(history);
    }

    public List<com.flowdesk.dto.HistoryResponse> getHistory(Long ticketId) {
        return historyRepository
                .findByTicketIdOrderByChangedAtAsc(ticketId)
                .stream()
                .map(this::toResponse)
                .toList();
    }

    private com.flowdesk.dto.HistoryResponse toResponse(TicketHistory history) {
        return com.flowdesk.dto.HistoryResponse.builder()
                .id(history.getId())
                .action(history.getAction())
                .oldValue(history.getOldValue())
                .newValue(history.getNewValue())
                .changedBy(history.getChangedBy() != null ? history.getChangedBy().getName() : "SYSTEM")
                .changedAt(history.getChangedAt())
                .build();
    }
}
