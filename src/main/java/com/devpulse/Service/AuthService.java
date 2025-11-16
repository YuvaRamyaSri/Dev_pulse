package com.devpulse.Service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.devpulse.model.User;
import com.devpulse.dao.UserRepository;

@Service
public class AuthService {

    @Autowired
    private UserRepository userRepository;

    public User validateUser(String email, String password) {
        User user = userRepository.findByEmailAndPassword(email, password);
        return user;
    }
}
