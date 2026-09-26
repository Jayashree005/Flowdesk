package com.flowdesk.dto;

import com.flowdesk.enums.Severity;
import com.flowdesk.enums.TicketCategory;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CreateTicketRequest {

    @NotBlank
    private String title;

    @NotBlank
    private String description;

    @NotNull
    private TicketCategory category;

    @NotNull
    private Severity severity;

    @NotNull
    private Long requesterId;

    @NotNull
    private Long departmentId;

    public CreateTicketRequest() {}

    public CreateTicketRequest(String title, String description, TicketCategory category, Severity severity, Long requesterId, Long departmentId) {
        this.title = title;
        this.description = description;
        this.category = category;
        this.severity = severity;
        this.requesterId = requesterId;
        this.departmentId = departmentId;
    }

    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public TicketCategory getCategory() { return category; }
    public void setCategory(TicketCategory category) { this.category = category; }

    public Severity getSeverity() { return severity; }
    public void setSeverity(Severity severity) { this.severity = severity; }

    public Long getRequesterId() { return requesterId; }
    public void setRequesterId(Long requesterId) { this.requesterId = requesterId; }

    public Long getDepartmentId() { return departmentId; }
    public void setDepartmentId(Long departmentId) { this.departmentId = departmentId; }
}
