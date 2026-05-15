-- Migration 001: Enable required PostgreSQL extensions
-- pgcrypto: bcrypt hashing for security question answers (available all plans)
-- Note: pg_cron removed — requires Pro+ plan; reputation decay uses external cron fallback

CREATE EXTENSION IF NOT EXISTS pgcrypto WITH SCHEMA extensions;
