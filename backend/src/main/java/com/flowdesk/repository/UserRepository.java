package com.flowdesk.repository;

import com.flowdesk.entity.User;
import com.flowdesk.enums.Role;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface UserRepository
        extends JpaRepository<User, Long> {

    List<User> findByRole(Role role);
}
