package com.secureorder.application;

import com.secureorder.domain.Order;
import com.secureorder.domain.User;

import java.time.Instant;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

/**
 * Application service implementing the use cases (PortsIn).
 * Depends on PortsOut (SPI) for persistence and security operations.
 */
public class OrderService implements PortsIn {

    private final PortsOut portsOut;

    public OrderService(PortsOut portsOut) {
        this.portsOut = Objects.requireNonNull(portsOut);
    }

    // User operations
    @Override
    public User createUser(String username, String passwordHash, String role) {
        User user = new User(null, username, passwordHash, role, true, 0, null);
        return portsOut.saveUser(user);
    }

    @Override
    public User getUserById(Long id) {
        return portsOut.findUserById(id)
                .orElseThrow(() -> new IllegalArgumentException("User not found with id: " + id));
    }

    @Override
    public User getUserByUsername(String username) {
        return portsOut.findUserByUsername(username)
                .orElseThrow(() -> new IllegalArgumentException("User not found with username: " + username));
    }

    @Override
    public List<User> getAllUsers() {
        return portsOut.findAllUsers();
    }

    // Order operations
    @Override
    public Order createOrder(String reference, double amount, String currency,
                             String counterparty, Long createdBy) {
        // Validate amount > 0
        if (amount <= 0) {
            throw new IllegalArgumentException("Amount must be greater than zero");
        }
        
        // Validate currency (basic check for ISO 4217 format)
        if (currency == null || currency.length() != 3) {
            throw new IllegalArgumentException("Currency must be a valid ISO 4217 code");
        }
        
        // Validate counterparty not empty
        if (counterparty == null || counterparty.isBlank()) {
            throw new IllegalArgumentException("Counterparty must not be empty");
        }

        Order order = new Order(
                null, reference, amount, currency, counterparty,
                "PENDING", createdBy, Instant.now(),
                null, null, null, 0L
        );
        
        return portsOut.saveOrder(order);
    }

    @Override
    public Order getOrderById(Long id) {
        return portsOut.findOrderById(id)
                .orElseThrow(() -> new IllegalArgumentException("Order not found with id: " + id));
    }

    @Override
    public List<Order> getPendingOrders() {
        return portsOut.findPendingOrders();
    }

    @Override
    public List<Order> getOrdersByUser(Long userId) {
        return portsOut.findOrdersByUser(userId);
    }

    @Override
    public Order approveOrder(Long orderId, Long decidedBy) {
        Order order = getOrderById(orderId);
        
        // Validate that the order is in PENDING status
        if (!"PENDING".equals(order.getStatus())) {
            throw new IllegalStateException("Order is not in PENDING status");
        }
        
        // Validate that the decider is a VALIDATOR
        User decider = getUserById(decidedBy);
        if (!"VALIDATOR".equals(decider.getRole())) {
            throw new IllegalStateException("Only VALIDATOR can approve orders");
        }
        
        // Validate that the validator didn't create the order (separation of duties)
        if (order.getCreatedBy().equals(decidedBy)) {
            throw new IllegalStateException("Validator cannot approve an order they created");
        }
        
        // Update order for approval
        Order updatedOrder = new Order(
                order.getId(),
                order.getReference(),
                order.getAmount(),
                order.getCurrency(),
                order.getCounterparty(),
                "APPROVED",
                order.getCreatedBy(),
                order.getCreatedAt(),
                decidedBy,
                Instant.now(),
                null, // No decision reason for approval
                order.getVersion()
        );
        
        return portsOut.saveOrder(updatedOrder);
    }

    @Override
    public Order rejectOrder(Long orderId, Long decidedBy, String decisionReason) {
        Order order = getOrderById(orderId);
        
        // Validate that the order is in PENDING status
        if (!"PENDING".equals(order.getStatus())) {
            throw new IllegalStateException("Order is not in PENDING status");
        }
        
        // Validate that the decider is a VALIDATOR
        User decider = getUserById(decidedBy);
        if (!"VALIDATOR".equals(decider.getRole())) {
            throw new IllegalStateException("Only VALIDATOR can reject orders");
        }
        
        // Validate that the validator didn't create the order (separation of duties)
        if (order.getCreatedBy().equals(decidedBy)) {
            throw new IllegalStateException("Validator cannot reject an order they created");
        }
        
        // Validate that decision reason is provided
        if (decisionReason == null || decisionReason.isBlank()) {
            throw new IllegalArgumentException("Decision reason is required for rejection");
        }
        
        // Update order for rejection
        Order updatedOrder = new Order(
                order.getId(),
                order.getReference(),
                order.getAmount(),
                order.getCurrency(),
                order.getCounterparty(),
                "REJECTED",
                order.getCreatedBy(),
                order.getCreatedAt(),
                decidedBy,
                Instant.now(),
                decisionReason,
                order.getVersion()
        );
        
        return portsOut.saveOrder(updatedOrder);
    }

    @Override
    public List<Order> getMyOrders(Long userId) {
        return portsOut.findOrdersByUser(userId);
    }
}