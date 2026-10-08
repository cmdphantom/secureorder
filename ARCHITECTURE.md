# SecureOrder Architecture Technical Choices

## Hexagonal Architecture (Ports and Adapters)

The application follows the hexagonal architecture pattern to ensure separation of concerns and testability.

### Layers

1. **Domain Layer** (`com.secureorder.domain`)
   - Contains business entities, value objects, and business rules.
   - No dependencies on any external frameworks or libraries (no Spring, JPA, Jackson, etc.).
   - This is the core of the application.

2. **Application Layer** (`com.secureorder.application`)
   - Contains use cases (ports in) and ports out (interfaces).
   - Application services that orchestrate the flow of data to and from the domain.
   - Depends only on the domain layer.

3. **Adapter Layer**
   - **Inbound Adapters** (`com.secureorder.adapter.in.web`)
     - REST controllers that handle HTTP requests and translate them to use case invocations.
     - Contains DTOs and mappers to/from domain objects.
   - **Outbound Adapters**
     - **Persistence** (`com.secureorder.adapter.out.persistence`)
       - JPA entities, Spring Data repositories, and mappers to/from domain.
       - This layer is responsible for data storage and retrieval.
     - **Security** (`com.secureorder.adapter.out.security`)
       - Handles JWT token generation/validation, password hashing, refresh token management.
       - Implements the security ports out.

4. **Configuration Layer** (`com.secureorder.config`)
   - Spring configuration classes (security, web, etc.).
   - Wires the application together.

### Dependencies Rule
- Domain layer: no dependencies on other layers.
- Application layer: depends only on domain layer.
- Adapter layer: depends on application and domain layers.
- Configuration layer: depends on all layers to wire them together.

This structure ensures that the core business logic is isolated from external concerns, making it easier to test and evolve.

## Technology Choices

### Backend
- **Language**: Java 17 (LTS)
- **Framework**: Spring Boot 3.5.x
  - Provides dependency injection, embedded Tomcat, and auto-configuration.
  - Chosen for its maturity, ecosystem, and suitability for microservices.
- **Build Tool**: Maven with Maven Wrapper (mvnw)
  - Ensures consistent builds across environments.
- **Persistence**: Spring Data JPA with Hibernate
  - ORM for database access.
  - Chosen for its integration with Spring and ease of use.
- **Database Migration**: Flyway
  - Version-controlled database migrations.
  - Chosen for its simplicity and reliability.
- **Security**: Spring Security
  - For authentication and authorization.
  - Custom implementation for JWT and refresh token handling.
- **API Documentation**: SpringDoc OpenAPI (Swagger UI)
  - Automatic generation of API documentation.
- **Validation**: Bean Validation (Hibernate Validator)
  - For input validation via annotations.
- **Logging**: SLF4J with Logback (default in Spring Boot)
  - Standard logging facade.
- **Testing**:
  - JUnit 5: Testing framework.
  - AssertJ: Fluent assertions.
  - Mockito: Mocking framework.
  - Testcontainers: For integration tests with real dependencies (e.g., PostgreSQL).
  - ArchUnit: To enforce architectural rules.

### Database
- **Engine**: PostgreSQL 17
  - Chosen for its reliability, features, and strong support for JSON and SQL.
- **Extension**: pgAudit
  - For detailed audit logging of database operations.
  - Configured to target specific tables and roles.

### Frontend
- **Framework**: React 18
  - Chosen for its component-based architecture and large ecosystem.
- **Language**: TypeScript (strict mode)
  - For static typing and improved developer experience.
- **Build Tool**: Vite
  - For fast development builds and optimized production bundles.
- **HTTP Client**: Axios
  - For making HTTP requests to the backend.
- **Testing**:
  - Vitest: Unit testing framework (compatible with Vite).
  - React Testing Library: For testing React components.
  - ESLint: For code quality and style enforcement.

### Infrastructure
- **Containerization**: Docker
  - Multi-stage builds for small, secure images.
- **Orchestration**: Docker Compose (for local development)
  - Defines and runs multi-container applications.
- **CI/CD**: GitHub Actions
  - For automated testing, building, and deployment.

### Quality Gates
- **Code Coverage**: Aim for high coverage with unit and integration tests.
- **Security**: Regular dependency scans (OWASP Dependency Check) and code reviews.
- **Performance**: Load testing considerations for critical paths.

## Decision Log

| Date       | Decision                                     | Rationale                                                                 | Status  |
|------------|----------------------------------------------|---------------------------------------------------------------------------|---------|
| 2026-10-08 | Use hexagonal architecture                   | To ensure clean separation of concerns and testability.                   | Accepted|
| 2026-10-08 | Java 17 and Spring Boot 3.5.x                | LTS version with latest features and long-term support.                   | Accepted|
| 2026-10-08 | PostgreSQL with pgAudit                      | Reliable database with built-in auditing capabilities.                    | Accepted|
| 2026-10-08 | React 18 with TypeScript and Vite            | Modern frontend stack for performance and developer experience.           | Accepted|
| 2026-10-08 | Docker and Docker Compose                    | For consistent local development and production-like environments.        | Accepted|
| 2026-10-08 | GitHub Actions for CI/CD                     | Integrated with GitHub repository for automation.                         | Accepted|

## Open Questions

- [ ] Should we use Redis for rate limiting in production? (Currently using in-memory Bucket4j for dev)
- [ ] What are the exact CORS origins for production?
- [ ] How will we manage secrets in production (Vault, AWS Secrets Manager, etc.)?
