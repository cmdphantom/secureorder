# SecureOrder Tools and Frameworks

This document summarizes the tools and frameworks used in the SecureOrder project.

## Backend

### Language
- **Java 17**: LTS version, chosen for stability and long-term support.

### Framework
- **Spring Boot 3.5.x**: Provides dependency injection, embedded web server, and auto-configuration. Chosen for its maturity and ecosystem.

### Build Tool
- **Maven with Maven Wrapper (mvnw)**: Ensures consistent and reproducible builds.

### Persistence
- **Spring Data JPA with Hibernate**: ORM for database access. Simplifies data access layers.

### Database Migration
- **Flyway**: Version-controlled database migrations. Chosen for simplicity and reliability.

### Security
- **Spring Security**: For authentication and authorization. Custom implementation for JWT and refresh token handling.

### API Documentation
- **SpringDoc OpenAPI (Swagger UI)**: Automatic generation of API documentation from Spring Boot applications.

### Validation
- **Bean Validation (Hibernate Validator)**: For input validation via annotations.

### Logging
- **SLF4J with Logback**: Default logging facade in Spring Boot.

### Testing
- **JUnit 5**: Testing framework.
- **AssertJ**: Fluent assertions for writing tests.
- **Mockito**: Mocking framework for unit tests.
- **Testcontainers**: For integration tests with real dependencies (e.g., PostgreSQL).
- **ArchUnit**: To enforce architectural rules (e.g., hexagonal architecture constraints).

## Database

### Engine
- **PostgreSQL 17**: Chosen for reliability, features, and strong support for JSON and SQL.

### Extension
- **pgAudit**: For detailed audit logging of database operations. Configured to target specific tables and roles.

## Frontend

### Framework
- **React 18**: Chosen for its component-based architecture and large ecosystem.

### Language
- **TypeScript (strict mode)**: For static typing and improved developer experience.

### Build Tool
- **Vite**: For fast development builds and optimized production bundles.

### HTTP Client
- **Axios**: For making HTTP requests to the backend.

### Testing
- **Vitest**: Unit testing framework (compatible with Vite).
- **React Testing Library**: For testing React components.
- **ESLint**: For code quality and style enforcement.

## Infrastructure

### Containerization
- **Docker**: Multi-stage builds for small, secure images.

### Orchestration
- **Docker Compose**: Defines and runs multi-container applications for local development.

### CI/CD
- **GitHub Actions**: For automated testing, building, and deployment.

## Quality and Process

### Code Quality
- **SonarQube** (optional): For continuous inspection of code quality.
- **Checkstyle** / **SpotBugs** (optional): For static analysis.

### Documentation
- **Swagger UI**: For API documentation.
- **Markdown**: For project documentation (this file, README, etc.).

### Collaboration
- **Git**: For version control.
- **GitHub**: For hosting the repository and project management (issues, pull requests).

## Development Tools

### IDE
- **IntelliJ IDEA** or **VS Code**: Recommended IDEs for development.

### API Testing
- **Postman** or **curl**: For manual API testing during development.

### Database Management
- **pgAdmin** or **DBeaver**: For database administration and querying.

## Version and Compatibility Notes

- All versions are fixed as per the project requirements to avoid drift.
- The backend uses Java 17 and Spring Boot 3.5.x.
- The frontend uses React 18 and TypeScript.
- Docker images are multi-stage to minimize size and improve security.

---
*This document is part of the SecureOrder project documentation and should be kept up-to-date with any changes in tools or frameworks.*