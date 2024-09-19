package com.onion.backend.user.service;

import com.onion.backend.exception.ResourceNotFoundException;
import com.onion.backend.user.domain.User;
import com.onion.backend.user.infrastructure.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
public class UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    @Autowired
    public UserService(UserRepository userRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    /**
     * Create a new user with the provided email and password.
     *
     * @param email    the user's email
     * @param password the user's password
     * @return the created User entity
     */
    public User createUser(String username, String email, String password) {
        if (userRepository.findByEmail(email).isPresent()) {
            throw new IllegalArgumentException("User with this email already exists.");
        }

        User newUser = new User(email, passwordEncoder.encode(password));
        newUser.setUsername(username);
        newUser.setLastLogin(LocalDateTime.now()); // Optionally, set last login on creation

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

    public List<User> getUsers(){
        return userRepository.findAll();
    }

    public User findByUsername(String username){
        return userRepository.findByUsername(username).orElseThrow(() -> new ResourceNotFoundException("user not found"));
    }

    public User userBySecurityContext(){
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        UserDetails userDetails = (UserDetails) authentication.getPrincipal();

        String username = userDetails.getUsername();
        return findByUsername(username);
    }
}