package com.secureorder.domain;

import org.junit.jupiter.api.Test;

import java.time.Instant;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit tests for the domain Order entity.
 * These are pure unit tests with no Spring dependencies.
 */
class OrderTest {

    @Test
    void testOrderCreation() {
        // Given
        Long id = 1L;
        String reference = "ORD-001";
        double amount = 100.50;
        String currency = "USD";
        String counterparty = "Acme Corp";
        String status = "PENDING";
        Long createdBy = 100L;
        Instant createdAt = Instant.now();
        Long decidedBy = null;
        Instant decidedAt = null;
        String decisionReason = null;
        Long version = 0L;

        // When
        Order order = new Order(id, reference, amount, currency, counterparty, status,
                createdBy, createdAt, decidedBy, decidedAt, decisionReason, version);

        // Then
        assertEquals(id, order.getId());
        assertEquals(reference, order.getReference());
        assertEquals(amount, order.getAmount(), 0.001);
        assertEquals(currency, order.getCurrency());
        assertEquals(counterparty, order.getCounterparty());
        assertEquals(status, order.getStatus());
        assertEquals(createdBy, order.getCreatedBy());
        assertEquals(createdAt, order.getCreatedAt());
        assertEquals(decidedBy, order.getDecidedBy());
        assertEquals(decidedAt, order.getDecidedAt());
        assertEquals(decisionReason, order.getDecisionReason());
        assertEquals(version, order.getVersion());
    }

    @Test
    void testOrderStatusTransitions() {
        // Given
        Order order = new Order(
                1L, "ORD-001", 100.0, "USD", "Counterparty",
                "PENDING", 1L, Instant.now(), null, null, null, 0L
        );

        // When/Then - Test valid transitions
        Order approvedOrder = new Order(
                order.getId(), order.getReference(), order.getAmount(),
                order.getCurrency(), order.getCounterparty(),
                "APPROVED", order.getCreatedBy(), order.getCreatedAt(),
                2L, Instant.now(), null, order.getVersion()
        );

        Order rejectedOrder = new Order(
                order.getId(), order.getReference(), order.getAmount(),
                order.getCurrency(), order.getCounterparty(),
                "REJECTED", order.getCreatedBy(), order.getCreatedAt(),
                2L, Instant.now(), "Insufficient funds", order.getVersion()
        );

        // Then
        assertEquals("APPROVED", approvedOrder.getStatus());
        assertEquals("REJECTED", rejectedOrder.getStatus());
        assertNotNull(approvedOrder.getDecidedBy());
        assertNotNull(rejectedOrder.getDecidedBy());
        assertNull(approvedOrder.getDecisionReason());
        assertEquals("Insufficient funds", rejectedOrder.getDecisionReason());
    }

    @Test
    void testOrderValidation() {
        // Test that invalid amounts are caught in application layer (not domain)
        // Domain layer accepts any values - validation happens in application layer
        
        // Given
        Order order = new Order(
                1L, "ORD-001", -50.0, "USD", "Counterparty",
                "PENDING", 1L, Instant.now(), null, null, null, 0L
        );

        // When/Then - Domain object allows negative amount (validation in application layer)
        assertEquals(-50.0, order.getAmount(), 0.001);
    }
}