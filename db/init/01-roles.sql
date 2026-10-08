-- Enable pgaudit extension (requires superuser)
CREATE EXTENSION IF NOT EXISTS pgaudit;

-- Create roles
CREATE ROLE secureorder_migrator LOGIN PASSWORD '${POSTGRES_PASSWORD}';
CREATE ROLE secureorder_app LOGIN PASSWORD '${POSTGRES_PASSWORD}';
CREATE ROLE auditor NOLOGIN;

-- Grant privileges
-- secureorder_migrator is the owner of the schema (will be set in Flyway migrations)
-- For now, we grant all privileges on the database to secureorder_migrator (so it can create schema)
GRANT ALL PRIVILEGES ON DATABASE ${POSTGRES_DB} TO secureorder_migrator;

-- secureorder_app: CONNECT and USAGE on the database, and will be granted DML privileges on tables via migrations
GRANT CONNECT, TEMPORARY ON DATABASE ${POSTGRES_DB} TO secureorder_app;

-- auditor: no login, but we will grant SELECT on specific tables and INSERT/UPDATE/DELETE on transactions via migrations