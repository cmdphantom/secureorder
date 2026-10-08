package com.secureorder.adapter.out.persistence;

import com.secureorder.adapter.out.security.SecurityAdapter;
import com.secureorder.application.PortsOut;
import com.secureorder.domain.Order;
import com.secureorder.domain.User;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * Persistence adapter implementing the PortsOut interface.
 * This belongs to the adapter.out.persistence layer and uses Spring Data JPA repositories.
 * Delegates security operations to SecurityAdapter.
 */
public class PersistenceAdapter implements PortsOut {

    private final UserRepository userRepository;
    private final OrderRepository orderRepository;
    private final SecurityAdapter securityAdapter;

    public PersistenceAdapter(UserRepository userRepository, OrderRepository orderRepository, SecurityAdapter securityAdapter) {
        this.userRepository = userRepository;
        this.orderRepository = orderRepository;
        this.securityAdapter = securityAdapter;
    }

    // User persistence operations
    @Override
    public User saveUser(User user) {
        UserEntity entity = EntityMapper.toEntity(user);
        UserEntity savedEntity = userRepository.save(entity);
        return EntityMapper.toDomain(savedEntity);
    }

    @Override
    public Optional<User> findUserById(Long id) {
        return userRepository.findById(id)
                .map(EntityMapper::toDomain);
    }

    @Override
    public Optional<User> findUserByUsername(String username) {
        return userRepository.findByUsername(username)
                .map(EntityMapper::toDomain);
    }

    @Override
    public java.util.List<User> findAllUsers() {
        return userRepository.findAll().stream()
                .map(EntityMapper::toDomain)
                .collect(Collectors.toList());
    }

    // Order persistence operations
    @Override
    public Order saveOrder(Order order) {
        OrderEntity entity = EntityMapper.toEntity(order);
        OrderEntity savedEntity = orderRepository.save(entity);
        return EntityMapper.toDomain(savedEntity);
    }

    @Override
    public Optional<Order> findOrderById(Long id) {
        return orderRepository.findById(id)
                .map(EntityMapper::toDomain);
    }

    @Override
    public List<Order> findPendingOrders() {
        return orderRepository.findByStatusPending().stream()
                .map(EntityMapper::toDomain)
                .collect(Collectors.toList());
    }

    @Override
    public List<Order> findOrdersByUser(Long userId) {
        return orderRepository.findByCreatedBy(userId).stream()
                .map(EntityMapper::toDomain)
                .collect(Collectors.toList());
    }

    // Security operations delegated to SecurityAdapter
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