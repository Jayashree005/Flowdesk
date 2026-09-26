package com.flowdesk.controller;

import com.flowdesk.dto.UserResponse;
import com.flowdesk.entity.User;
import com.flowdesk.enums.Role;
import com.flowdesk.repository.UserRepository;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/users")
@CrossOrigin(origins = "*")
public class UserController {

    private final UserRepository userRepository;

    public UserController(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @GetMapping
    public ResponseEntity<List<UserResponse>> getUsers(@RequestParam(required = false) Role role) {
        List<User> users = (role != null)
                ? userRepository.findByRole(role)
                : userRepository.findAll();

        List<UserResponse> responses = users.stream()
                .map(u -> UserResponse.builder()
                        .id(u.getId())
                        .name(u.getName())
                        .email(u.getEmail())
                        .role(u.getRole())
                        .departmentName(u.getDepartment() != null ? u.getDepartment().getName() : null)
                        .build())
                .toList();

        return ResponseEntity.ok(responses);
    }
}
