# SecureOrder Database Class Diagram

This diagram represents the JPA entities used for persistence in the SecureOrder application.
These entities map to the database tables and are part of the adapter.out.persistence layer.

## Entities

### OrderEntity
Maps to the `transactions` table (as per requirements).

```mermaid
classDiagram
    class OrderEntity {
        -Long id
        -String reference
        -double amount
        -String currency
        -String counterparty
        -String status
        -Long createdBy
        -Instant createdAt
        -Long decidedBy
        -Instant decidedAt
        -String decisionReason
        -Long version
        
        +OrderEntity()
        +OrderEntity(Long id, String reference, double amount, String currency, String counterparty, String status, Long createdBy, Instant createdAt, Long decidedBy, Instant decidedAt, String decisionReason, Long version)
        +Long getId()
        +void setId(Long id)
        +String getReference()
        +void setReference(String reference)
        +double getAmount()
        +void setAmount(double amount)
        +String getCurrency()
        +void setCurrency(String currency)
        +String getCounterparty()
        +void setCounterparty(String counterparty)
        +String getStatus()
        +void setStatus(String status)
        +Long getCreatedBy()
        +void setCreatedBy(Long createdBy)
        +Instant getCreatedAt()
        +void setCreatedAt(Instant createdAt)
        +Long getDecidedBy()
        +void setDecidedBy(Long decidedBy)
        +Instant getDecidedAt()
        +void setDecidedAt(Instant decidedAt)
        +String getDecisionReason()
        +void setDecisionReason(String decisionReason)
        +Long getVersion()
        +void setVersion(Long version)
    }
```

### UserEntity
Maps to the `users` table (implied by the context).

```mermaid
classDiagram
    class UserEntity {
        -Long id
        -String username
        -String passwordHash
        -String role
        -boolean enabled
        -int failedAttempts
        -Instant lockedUntil
        
        +UserEntity()
        +UserEntity(Long id, String username, String passwordHash, String role, boolean enabled, int failedAttempts, Instant lockedUntil)
        +Long getId()
        +void setId(Long id)
        +String getUsername()
        +void setUsername(String username)
        +String getPasswordHash()
        +void setPasswordHash(String passwordHash)
        +String getRole()
        +void setRole(String role)
        +boolean isEnabled()
        +void setEnabled(boolean enabled)
        +int getFailedAttempts()
        +void setFailedAttempts(int failedAttempts)
        +Instant getLockedUntil()
        +void setLockedUntil(Instant lockedUntil)
    }
```

## Relationships
Note: In the current implementation, there are no direct relationships defined between OrderEntity and UserEntity in the JPA layer (the foreign keys are just represented as Long fields). However, in the domain model, an Order is associated with a User (createdBy and decidedBy).

If we were to represent the relationships in the database, we would have:
- OrderEntity.createdBy -> UserEntity.id
- OrderEntity.decidedBy -> UserEntity.id (nullable)

But since we are focusing on the JPA entities as they are, we show them as separate classes without explicit relationships in this diagram.

## Notes
- The actual database table names are defined by the `@Table` annotation (OrderEntity uses "transactions").
- Column names are defined by the `@Column` annotation where specified.
- These entities are used by Spring Data JPA to interact with the PostgreSQL database.
- The domain layer (Order and User) is mapped to/from these entities by the persistence adapter.

---
*Generated as part of SecureOrder project documentation.*