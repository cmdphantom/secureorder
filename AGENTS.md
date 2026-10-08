# SecureOrder – règles pour agents de code

## Produit
Plateforme de validation d'ordres financiers. OPERATOR crée des ordres. VALIDATOR approuve ou rejette.
Conformité forte : audit au niveau base (pgaudit), moindre privilège, auth stateless.

## Stack (versions fixées, pas de dérive)
- Backend : Java 17, Spring Boot 3.5.x, Maven wrapper, Spring Security, Spring Data JPA, Flyway, Bucket4j, springdoc-openapi.
- DB : PostgreSQL 17 + pgaudit (image Docker custom).
- Front : React 18 + TypeScript strict, Vite, Axios, Vitest, React Testing Library, ESLint.
- Infra : Docker multi-stage, docker compose, GitHub Actions.
- Tests : JUnit 5, AssertJ, Mockito, Testcontainers, ArchUnit.

## Architecture hexagonale (obligatoire)
Packages sous `com.secureorder` :
- `domain` : entités, value objects, règles métier, exceptions. AUCUNE dépendance Spring/JPA/Jackson.
- `application` : use cases (ports in), ports out (interfaces), services applicatifs. Dépend uniquement de `domain`.
- `adapter.in.web` : contrôleurs REST, DTO, mapping, gestion d'erreurs (RFC 7807).
- `adapter.out.persistence` : entités JPA, repositories Spring Data, mappers vers le domaine.
- `adapter.out.security` : JWT, hachage, rotation des refresh tokens.
- `config` : wiring Spring, sécurité HTTP, rate limiting.
Règle ArchUnit : domain ne dépend de rien ; application ne dépend pas des adapters ; les adapters ne se connaissent pas entre eux.
Les entités JPA ne sortent jamais de `adapter.out.persistence`. Les entités du domaine ne sont jamais exposées en JSON.

## Sécurité (non négociable)
- Aucun secret en dur. Tout vient de variables d'environnement (`.env.example` commité, `.env` ignoré).
- Mots de passe : Argon2id (ou BCrypt coût >= 12). Jamais loggés.
- Access token : JWT, 5 min, claims minimaux (sub, roles, jti, exp). Signature explicite (pas d'`alg: none`).
- Refresh token : valeur opaque aléatoire (256 bits), stockée HASHÉE (SHA-256) en base, avec `family_id`. Rotation à chaque usage. Réutilisation d'un token déjà utilisé => révocation de toute la famille.
- Refresh token en cookie `HttpOnly; Secure; SameSite=Strict; Path=/api/auth`. Access token jamais en localStorage (mémoire JS uniquement).
- RBAC via `@PreAuthorize` + règles HTTP deny-by-default.
- CORS strict (origines explicites), en-têtes de sécurité (CSP, HSTS, X-Content-Type-Options, X-Frame-Options/frame-ancestors, Referrer-Policy).
- Rate limiting sur login et refresh. Messages d'erreur d'auth génériques (pas d'énumération d'utilisateurs).
- Validation des entrées (Bean Validation), requêtes paramétrées uniquement, pas de stack trace dans les réponses.

## Base de données
- Trois rôles PostgreSQL : `secureorder_migrator` (DDL, utilisé par Flyway), `secureorder_app` (DML limité, utilisé par l'appli, jamais superuser), `auditor` (NOLOGIN, rôle d'audit pgaudit).
- Audit : `pgaudit.role = auditor`. GRANT INSERT/UPDATE/DELETE sur `transactions` à `auditor` (audit d'écriture ciblé) et GRANT SELECT sur `users` (audit de lecture ciblé).
- `log_line_prefix` doit contenir horodatage, pid, utilisateur, base, application_name.

## Qualité
- Tests avant de déclarer une tâche terminée. Pas de test flaky, pas de `Thread.sleep`.
- Couvre les cas d'erreur : 401, 403, token expiré, token rejoué, validation 400, transition d'état invalide 409.
- Commits petits, conventionnels (`feat:`, `fix:`, `test:`, `docs:`, `chore:`).
- Ne jamais désactiver un test ou une règle de sécurité pour "faire passer".

## Commandes
- Backend : `./mvnw verify`
- Front : `npm ci && npm run lint && npm test && npm run build`
- Tout : `docker compose up --build`
- Audit : `./scripts/show-audit.sh`

## Façon de travailler
- Avant de coder : propose un plan court, liste les fichiers à créer.
- Après chaque tâche : lance les tests et corrige. Résume ce qui a changé et les compromis.
- Si une exigence est ambiguë, choisis l'option la plus sûre et note-la dans un ADR.