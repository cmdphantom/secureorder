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