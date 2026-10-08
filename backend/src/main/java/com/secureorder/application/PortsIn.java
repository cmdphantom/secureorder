package com.secureorder.application;

import com.secureorder.domain.Order;
import com.secureorder.domain.User;

import java.util.List;

/**
 * Ports in (use cases) - defines the operations that the application can perform.
 * These are the interfaces that adapters (like web controllers) will implement.
 */
public interface PortsIn {
    // User operations
    User createUser(String username, String passwordHash, String role);
    User getUserById(Long id);
    User getUserByUsername(String username);
    List<User> getAllUsers();
    
    // Authentication operations
    String authenticate(String username, String password);
    String refresh(String refreshToken);
    void logout(String refreshToken);
    
    // Order operations
    Order createOrder(String reference, double amount, String currency, 
                      String counterparty, Long createdBy);
    Order getOrderById(Long id);
    List<Order> getPendingOrders();
    List<Order> getOrdersByUser(Long userId);
    Order approveOrder(Long orderId, Long decidedBy);
    Order rejectOrder(Long orderId, Long decidedBy, String decisionReason);
    List<Order> getMyOrders(Long userId);
}