# SecureOrder – Agent Pack (Claude Code + GitHub Copilot)

Source unique de vérité : `AGENTS.md`. Claude Code et Copilot le lisent tous les deux.
Livraison AMEXIO : vendredi 16 octobre 2026, midi. Budget conseillé : 4 à 6 h.

---

## 0. Mode d'emploi

1. Crée le repo `secureorder`, puis les fichiers des sections 1 à 4 aux chemins indiqués.
2. **Claude Code** : lance `claude` à la racine, passe en mode plan (Shift+Tab), colle la phase 0, valide le plan, puis exécute les phases une par une. Commit après chaque phase.
3. **Copilot (VS Code)** : ouvre le chat en mode Agent. Active `chat.useAgentsMdFile` si besoin. Colle les mêmes prompts (section 5), ou sauve-les dans `.github/prompts/phaseN.prompt.md` et lance-les avec `/phaseN`.
4. Après chaque phase : `./mvnw verify`, `npm test`, `docker compose up --build`. Ne passe pas à la phase suivante si ça casse.
5. Relis chaque diff. Tu devras expliquer chaque choix en entretien. Écris toi-même les ADR (l'agent fournit un brouillon, tu le corriges avec tes mots).

Vérifie que ta version de Claude Code gère bien l'import `@AGENTS.md` dans CLAUDE.md et que ta version de Copilot lit AGENTS.md. Sinon, copie le contenu dans `.github/copilot-instructions.md`.

---

## 1. `AGENTS.md` (à la racine)

```markdown
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
```

---

## 2. Fichiers de pointage

`CLAUDE.md`
```markdown
@AGENTS.md

## Spécifique Claude Code
- Utilise le mode plan pour toute tâche touchant sécurité ou base de données.
- Avant de finir une phase, invoque le skill `secure-review` sur le diff.
- Spec de référence : docs/SPEC.md.
```

`.github/copilot-instructions.md`
```markdown
Suis les règles de AGENTS.md à la racine du dépôt (architecture hexagonale, sécurité, base de données, qualité).
Spec de référence : docs/SPEC.md. Réponds en français pour la documentation, en anglais pour le code.
```

`.github/instructions/java.instructions.md`
```markdown
---
applyTo: "backend/**/*.java"
---
Java 17. Hexagonal strict : le package domain n'importe ni Spring, ni JPA, ni Jackson. Pas de logique métier dans les contrôleurs. Constructor injection uniquement. Records pour les DTO.
```

`.github/instructions/react.instructions.md`
```markdown
---
applyTo: "frontend/**/*.{ts,tsx}"
---
TypeScript strict, pas de `any`. Access token en mémoire uniquement. Un seul refresh en vol à la fois (single-flight). Composants testés avec React Testing Library.
```

---

## 3. `docs/SPEC.md` (développement piloté par la spec)

```markdown
# SPEC – SecureOrder

## Acteurs et rôles
- OPERATOR : crée des ordres, consulte les siens.
- VALIDATOR : liste les ordres PENDING, approuve ou rejette (avec motif).
Un utilisateur a exactement un rôle. Un VALIDATOR ne crée pas d'ordre. Un OPERATOR ne valide pas.

## Modèle de domaine
- User(id, username, passwordHash, role, enabled, failedAttempts, lockedUntil)
- Order(id, reference, amount, currency, counterparty, status, createdBy, createdAt, decidedBy, decidedAt, decisionReason, version)
- Statuts : PENDING -> APPROVED | REJECTED. Aucune autre transition (409 sinon).
- Montant > 0, devise ISO 4217, contrepartie non vide.
- Verrou optimiste (`version`) : deux validations concurrentes => une seule réussit.

## API (REST, OpenAPI généré)
- POST /api/auth/login -> 200 {accessToken, expiresIn} + cookie refresh. 401 générique si échec.
- POST /api/auth/refresh -> 200 nouveau couple (rotation). 401 si invalide/expiré/rejoué.
- POST /api/auth/logout -> 204, révoque la famille, efface le cookie.
- POST /api/orders (OPERATOR) -> 201
- GET /api/orders/mine (OPERATOR) -> 200
- GET /api/orders?status=PENDING (VALIDATOR) -> 200 paginé
- POST /api/orders/{id}/approve (VALIDATOR) -> 200
- POST /api/orders/{id}/reject (VALIDATOR, motif obligatoire) -> 200
Erreurs : RFC 7807 (application/problem+json). 401 non authentifié, 403 mauvais rôle, 404, 409, 429.

## Critères d'acceptation
- AC1 : un access token expiré est refusé (401).
- AC2 : un refresh token utilisé deux fois révoque toute la famille.
- AC3 : un OPERATOR appelant /approve reçoit 403.
- AC4 : 6 échecs de login consécutifs => 429 / verrouillage temporaire.
- AC5 : l'appli se connecte avec `secureorder_app` ; `secureorder_app` ne peut pas faire de DDL ni lire `pg_authid`.
- AC6 : un INSERT/UPDATE/DELETE sur `transactions` produit une ligne `AUDIT: OBJECT` ; un SELECT sur `users` aussi.
- AC7 : chaque ligne d'audit contient horodatage, rôle de connexion, requête.
- AC8 : `docker compose up --build` démarre db + backend + frontend sans action manuelle.
- AC9 : le pipeline CI échoue si lint ou tests échouent.

## Hors périmètre (à documenter dans l'ADR de compromis)
Pas de MFA, pas de gestion d'utilisateurs en UI, rate limiting en mémoire (Redis en prod), pas de TLS local réel (reverse proxy en prod).
```

---

## 4. Skill Claude Code : `.claude/skills/secure-review/SKILL.md`

```markdown
---
name: secure-review
description: Revue sécurité du diff courant pour SecureOrder (JWT, refresh token, RBAC, CORS, pgaudit, moindre privilège). À utiliser avant de terminer une phase ou de commiter du code touchant auth, accès base ou configuration.
---

# secure-review

Passe le diff (`git diff` + fichiers non suivis) sur cette grille. Réponds par une liste PASS/FAIL avec fichier:ligne et correctif.

## Tokens
- Aucun secret en dur. Variables d'environnement uniquement.
- Algorithme de signature explicite, pas de `none`. Durée access <= 5 min.
- Refresh : opaque, hashé en base, rotation, détection de réutilisation, révocation de famille.
- Cookie : HttpOnly, Secure, SameSite=Strict, Path limité.
- Aucun token dans localStorage, les URL ou les logs.

## Accès
- Deny-by-default. `@PreAuthorize` sur chaque cas d'usage sensible.
- Contrôle d'objet (IDOR) : un OPERATOR ne lit que ses propres ordres.
- Messages d'erreur génériques. Pas de stack trace.

## Web
- CORS : origines explicites, pas de `*` avec credentials.
- En-têtes : CSP, HSTS, nosniff, frame-ancestors, Referrer-Policy.
- Rate limiting sur login et refresh.
- Validation des entrées, requêtes paramétrées.

## Base
- L'appli n'est pas superuser et n'a aucun droit DDL.
- pgaudit actif, `pgaudit.role` défini, GRANT corrects sur `transactions` et `users`.
- Pas de données sensibles en clair dans les logs d'audit (mots de passe).

## Architecture
- `domain` sans import Spring/JPA/Jackson. Test ArchUnit vert.

Termine par : les 3 risques résiduels les plus importants, à noter dans les ADR.
```

---

## 5. Prompts par phase (à coller un par un)

### Phase 0 – Plan (mode plan, sans code)
```
Lis AGENTS.md et docs/SPEC.md. Propose un plan d'implémentation en 8 phases pour SecureOrder
(squelette + docker, base + pgaudit, domaine, auth, ordres, frontend, tests + CI, documentation).
Pour chaque phase : fichiers à créer, risques, critère "terminé". Pas de code. Signale toute ambiguïté
de la spec et la décision par défaut que tu proposes.
```

### Phase 1 – Squelette et Docker
```
Crée la structure : backend/ (Spring Boot 3.5, Java 17, Maven wrapper, packages hexagonaux vides),
frontend/ (Vite React TS strict), db/ (Dockerfile), docker-compose.yml, .env.example, .gitignore, Makefile.
- backend/Dockerfile multi-stage (build Maven, runtime JRE 21 non-root).
- frontend/Dockerfile multi-stage (build Node, runtime nginx non-root) avec reverse proxy /api -> backend (même origine).
- docker-compose : db, backend, frontend, healthchecks, variables d'environnement externalisées, pas de secret commité.
- Ajoute un test ArchUnit qui impose les règles hexagonales de AGENTS.md.
Critère : `docker compose up --build` démarre les 3 services et le test ArchUnit passe.
```

### Phase 2 – PostgreSQL + pgaudit
```
Crée db/Dockerfile (postgres:17 + paquet postgresql-17-pgaudit) et db/init/01-roles.sql :
- CREATE EXTENSION pgaudit; rôles secureorder_migrator (propriétaire du schéma), secureorder_app (CONNECT + USAGE, droits DML via migrations), auditor (NOLOGIN).
- Configuration serveur dans docker-compose via `command: postgres -c shared_preload_libraries=pgaudit -c pgaudit.log=none -c pgaudit.role=auditor -c pgaudit.log_parameter=on -c pgaudit.log_relation=on -c log_line_prefix='%m [%p] user=%u db=%d app=%a '`.
Migrations Flyway (exécutées par secureorder_migrator) : users, orders (table `transactions`), refresh_tokens, avec index et contraintes (montant > 0, statut contrôlé).
Migration dédiée : GRANT INSERT, UPDATE, DELETE ON transactions TO auditor ; GRANT SELECT ON users TO auditor.
Ajoute scripts/show-audit.sh qui filtre `docker compose logs db` sur "AUDIT:".
Explique en commentaire pourquoi l'audit par objet (pgaudit.role) est plus précis que pgaudit.log='write' global.
Critère : un INSERT sur transactions et un SELECT sur users apparaissent dans show-audit.sh avec horodatage, utilisateur et requête.
```

### Phase 3 – Domaine et cas d'usage
```
Implémente `domain` et `application` : User, Order, statuts, machine d'états, exceptions métier, ports in/out,
cas d'usage CreateOrder, ListPendingOrders, ApproveOrder, RejectOrder, ListMyOrders.
Règles : transitions valides uniquement, motif obligatoire au rejet, un validateur ne valide pas un ordre qu'il aurait créé.
Tests unitaires purs (sans Spring) couvrant les cas nominaux et d'erreur. Pas d'annotation Spring dans domain.
```

### Phase 4 – Authentification
```
Implémente `adapter.out.security` et `adapter.in.web` pour l'auth selon docs/SPEC.md :
Argon2id, JWT 5 min (clé via variable d'environnement, minimum 256 bits, échec au démarrage si absente),
refresh token opaque hashé SHA-256 avec family_id, rotation, détection de réutilisation et révocation de famille,
cookie HttpOnly/Secure/SameSite=Strict/Path=/api/auth, logout, verrouillage après 5 échecs, rate limiting Bucket4j sur login et refresh.
Config Spring Security : stateless, deny-by-default, CORS strict, CSP/HSTS/nosniff/frame-ancestors/Referrer-Policy, erreurs RFC 7807.
Tests : unitaires (validation JWT, expiration, signature altérée) + intégration Testcontainers (login, refresh, rejeu, logout, 401/403/429).
Termine en invoquant secure-review (ou en appliquant sa grille) et corrige les FAIL.
```

### Phase 5 – API des ordres
```
Implémente les contrôleurs et l'adaptateur de persistance des ordres : DTO en records, validation Bean Validation,
@PreAuthorize par rôle, contrôle d'objet (OPERATOR ne voit que ses ordres), pagination, verrou optimiste, mapping entité JPA <-> domaine.
La datasource de l'appli utilise secureorder_app ; Flyway utilise secureorder_migrator (deux datasources/config distinctes).
Ajoute le contexte utilisateur dans les requêtes auditées (ex. commentaire SQL ou application_name par transaction) et documente la limite
(un seul rôle de connexion partagé par l'appli).
Tests d'intégration Testcontainers (image db/Dockerfile) : flux complet, 403 mauvais rôle, 409 double validation concurrente, et un test qui lit les logs du conteneur et vérifie la présence de lignes AUDIT.
```

### Phase 6 – Frontend
```
Dans frontend/ : écran de login, vue opérateur (formulaire d'ordre avec validation), vue validateur (tableau des PENDING, approuver/rejeter avec motif).
Client Axios avec : access token en mémoire (pas de localStorage), intercepteur 401 -> refresh single-flight (file d'attente des requêtes en échec), retry unique, redirection login si refresh échoue, credentials: include pour le cookie.
Route guards par rôle. Gestion d'erreurs lisible. Styles simples et accessibles.
Tests Vitest + RTL : session (refresh transparent, échec du refresh), formulaire (validation, soumission), guard de rôle.
```

### Phase 7 – CI/CD
```
Crée .github/workflows/ci.yml : jobs backend (setup-java 17, cache Maven, ./mvnw verify avec Testcontainers), frontend (Node LTS, npm ci, lint, test, build),
docker (build des 3 images, sans push) et scan de dépendances (OWASP Dependency-Check ou Trivy, non bloquant si faux positifs documentés).
Déclenché sur push et pull_request. Ajoute checkstyle/spotless côté Java et ESLint côté front.
```

### Phase 8 – Documentation et ADR
```
Écris README.md (prérequis, variables d'environnement, `docker compose up --build`, comptes de démo créés par migration de seed avec mots de passe en variables,
comment lancer les tests, comment voir les logs d'audit).
Écris docs/adr/ : 
- 001 refresh token (opaque + hash + rotation + famille + cookie) vs JWT refresh,
- 002 pgaudit par objet via rôle auditor vs log global, et pourquoi des rôles DB séparés,
- 003 état React (contexte + token en mémoire) et pourquoi pas localStorage,
- 004 compromis faits faute de temps (rate limiting en mémoire, pas de MFA, TLS, etc.).
Écris docs/architecture.md avec deux diagrammes Mermaid :
(1) séquence login -> accès -> 401 -> refresh -> rejeu détecté -> révocation ;
(2) flux de production distribué : app -> PostgreSQL (pgaudit) -> logs en csvlog/stderr -> Fluent Bit ou Vector -> OpenSearch/Loki -> SIEM, avec
    notes sur rétention, immuabilité, alertes (ex. lectures massives sur users), et sur le scale horizontal (API stateless, refresh tokens en base partagée, rate limiting sur Redis).
Un diagramme C4 niveau conteneur est un plus. Les ADR doivent citer les alternatives rejetées.
```

---

## 6. Checklist finale (grille de l'évaluateur)

| Critère | À vérifier |
|---|---|
| Vision d'architecte | Hexagonal respecté (ArchUnit), OpenAPI, ADR avec alternatives rejetées |
| Sécurité JWT | Access 5 min, refresh hashé, rotation, réutilisation détectée, aucun secret en dur |
| Cyber | Pas d'IDOR, CORS strict, en-têtes, rate limiting, erreurs génériques, pas de localStorage |
| PostgreSQL / pgaudit | Audit par objet via `auditor`, 3 rôles DB, l'appli non superuser, `show-audit.sh` prouve le contenu des logs |
| Tests | Testcontainers, cas 401/403/expiré/rejoué, tests front sur session et formulaire |
| DevOps | Dockerfiles multi-stage non-root, compose stable, CI verte |

## Planning conseillé (5 h)

- 0:30 plan + squelette + Docker
- 0:45 base + pgaudit + preuve d'audit
- 0:45 domaine + cas d'usage
- 1:00 auth + tests
- 0:30 API des ordres
- 0:45 frontend
- 0:30 CI + documentation + relecture

## Pièges courants à éviter

- pgaudit exige `shared_preload_libraries` au démarrage : sans lui l'extension échoue.
- `CREATE EXTENSION` demande un superuser : à faire dans le script d'init, pas dans Flyway.
- Les GRANT à `auditor` doivent venir après la création des tables (migration Flyway, pas script d'init).
- Cookie `Secure` en local : fonctionne sur `localhost` ; garde le front et l'API sur la même origine via nginx.
- Testcontainers doit utiliser ton image avec pgaudit, pas `postgres:17` brut.
- Un rejeu de refresh token doit invalider toute la famille, pas seulement le token rejoué.

## Préparation de l'entretien technique

Sois prêt à expliquer : pourquoi un refresh opaque plutôt qu'un JWT, comment la réutilisation est détectée, pourquoi l'audit par objet, ce qui manquerait en production (TLS, Redis, SIEM, rotation de clés, MFA), et comment tu as relu le code généré par l'agent.
