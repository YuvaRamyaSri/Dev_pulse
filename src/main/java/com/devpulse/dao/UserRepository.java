package com.devpulse.dao;

import org.springframework.data.jpa.repository.JpaRepository;
import com.devpulse.model.User;

public interface UserRepository extends JpaRepository<User, Long> {
    User findByEmailAndPassword(String email, String password);
}
