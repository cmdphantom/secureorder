package com.secureorder.domain;

import java.time.Instant;
import java.util.Objects;

/**
 * Domain Order entity - represents an order in the system.
 * This is a pure domain object with no Spring/JPA annotations.
 */
public class Order {
    private Long id;
    private String reference;
    private double amount;
    private String currency; // ISO 4217
    private String counterparty;
    private String status; // PENDING, APPROVED, REJECTED
    private Long createdBy; // User ID
    private Instant createdAt;
    private Long decidedBy; // User ID (nullable)
    private Instant decidedAt; // nullable
    private String decisionReason; // nullable (required for REJECTED)
    private Long version; // For optimistic locking

    // Constructors, getters, setters, equals, hashCode, toString
    public Order() {}

    public Order(Long id, String reference, double amount, String currency, 
                 String counterparty, String status, Long createdBy, Instant createdAt,
                 Long decidedBy, Instant decidedAt, String decisionReason, Long version) {
        this.id = id;
        this.reference = reference;
        this.amount = amount;
        this.currency = currency;
        this.counterparty = counterparty;
        this.status = status;
        this.createdBy = createdBy;
        this.createdAt = createdAt;
        this.decidedBy = decidedBy;
        this.decidedAt = decidedAt;
        this.decisionReason = decisionReason;
        this.version = version;
    }

    // Getters and Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    
    public String getReference() { return reference; }
    public void setReference(String reference) { this.reference = reference; }
    
    public double getAmount() { return amount; }
    public void setAmount(double amount) { this.amount = amount; }
    
    public String getCurrency() { return currency; }
    public void setCurrency(String currency) { this.currency = currency; }
    
    public String getCounterparty() { return counterparty; }
    public void setCounterparty(String counterparty) { this.counterparty = counterparty; }
    
    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
    
    public Long getCreatedBy() { return createdBy; }
    public void setCreatedBy(Long createdBy) { this.createdBy = createdBy; }
    
    public Instant getCreatedAt() { return createdAt; }
    public void setCreatedAt(Instant createdAt) { this.createdAt = createdAt; }
    
    public Long getDecidedBy() { return decidedBy; }
    public void setDecidedBy(Long decidedBy) { this.decidedBy = decidedBy; }
    
    public Instant getDecidedAt() { return decidedAt; }
    public void setDecidedAt(Instant decidedAt) { this.decidedAt = decidedAt; }
    
    public String getDecisionReason() { return decisionReason; }
    public void setDecisionReason(String decisionReason) { this.decisionReason = decisionReason; }
    
    public Long getVersion() { return version; }
    public void setVersion(Long version) { this.version = version; }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Order order = (Order) o;
        return Double.compare(order.amount, amount) == 0 && 
               Objects.equals(id, order.id) && 
               Objects.equals(reference, order.reference) && 
               Objects.equals(currency, order.currency) && 
               Objects.equals(counterparty, order.counterparty) && 
               Objects.equals(status, order.status) && 
               Objects.equals(createdBy, order.createdBy) && 
               Objects.equals(createdAt, order.createdAt) && 
               Objects.equals(decidedBy, order.decidedBy) && 
               Objects.equals(decidedAt, order.decidedAt) && 
               Objects.equals(decisionReason, order.decisionReason) && 
               Objects.equals(version, order.version);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, reference, amount, currency, counterparty, status, 
                           createdBy, createdAt, decidedBy, decidedAt, decisionReason, version);
    }

    @Override
    public String toString() {
        return "Order{" +
                "id=" + id +
                ", reference='" + reference + '\'' +
                ", amount=" + amount +
                ", currency='" + currency + '\'' +
                ", counterparty='" + counterparty + '\'' +
                ", status='" + status + '\'' +
                ", createdBy=" + createdBy +
                ", createdAt=" + createdAt +
                ", decidedBy=" + decidedBy +
                ", decidedAt=" + decidedAt +
                ", decisionReason='" + decisionReason + '\'' +
                ", version=" + version +
                '}';
    }
}