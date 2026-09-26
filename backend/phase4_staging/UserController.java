package com.flowdesk.controller;

import com.flowdesk.dto.DepartmentDto;
import com.flowdesk.dto.UserDto;
import com.flowdesk.repository.DepartmentRepository;
import com.flowdesk.repository.UserRepository;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api")
@CrossOrigin(origins = "*")
public class UserController {

    private final UserRepository userRepository;
    private final DepartmentRepository departmentRepository;

    public UserController(UserRepository userRepository, DepartmentRepository departmentRepository) {
        this.userRepository = userRepository;
        this.departmentRepository = departmentRepository;
    }

    @GetMapping("/users")
    public ResponseEntity<List<UserDto>> getUsers() {
        List<UserDto> users = userRepository.findAll().stream()
                .map(u -> new UserDto(
                        u.getId(),
                        u.getName(),
                        u.getEmail(),
                        u.getRole(),
                        u.getDepartment() != null ? u.getDepartment().getName() : null
                ))
                .collect(Collectors.toList());
        return ResponseEntity.ok(users);
    }

    @GetMapping("/departments")
    public ResponseEntity<List<DepartmentDto>> getDepartments() {
        List<DepartmentDto> departments = departmentRepository.findAll().stream()
                .map(d -> new DepartmentDto(
                        d.getId(),
                        d.getName(),
                        d.getCode(),
                        d.getLeadName()
                ))
                .collect(Collectors.toList());
        return ResponseEntity.ok(departments);
    }
}
