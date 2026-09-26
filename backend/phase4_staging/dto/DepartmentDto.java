package com.flowdesk.dto;

public class DepartmentDto {
    private Long id;
    private String name;
    private String code;
    private String leadName;

    public DepartmentDto() {}

    public DepartmentDto(Long id, String name, String code, String leadName) {
        this.id = id;
        this.name = name;
        this.code = code;
        this.leadName = leadName;
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getCode() { return code; }
    public void setCode(String code) { this.code = code; }

    public String getLeadName() { return leadName; }
    public void setLeadName(String leadName) { this.leadName = leadName; }
}
