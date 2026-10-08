package com.secureorder.domain;

import java.time.Instant;
import java.util.Objects;

/**
 * Domain User entity - represents a user in the system.
 * This is a pure domain object with no Spring/JPA annotations.
 */
public class User {
    private Long id;
    private String username;
    private String passwordHash; // Argon2id or BCrypt hashed password
    private String role; // OPERATOR or VALIDATOR
    private boolean enabled;
    private int failedAttempts;
    private Instant lockedUntil;

    // Constructors, getters, setters, equals, hashCode, toString
    public User() {}

    public User(Long id, String username, String passwordHash, String role, 
                boolean enabled, int failedAttempts, Instant lockedUntil) {
        this.id = id;
        this.username = username;
        this.passwordHash = passwordHash;
        this.role = role;
        this.enabled = enabled;
        this.failedAttempts = failedAttempts;
        this.lockedUntil = lockedUntil;
    }

    // Getters and Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    
    public String getUsername() { return username; }
    public void setUsername(String username) { this.username = username; }
    
    public String getPasswordHash() { return passwordHash; }
    public void setPasswordHash(String passwordHash) { this.passwordHash = passwordHash; }
    
    public String getRole() { return role; }
    public void setRole(String role) { this.role = role; }
    
    public boolean isEnabled() { return enabled; }
    public void setEnabled(boolean enabled) { this.enabled = enabled; }
    
    public int getFailedAttempts() { return failedAttempts; }
    public void setFailedAttempts(int failedAttempts) { this.failedAttempts = failedAttempts; }
    
    public Instant getLockedUntil() { return lockedUntil; }
    public void setLockedUntil(Instant lockedUntil) { this.lockedUntil = lockedUntil; }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        User user = (User) o;
        return enabled == user.enabled && 
               failedAttempts == user.failedAttempts && 
               Objects.equals(id, user.id) && 
               Objects.equals(username, user.username) && 
               Objects.equals(passwordHash, user.passwordHash) && 
               Objects.equals(role, user.role) && 
               Objects.equals(lockedUntil, user.lockedUntil);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, username, passwordHash, role, enabled, failedAttempts, lockedUntil);
    }

    @Override
    public String toString() {
        return "User{" +
                "id=" + id +
                ", username='" + username + '\'' +
                ", role='" + role + '\'' +
                ", enabled=" + enabled +
                ", failedAttempts=" + failedAttempts +
                ", lockedUntil=" + lockedUntil +
                '}';
    }
}