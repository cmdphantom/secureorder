.PHONY: backend frontend all test build

backend:
	cd backend && ./mvnw verify

frontend:
	cd frontend && npm ci && npm run lint && npm test && npm run build

all: backend frontend

test:
	@$(MAKE) backend
	@$(MAKE) frontend

build:
	docker compose build

up:
	docker compose up --build

down:
	docker compose down

logs:
	docker compose logs -f

audit:
	./scripts/show-audit.sh