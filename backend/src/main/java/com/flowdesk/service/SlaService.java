package com.flowdesk.service;

import com.flowdesk.enums.Severity;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.time.LocalDateTime;

@Service
public class SlaService {

    public LocalDateTime calculateDeadline(
            Severity severity,
            LocalDateTime createdAt
    ) {

        Duration duration = switch (severity) {
            case LOW -> Duration.ofHours(48);
            case MEDIUM -> Duration.ofHours(24);
            case HIGH -> Duration.ofHours(8);
            case CRITICAL -> Duration.ofHours(2);
        };

        return createdAt.plus(duration);
    }

    public boolean isBreached(LocalDateTime deadline) {
        return LocalDateTime.now().isAfter(deadline);
    }

    public String getSlaStatus(LocalDateTime deadline) {

        LocalDateTime now = LocalDateTime.now();

        if (now.isAfter(deadline)) {
            return "BREACHED";
        }

        long minutesRemaining =
                Duration.between(now, deadline).toMinutes();

        if (minutesRemaining <= 60) {
            return "AT_RISK";
        }

        return "ON_TRACK";
    }
}
