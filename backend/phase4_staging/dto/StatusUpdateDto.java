package com.flowdesk.dto;

import com.flowdesk.enums.Status;
import jakarta.validation.constraints.NotNull;

public class StatusUpdateDto {

    @NotNull(message = "Status is required")
    private Status status;

    private String performedBy;
    private String comment;

    public StatusUpdateDto() {}

    public StatusUpdateDto(Status status, String performedBy, String comment) {
        this.status = status;
        this.performedBy = performedBy;
        this.comment = comment;
    }

    public Status getStatus() { return status; }
    public void setStatus(Status status) { this.status = status; }

    public String getPerformedBy() { return performedBy; }
    public void setPerformedBy(String performedBy) { this.performedBy = performedBy; }

    public String getComment() { return comment; }
    public void setComment(String comment) { this.comment = comment; }
}
