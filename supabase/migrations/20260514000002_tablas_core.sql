-- Migration 002: Core tables
-- usuarios, roles_usuario, vidas_usuario, intentos_auth
-- Depends on: 001_extensiones

-- ============================================================
-- tabla: usuarios
-- Linked to auth.users; stores app-level profile per user.
-- ============================================================
CREATE TABLE IF NOT EXISTS public.usuarios (
    id                         UUID        PRIMARY KEY REFERENCES auth.users(id) ON DELETE CASCADE,
    dni                        CHAR(8)     NOT NULL UNIQUE CHECK (dni ~ '^\d{8}$'),
    nombre                     TEXT        NOT NULL,
    rol_activo                 TEXT        NOT NULL DEFAULT 'conductor'
                                           CHECK (rol_activo IN ('conductor', 'peaton')),
    pregunta_seguridad         TEXT        NOT NULL,
    respuesta_seguridad_hash   TEXT        NOT NULL,
    compromiso_minutos         SMALLINT    NOT NULL DEFAULT 2
                                           CHECK (compromiso_minutos IN (2, 3, 5)),
    tutorial_completado        BOOLEAN     NOT NULL DEFAULT false,
    debe_cambiar_pregunta      BOOLEAN     NOT NULL DEFAULT false,
    esta_baneado               BOOLEAN     NOT NULL DEFAULT false,
    eliminado_en               TIMESTAMPTZ          DEFAULT NULL,
    fecha_registro             TIMESTAMPTZ NOT NULL DEFAULT now(),
    actualizado_en             TIMESTAMPTZ NOT NULL DEFAULT now()
);

-- ============================================================
-- tabla: roles_usuario
-- One row per (usuario, rol); tracks XP, reputation, streak.
-- ============================================================
CREATE TABLE IF NOT EXISTS public.roles_usuario (
    id                       UUID        PRIMARY KEY DEFAULT gen_random_uuid(),
    usuario_id               UUID        NOT NULL REFERENCES public.usuarios(id) ON DELETE CASCADE,
    rol                      TEXT        NOT NULL CHECK (rol IN ('conductor', 'peaton')),
    xp_total                 INTEGER     NOT NULL DEFAULT 0 CHECK (xp_total >= 0),
    reputacion               NUMERIC(5,2) NOT NULL DEFAULT 0
                                          CHECK (reputacion >= 0 AND reputacion <= 100),
    racha_dias               INTEGER     NOT NULL DEFAULT 0 CHECK (racha_dias >= 0),
    racha_maxima             INTEGER     NOT NULL DEFAULT 0 CHECK (racha_maxima >= 0),
    ultima_sesion            DATE                 DEFAULT NULL,
    sesion_completada_hoy    DATE                 DEFAULT NULL,
    clasificacion_completada BOOLEAN     NOT NULL DEFAULT false,
    actualizado_en           TIMESTAMPTZ NOT NULL DEFAULT now(),
    UNIQUE (usuario_id, rol)
);

-- ============================================================
-- tabla: vidas_usuario
-- One row per usuario; tracks lives and last refill time.
-- ============================================================
CREATE TABLE IF NOT EXISTS public.vidas_usuario (
    id              UUID        PRIMARY KEY DEFAULT gen_random_uuid(),
    usuario_id      UUID        NOT NULL UNIQUE REFERENCES public.usuarios(id) ON DELETE CASCADE,
    vidas_actuales  SMALLINT    NOT NULL DEFAULT 5
                                CHECK (vidas_actuales >= 0 AND vidas_actuales <= 5),
    ultima_recarga  TIMESTAMPTZ NOT NULL DEFAULT now(),
    actualizado_en  TIMESTAMPTZ NOT NULL DEFAULT now()
);

-- ============================================================
-- tabla: intentos_auth
-- Rate-limiting table for /auth/recover (5 attempts per DNI per hour).
-- Keyed by DNI; not linked to auth.users (user may not exist yet).
-- ============================================================
CREATE TABLE IF NOT EXISTS public.intentos_auth (
    dni             CHAR(8)     PRIMARY KEY CHECK (dni ~ '^\d{8}$'),
    intentos        SMALLINT    NOT NULL DEFAULT 1,
    ventana_inicio  TIMESTAMPTZ NOT NULL DEFAULT now()
);
