# SecureOrder

Platform for financial order validation. OPERATOR creates orders. VALIDATOR approves or rejects.

## Stack (fixed versions, no drift)
- Backend: Java 17, Spring Boot 3.5.x, Maven wrapper, Spring Security, Spring Data JPA, Flyway, Bucket4j, springdoc-openapi.
- DB: PostgreSQL 17 + pgaudit (custom Docker image).
- Frontend: React 18 + TypeScript strict, Vite, Axios, Vitest, React Testing Library, ESLint.
- Infra: Docker multi-stage, docker compose, GitHub Actions.
- Tests: JUnit 5, AssertJ, Mockito, Testcontainers, ArchUnit.

## Hexagonal Architecture (mandatory)
Packages under `com.secureorder`:
- `domain`: entities, value objects, business rules, exceptions. NO Spring/JPA/Jackson dependencies.
- `application`: use cases (ports in), ports out (interfaces), application services. Depends only on `domain`.
- `adapter.in.web`: REST controllers, DTOs, mapping, error handling (RFC 7807).
- `adapter.out.persistence`: JPA entities, Spring Data repositories, mappers to domain.
- `adapter.out.security`: JWT, hashing, refresh token rotation.
- `config`: Spring wiring, HTTP security, rate limiting.
ArchUnit rule: domain depends on nothing; application does not depend on adapters; adapters do not know each other.
JPA entities never leave `adapter.out.persistence`. Domain entities are never exposed in JSON.

## Security (non-negotiable)
- No hardcoded secrets. All from environment variables (`.env.example` committed, `.env` ignored).
- Passwords: Argon2id (or BCrypt cost >= 12). Never logged.
- Access token: JWT, 5 min, minimal claims (sub, roles, jti, exp). Explicit signature (no `alg: none`).
- Refresh token: opaque random value (256 bits), stored HASHED (SHA-256) in DB, with `family_id`. Rotation on each use. Reuse of a used token => revocation of entire family.
- Refresh token in cookie `HttpOnly; Secure; SameSite=Strict; Path=/api/auth`. Access token: JWT, 5 min, minimal claims (sub, roles, jti, exp). Explicit signature (no `alg: none`). Stored in a sameSite strict cookie (accessible via JS) and never in localStorage.
- RBAC via `@PreAuthorize` + HTTP deny-by-default.
- CORS strict (explicit origins), security headers (CSP, HSTS, X-Content-Type-Options, X-Frame-Options/frame-ancestors, Referrer-Policy).
- Rate limiting on login and refresh. Auth error messages generic (no user enumeration).
- Input validation (Bean Validation), parameterized queries only, no stack trace in responses.

## Database
- Three PostgreSQL roles: `secureorder_migrator` (DDL, used by Flyway), `secureorder_app` (DML limited, used by app, never superuser), `auditor` (NOLOGIN, pgaudit audit role).
- Audit: `pgaudit.role = auditor`. GRANT INSERT/UPDATE/DELETE on `transactions` to `auditor` (targeted write audit) and GRANT SELECT on `users` (targeted read audit).
- `log_line_prefix` must contain timestamp, pid, user, database, application_name.

## Quality
- Tests before declaring a task done. No flaky tests, no `Thread.sleep`.
- Covers error cases: 401, 403, expired token, replayed token, validation 400, invalid state transition 409.
- Small, conventional commits (`feat:`, `fix:`, `test:`, `docs:`, `chore:`).
- Never disable a test or security rule to "make it pass".

## Commands
- Backend: `./mvnw verify`
- Frontend: `npm ci && npm run lint && npm test && npm run build`
- All: `docker compose up --build`
- Audit: `./scripts/show-audit.sh`

## How to Work
- Before coding: propose a short plan, list files to create.
- After each task: run tests and fix. Summarize what changed and trade-offs.
- If a requirement is ambiguous, choose the safest option and note it in an ADR.