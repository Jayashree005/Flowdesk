package com.flowdesk.service;

import com.flowdesk.entity.Department;
import com.flowdesk.entity.User;
import com.flowdesk.enums.Role;
import com.flowdesk.repository.DepartmentRepository;
import com.flowdesk.repository.UserRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Service;

@Service
public class DataSeederService implements CommandLineRunner {

    private final DepartmentRepository departmentRepository;
    private final UserRepository userRepository;

    public DataSeederService(DepartmentRepository departmentRepository, UserRepository userRepository) {
        this.departmentRepository = departmentRepository;
        this.userRepository = userRepository;
    }

    @Override
    public void run(String... args) {
        if (departmentRepository.count() == 0) {
            Department itDept = departmentRepository.save(new Department("IT Support"));
            Department hrDept = departmentRepository.save(new Department("Human Resources"));
            Department finDept = departmentRepository.save(new Department("Finance"));
            Department facDept = departmentRepository.save(new Department("Facilities"));

            userRepository.save(new User("Admin System", "admin@flowdesk.io", Role.ADMIN, itDept));
            userRepository.save(new User("Arun Sharma", "arun@flowdesk.io", Role.AGENT, itDept));
            userRepository.save(new User("Rahul Verma", "rahul@company.io", Role.REQUESTER, null));
        }
    }
}
