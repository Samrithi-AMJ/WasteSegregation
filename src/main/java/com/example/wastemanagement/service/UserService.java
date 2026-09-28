package com.example.wastemanagement.service;

import com.example.wastemanagement.dto.LoginRequest;
import com.example.wastemanagement.dto.LoginResponse;
import com.example.wastemanagement.entity.User;
import com.example.wastemanagement.exception.BadRequestException;
import com.example.wastemanagement.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class UserService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    /**
     * Simple username/password login used for this college project.
     * Passwords are never stored in plain text - they are hashed with BCrypt.
     */
    public LoginResponse login(LoginRequest request) {
        User user = userRepository.findByUsername(request.getUsername())
                .orElseThrow(() -> new BadRequestException("Invalid username or password"));

        if (!passwordEncoder.matches(request.getPassword(), user.getPassword())) {
            throw new BadRequestException("Invalid username or password");
        }

        return new LoginResponse(user.getUserId(), user.getUsername(), user.getRole(), user.getCitizenId());
    }

    public User createUser(String username, String rawPassword, com.example.wastemanagement.entity.Role role, Long citizenId) {
        if (userRepository.existsByUsername(username)) {
            throw new BadRequestException("Username already exists: " + username);
        }
        User user = new User();
        user.setUsername(username);
        user.setPassword(passwordEncoder.encode(rawPassword));
        user.setRole(role);
        user.setCitizenId(citizenId);
        return userRepository.save(user);
    }
}
