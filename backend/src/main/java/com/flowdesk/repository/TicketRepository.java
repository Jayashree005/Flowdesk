package com.flowdesk.repository;

import com.flowdesk.entity.Ticket;
import com.flowdesk.enums.TicketStatus;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface TicketRepository
        extends JpaRepository<Ticket, Long> {

    List<Ticket> findByStatus(TicketStatus status);
}
