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