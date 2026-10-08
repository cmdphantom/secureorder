-- Grant privileges to auditor role for pgaudit
-- This migration should be executed after the tables are created

-- Grant INSERT, UPDATE, DELETE on transactions to auditor
GRANT INSERT, UPDATE, DELETE ON transactions TO auditor;

-- Grant SELECT on users to auditor
GRANT SELECT ON users TO auditor;

-- Note: The auditor role is NOLOGIN and is used by pgaudit for object-level auditing
-- The actual audit configuration is done at the database level via shared_preload_libraries
-- and pgaudit.role setting in the PostgreSQL configuration