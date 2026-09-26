package com.flowdesk.enums;

public enum Severity {
    LOW(48),
    MEDIUM(24),
    HIGH(8),
    CRITICAL(2);

    private final int defaultSlaHours;

    Severity(int defaultSlaHours) {
        this.defaultSlaHours = defaultSlaHours;
    }

    public int getDefaultSlaHours() {
        return defaultSlaHours;
    }
}
