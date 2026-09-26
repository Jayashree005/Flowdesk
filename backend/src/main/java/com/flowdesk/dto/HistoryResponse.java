package com.flowdesk.dto;

import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
@Builder
public class HistoryResponse {

    private Long id;
    private String action;
    private String oldValue;
    private String newValue;
    private String changedBy;
    private LocalDateTime changedAt;

    public HistoryResponse() {}

    public HistoryResponse(Long id, String action, String oldValue, String newValue, String changedBy, LocalDateTime changedAt) {
        this.id = id;
        this.action = action;
        this.oldValue = oldValue;
        this.newValue = newValue;
        this.changedBy = changedBy;
        this.changedAt = changedAt;
    }

    public static HistoryResponseBuilder builder() {
        return new HistoryResponseBuilder();
    }

    public static class HistoryResponseBuilder {
        private Long id;
        private String action;
        private String oldValue;
        private String newValue;
        private String changedBy;
        private LocalDateTime changedAt;

        public HistoryResponseBuilder id(Long id) { this.id = id; return this; }
        public HistoryResponseBuilder action(String action) { this.action = action; return this; }
        public HistoryResponseBuilder oldValue(String oldValue) { this.oldValue = oldValue; return this; }
        public HistoryResponseBuilder newValue(String newValue) { this.newValue = newValue; return this; }
        public HistoryResponseBuilder changedBy(String changedBy) { this.changedBy = changedBy; return this; }
        public HistoryResponseBuilder changedAt(LocalDateTime changedAt) { this.changedAt = changedAt; return this; }

        public HistoryResponse build() {
            return new HistoryResponse(id, action, oldValue, newValue, changedBy, changedAt);
        }
    }

    public Long getId() { return id; }
    public String getAction() { return action; }
    public String getOldValue() { return oldValue; }
    public String getNewValue() { return newValue; }
    public String getChangedBy() { return changedBy; }
    public LocalDateTime getChangedAt() { return changedAt; }
}
