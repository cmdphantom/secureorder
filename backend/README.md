# SecureOrder Backend

This is the backend part of the SecureOrder application, built with Java 17 and Spring Boot 3.5.x.

## Features

- RESTful API for order management and authentication
- Hexagonal architecture (ports and adapters pattern)
- JWT-based authentication with refresh token rotation
- Role-based access control (RBAC)
- Database audit logging with pgAudit
- Rate limiting for security
- OpenAPI/Swagger documentation
- Containerized with Docker

## Setup

1. Ensure you have Java 17 and Maven installed
2. Configure your PostgreSQL database (see docker-compose.yml for reference)
3. Set up environment variables (see .env.example)
4. Build the application:
   ```bash
   ./mvnw clean install
   ```
5. Run the application:
   ```bash
   ./mvnw spring-boot:run
   ```

## Available Commands

- `./mvnw verify`: Run tests and check code quality
- `./mvnw spring-boot:run`: Run the application
- `docker compose up --build`: Run the full stack with Docker

## Architecture

The backend follows a strict hexagonal architecture:

- `domain`: Business logic, entities, value objects (no framework dependencies)
- `application`: Use cases, ports (interfaces), application services
- `adapter.in.web`: REST controllers, DTOs, HTTP adapter
- `adapter.out.persistence`: JPA entities, Spring Data repositories, database adapter
- `adapter.out.security`: JWT handling, password hashing, security adapter
- `config`: Spring configuration, security HTTP configuration

## Security Features

- JWT access tokens (5-minute expiration)
- Refresh token rotation with family-based revocation
- Argon2id/BCrypt password hashing
- HttpOnly, Secure, SameSite cookies for token storage
- Rate limiting on authentication endpoints
- CORS restrictions
- Security headers (CSP, HSTS, etc.)
- Input validation and sanitization
- No sensitive data in logs
- Principle of least privilege

## Database

- PostgreSQL 17 with pgAudit extension
- Three database roles:
  - `secureorder_migrator`: For Flyway migrations (DDL)
  - `secureorder_app`: For application data access (DML only)
  - `auditor`: For pgAudit (NOLOGIN, read-only access to specific tables)

## Testing

- Unit tests with JUnit 5, AssertJ, Mockito
- Integration tests with Testcontainers
- Architecture tests with ArchUnit
- API contract tests

## Deployment

The application is designed to be deployed via Docker Compose or Kubernetes.

See the root [README.md](../README.md) for more information about the full stack.