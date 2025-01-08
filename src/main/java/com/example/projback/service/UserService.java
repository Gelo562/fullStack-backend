package com.example.projback.service;

import com.example.projback.entity.User;
import com.example.projback.repository.UserRepository;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public UserService(UserRepository userRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    public void registerUser(User user) {
        if (userRepository.findByUsername(user.getUsername()).isPresent()) {
            throw new IllegalArgumentException("Username already exists!");
        }
        User user1 = new User();
        user1.setUsername(user.getUsername());
        user1.setPassword(passwordEncoder.encode(user.getPassword())); // Encrypt the password
        userRepository.save(user1);
    }

    public Optional<User> getUserByUsername(String username) {
        return userRepository.findByUsername(username); // Return Optional<User> from the repository
    }

    public Optional<User> findByUsername(String username) {
        return userRepository.findByUsername(username);
    }

}
