package com.flowdesk.controller;

import com.flowdesk.dto.WorkflowRuleDto;
import com.flowdesk.enums.Status;
import com.flowdesk.repository.WorkflowRuleRepository;
import com.flowdesk.workflow.WorkflowEngine;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.*;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/workflows")
@CrossOrigin(origins = "*")
public class WorkflowController {

    private final WorkflowRuleRepository workflowRuleRepository;
    private final WorkflowEngine workflowEngine;

    public WorkflowController(WorkflowRuleRepository workflowRuleRepository, WorkflowEngine workflowEngine) {
        this.workflowRuleRepository = workflowRuleRepository;
        this.workflowEngine = workflowEngine;
    }

    @GetMapping("/rules")
    public ResponseEntity<List<WorkflowRuleDto>> getAllRules() {
        List<WorkflowRuleDto> dtos = workflowRuleRepository.findAll().stream()
                .map(r -> new WorkflowRuleDto(
                        r.getId(),
                        r.getCategory(),
                        r.getSeverity(),
                        r.getDepartment() != null ? r.getDepartment().getName() : "All",
                        r.getSlaHours(),
                        r.getEscalationTarget(),
                        r.getDescription()
                ))
                .collect(Collectors.toList());
        return ResponseEntity.ok(dtos);
    }

    @GetMapping("/matrix")
    public ResponseEntity<Map<String, Set<Status>>> getTransitionMatrix() {
        Map<String, Set<Status>> matrix = new LinkedHashMap<>();
        for (Status status : Status.values()) {
            matrix.put(status.name(), workflowEngine.getAllowedNextStatuses(status));
        }
        return ResponseEntity.ok(matrix);
    }
}
