package com.flowdesk.dto;

import com.flowdesk.enums.Category;
import com.flowdesk.enums.Severity;

public class WorkflowRuleDto {
    private Long id;
    private Category category;
    private Severity severity;
    private String departmentName;
    private Integer slaHours;
    private String escalationTarget;
    private String description;

    public WorkflowRuleDto() {}

    public WorkflowRuleDto(Long id, Category category, Severity severity, String departmentName, Integer slaHours, String escalationTarget, String description) {
        this.id = id;
        this.category = category;
        this.severity = severity;
        this.departmentName = departmentName;
        this.slaHours = slaHours;
        this.escalationTarget = escalationTarget;
        this.description = description;
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public Category getCategory() { return category; }
    public void setCategory(Category category) { this.category = category; }

    public Severity getSeverity() { return severity; }
    public void setSeverity(Severity severity) { this.severity = severity; }

    public String getDepartmentName() { return departmentName; }
    public void setDepartmentName(String departmentName) { this.departmentName = departmentName; }

    public Integer getSlaHours() { return slaHours; }
    public void setSlaHours(Integer slaHours) { this.slaHours = slaHours; }

    public String getEscalationTarget() { return escalationTarget; }
    public void setEscalationTarget(String escalationTarget) { this.escalationTarget = escalationTarget; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }
}
