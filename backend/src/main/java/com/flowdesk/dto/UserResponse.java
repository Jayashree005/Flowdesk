package com.flowdesk.dto;

import com.flowdesk.enums.Role;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class UserResponse {
    private Long id;
    private String name;
    private String email;
    private Role role;
    private String departmentName;

    public UserResponse() {}

    public UserResponse(Long id, String name, String email, Role role, String departmentName) {
        this.id = id;
        this.name = name;
        this.email = email;
        this.role = role;
        this.departmentName = departmentName;
    }

    public static UserResponseBuilder builder() {
        return new UserResponseBuilder();
    }

    public static class UserResponseBuilder {
        private Long id;
        private String name;
        private String email;
        private Role role;
        private String departmentName;

        public UserResponseBuilder id(Long id) { this.id = id; return this; }
        public UserResponseBuilder name(String name) { this.name = name; return this; }
        public UserResponseBuilder email(String email) { this.email = email; return this; }
        public UserResponseBuilder role(Role role) { this.role = role; return this; }
        public UserResponseBuilder departmentName(String departmentName) { this.departmentName = departmentName; return this; }

        public UserResponse build() {
            return new UserResponse(id, name, email, role, departmentName);
        }
    }

    public Long getId() { return id; }
    public String getName() { return name; }
    public String getEmail() { return email; }
    public Role getRole() { return role; }
    public String getDepartmentName() { return departmentName; }
}
