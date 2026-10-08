package com.secureorder.adapter.out;

import com.secureorder.adapter.out.persistence.PersistenceAdapter;
import com.secureorder.adapter.out.security.SecurityAdapter;
import com.secureorder.application.PortsOut;
import com.secureorder.domain.Order;
import com.secureorder.domain.User;

import java.util.List;
import java.util.Optional;

/**
 * Composite adapter that combines PersistenceAdapter and SecurityAdapter
 * to implement the full PortsOut interface.
 * This belongs to the adapter.out layer.
 */
public class CompositePortsOut implements PortsOut {

    private final PersistenceAdapter persistenceAdapter;
    private final SecurityAdapter securityAdapter;

    public CompositePortsOut(PersistenceAdapter persistenceAdapter, SecurityAdapter securityAdapter) {
        this.persistenceAdapter = persistenceAdapter;
        this.securityAdapter = securityAdapter;
    }

    // User persistence operations - delegate to persistence adapter
    @Override
    public User saveUser(User user) {
        return persistenceAdapter.saveUser(user);
    }

    @Override
    public Optional<User> findUserById(Long id) {
        return persistenceAdapter.findUserById(id);
    }

    @Override
    public Optional<User> findUserByUsername(String username) {
        return persistenceAdapter.findUserByUsername(username);
    }

    @Override
    public List<User> findAllUsers() {
        return persistenceAdapter.findAllUsers();
    }

    // Order persistence operations - delegate to persistence adapter
    @Override
    public Order saveOrder(Order order) {
        return persistenceAdapter.saveOrder(order);
    }

    @Override
    public Optional<Order> findOrderById(Long id) {
        return persistenceAdapter.findOrderById(id);
    }

    @Override
    public List<Order> findPendingOrders() {
        return persistenceAdapter.findPendingOrders();
    }

    @Override
    public List<Order> findOrdersByUser(Long userId) {
        return persistenceAdapter.findOrdersByUser(userId);
    }

    // Security operations - delegate to security adapter
    @Override
    public String hashPassword(String plainTextPassword) {
        return securityAdapter.hashPassword(plainTextPassword);
    }

    @Override
    public boolean checkPassword(String plainTextPassword, String hashedPassword) {
        return securityAdapter.checkPassword(plainTextPassword, hashedPassword);
    }

    @Override
    public String generateAccessToken(User user) {
        return securityAdapter.generateAccessToken(user);
    }

    @Override
    public String generateRefreshToken(User user) {
        return securityAdapter.generateRefreshToken(user);
    }

    @Override
    public boolean validateAccessToken(String token) {
        return securityAdapter.validateAccessToken(token);
    }

    @Override
    public boolean validateRefreshToken(String token) {
        return securityAdapter.validateRefreshToken(token);
    }

    @Override
    public void revokeRefreshTokenFamily(String refreshTokenId) {
        securityAdapter.revokeRefreshTokenFamily(refreshTokenId);
    }
}