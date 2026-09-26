package com.flowdesk.dto;

import com.flowdesk.enums.Role;

public class UserDto {
    private Long id;
    private String name;
    private String email;
    private Role role;
    private String departmentName;

    public UserDto() {}

    public UserDto(Long id, String name, String email, Role role, String departmentName) {
        this.id = id;
        this.name = name;
        this.email = email;
        this.role = role;
        this.departmentName = departmentName;
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public Role getRole() { return role; }
    public void setRole(Role role) { this.role = role; }

    public String getDepartmentName() { return departmentName; }
    public void setDepartmentName(String departmentName) { this.departmentName = departmentName; }
}
