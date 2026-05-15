-- Migration 006: Support tables
-- beneficios, reportes_error, notificaciones_programadas, tokens_dispositivo
-- Depends on: 002_tablas_core

-- ============================================================
-- tabla: beneficios
-- All benefits show as locked / "Proximamente" for MVP.
-- disponible = false for all rows at launch.
-- ============================================================
CREATE TABLE IF NOT EXISTS public.beneficios (
    id                UUID         PRIMARY KEY DEFAULT gen_random_uuid(),
    titulo            TEXT         NOT NULL,
    descripcion       TEXT         NOT NULL,
    imagen_url        TEXT                  DEFAULT NULL,
    rol               TEXT         NOT NULL CHECK (rol IN ('conductor', 'peaton', 'ambos')),
    reputacion_minima NUMERIC(5,2) NOT NULL DEFAULT 70.0
                                   CHECK (reputacion_minima >= 0 AND reputacion_minima <= 100),
    esta_activo       BOOLEAN      NOT NULL DEFAULT true,
    disponible        BOOLEAN      NOT NULL DEFAULT false,
    orden             SMALLINT     NOT NULL DEFAULT 0
);

-- ============================================================
-- tabla: reportes_error
-- Users report incorrect questions. admin reviews via service_role.
-- usuario_id and pregunta_id are nullable (question/user may be deleted).
-- ============================================================
CREATE TABLE IF NOT EXISTS public.reportes_error (
    id          UUID        PRIMARY KEY DEFAULT gen_random_uuid(),
    usuario_id  UUID                 DEFAULT NULL REFERENCES public.usuarios(id) ON DELETE SET NULL,
    pregunta_id UUID                 DEFAULT NULL REFERENCES public.preguntas(id) ON DELETE SET NULL,
    descripcion TEXT        NOT NULL,
    revisado    BOOLEAN     NOT NULL DEFAULT false,
    creado_en   TIMESTAMPTZ NOT NULL DEFAULT now()
);

-- ============================================================
-- tabla: notificaciones_programadas
-- Notification schedule per user. Created at registration (19:00, 21:00, 23:00).
-- Notification job checks roles_usuario.sesion_completada_hoy to skip users who already played.
-- ============================================================
CREATE TABLE IF NOT EXISTS public.notificaciones_programadas (
    id         UUID    PRIMARY KEY DEFAULT gen_random_uuid(),
    usuario_id UUID    NOT NULL REFERENCES public.usuarios(id) ON DELETE CASCADE,
    hora_envio TIME    NOT NULL,
    esta_activa BOOLEAN NOT NULL DEFAULT true,
    UNIQUE (usuario_id, hora_envio)
);

-- ============================================================
-- tabla: tokens_dispositivo
-- FCM push tokens per device. UNIQUE(usuario_id, token_fcm) prevents duplicates.
-- ============================================================
CREATE TABLE IF NOT EXISTS public.tokens_dispositivo (
    id         UUID        PRIMARY KEY DEFAULT gen_random_uuid(),
    usuario_id UUID        NOT NULL REFERENCES public.usuarios(id) ON DELETE CASCADE,
    token_fcm  TEXT        NOT NULL,
    plataforma TEXT        NOT NULL CHECK (plataforma IN ('android', 'ios')),
    esta_activo BOOLEAN    NOT NULL DEFAULT true,
    creado_en  TIMESTAMPTZ NOT NULL DEFAULT now(),
    UNIQUE (usuario_id, token_fcm)
);
