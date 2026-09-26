package com.flowdesk.dto;

import jakarta.validation.constraints.NotNull;

public class AssignDto {

    @NotNull(message = "Agent ID is required")
    private Long agentId;

    private Long departmentId;
    private String performedBy;

    public AssignDto() {}

    public AssignDto(Long agentId, Long departmentId, String performedBy) {
        this.agentId = agentId;
        this.departmentId = departmentId;
        this.performedBy = performedBy;
    }

    public Long getAgentId() { return agentId; }
    public void setAgentId(Long agentId) { this.agentId = agentId; }

    public Long getDepartmentId() { return departmentId; }
    public void setDepartmentId(Long departmentId) { this.departmentId = departmentId; }

    public String getPerformedBy() { return performedBy; }
    public void setPerformedBy(String performedBy) { this.performedBy = performedBy; }
}
