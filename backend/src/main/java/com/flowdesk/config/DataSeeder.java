package com.flowdesk.config;

import com.flowdesk.entity.Department;
import com.flowdesk.entity.User;
import com.flowdesk.entity.WorkflowRule;
import com.flowdesk.enums.Role;
import com.flowdesk.enums.Severity;
import com.flowdesk.enums.TicketCategory;
import com.flowdesk.enums.TicketStatus;
import com.flowdesk.repository.DepartmentRepository;
import com.flowdesk.repository.UserRepository;
import com.flowdesk.repository.WorkflowRuleRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

@Component
public class DataSeeder implements CommandLineRunner {

    private final DepartmentRepository departmentRepository;
    private final UserRepository userRepository;
    private final WorkflowRuleRepository workflowRuleRepository;

    public DataSeeder(
            DepartmentRepository departmentRepository,
            UserRepository userRepository,
            WorkflowRuleRepository workflowRuleRepository
    ) {
        this.departmentRepository = departmentRepository;
        this.userRepository = userRepository;
        this.workflowRuleRepository = workflowRuleRepository;
    }

    @Override
    public void run(String... args) {

        if (departmentRepository.count() == 0) {
            seedDepartments();
        }

        if (userRepository.count() == 0) {
            seedUsers();
        }

        if (workflowRuleRepository.count() == 0) {
            seedWorkflowRules();
        }
    }

    private void seedDepartments() {

        departmentRepository.save(new Department("IT Support"));
        departmentRepository.save(new Department("Human Resources"));
        departmentRepository.save(new Department("Finance"));
        departmentRepository.save(new Department("Facilities"));
    }

    private void seedUsers() {

        Department it = departmentRepository
                .findByName("IT Support")
                .orElseThrow();

        Department hr = departmentRepository
                .findByName("Human Resources")
                .orElseThrow();

        userRepository.save(new User("Admin User", "admin@flowdesk.local", Role.ADMIN, it));
        userRepository.save(new User("Arun Kumar", "arun@flowdesk.local", Role.AGENT, it));
        userRepository.save(new User("Priya Sharma", "priya@flowdesk.local", Role.AGENT, hr));
        userRepository.save(new User("Rahul Kumar", "rahul@flowdesk.local", Role.REQUESTER, it));
    }

    private void seedWorkflowRules() {

        TicketCategory[] categories = TicketCategory.values();
        Severity[] severities = Severity.values();

        for (TicketCategory category : categories) {
            for (Severity severity : severities) {

                addRule(category, severity,
                        TicketStatus.OPEN,
                        TicketStatus.ASSIGNED);

                addRule(category, severity,
                        TicketStatus.ASSIGNED,
                        TicketStatus.IN_PROGRESS);

                addRule(category, severity,
                        TicketStatus.IN_PROGRESS,
                        TicketStatus.RESOLVED);

                addRule(category, severity,
                        TicketStatus.RESOLVED,
                        TicketStatus.CLOSED);

                // Allow reopening a resolved ticket
                addRule(category, severity,
                        TicketStatus.RESOLVED,
                        TicketStatus.IN_PROGRESS);
            }
        }
    }

    private void addRule(
            TicketCategory category,
            Severity severity,
            TicketStatus from,
            TicketStatus to
    ) {

        workflowRuleRepository.save(
                new WorkflowRule(
                        category,
                        severity,
                        from,
                        to,
                        true
                )
        );
    }
}
