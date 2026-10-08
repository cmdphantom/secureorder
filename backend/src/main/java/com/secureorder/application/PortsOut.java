package com.secureorder.application;

import com.secureorder.domain.Order;
import com.secureorder.domain.User;

import java.util.List;
import java.util.Optional;

/**
 * Ports out (interfaces for adapters) - defines the SPI (Service Provider Interface)
 * that the application layer depends on. Adapters (persistence, security, etc.) will implement these.
 */
public interface PortsOut {
    // User persistence operations
    User saveUser(User user);
    Optional<User> findUserById(Long id);
    Optional<User> findUserByUsername(String username);
    List<User> findAllUsers();
    
    // Order persistence operations
    Order saveOrder(Order order);
    Optional<Order> findOrderById(Long id);
    List<Order> findPendingOrders();
    List<Order> findOrdersByUser(Long userId);
    
    // Security operations (will be implemented by adapter.out.security)
    String hashPassword(String plainTextPassword);
    boolean checkPassword(String plainTextPassword, String hashedPassword);
    String generateAccessToken(User user);
    String generateRefreshToken(User user);
    boolean validateAccessToken(String token);
    boolean validateRefreshToken(String token);
    void revokeRefreshTokenFamily(String refreshTokenId);
}