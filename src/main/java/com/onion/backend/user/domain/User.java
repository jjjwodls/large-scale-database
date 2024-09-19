package com.onion.backend.user.domain;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@ToString
@Entity
@Table(name = "users")  // You can change the table name as needed
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO) // Auto-incremented ID
    private Long id;

    @Column(nullable = false)
    private String username;

    @Column(nullable = false)  // Email must be unique
    private String email;

    @Column(nullable = false)
    private String password;

    private LocalDateTime lastLogin;

    @CreatedDate
    @Column(insertable = true)
    private LocalDateTime createdAt;

    @LastModifiedDate
    private LocalDateTime updatedAt;

    // Other business logic or methods can go here

    // Constructor for creating a new user (email, password)
    public User(String email, String password) {
        this.email = email;
        this.password = password;
    }

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
    }

    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
    }
}