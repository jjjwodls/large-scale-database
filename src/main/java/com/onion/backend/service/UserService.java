package com.onion.backend.service;

import com.onion.backend.entity.User;
import com.onion.backend.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Optional;

@Service
public class UserService {

    private final UserRepository userRepository;

    @Autowired
    public UserService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    /**
     * Create a new user with the provided email and password.
     *
     * @param email    the user's email
     * @param password the user's password
     * @return the created User entity
     */
    public User createUser(String username, String email, String password) {
        // Check if a user with the email already exists
        if (userRepository.findByEmail(email).isPresent()) {
            throw new IllegalArgumentException("User with this email already exists.");
        }

        // Create a new user and set initial values
        User newUser = new User(email, password);
        newUser.setUsername(username);
        newUser.setLastLogin(LocalDateTime.now()); // Optionally, set last login on creation

        // Save the new user to the database
        return userRepository.save(newUser);
    }


    // Method to delete a user by ID (Hard Delete)
    public void deleteUser(Long userId) {
        Optional<User> user = userRepository.findById(userId);

        if (user.isPresent()) {
            userRepository.deleteById(userId);
        } else {
            throw new IllegalArgumentException("User not found.");
        }
    }
}