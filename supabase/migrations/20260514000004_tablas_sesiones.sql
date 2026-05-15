-- Migration 004: Session tables
-- sesiones, respuestas_sesion, clasificaciones, progreso_lecciones
-- Depends on: 003_tablas_contenido

-- ============================================================
-- tabla: sesiones
-- One row per game session; completada_en must be >= iniciada_en.
-- ============================================================
CREATE TABLE IF NOT EXISTS public.sesiones (
    id                UUID        PRIMARY KEY DEFAULT gen_random_uuid(),
    usuario_id        UUID        NOT NULL REFERENCES public.usuarios(id) ON DELETE CASCADE,
    leccion_id        UUID                 DEFAULT NULL REFERENCES public.lecciones(id) ON DELETE SET NULL,
    rol               TEXT        NOT NULL CHECK (rol IN ('conductor', 'peaton')),
    tipo              TEXT        NOT NULL CHECK (tipo IN ('normal', 'clasificacion', 'tutorial')),
    estado            TEXT        NOT NULL DEFAULT 'en_progreso'
                                  CHECK (estado IN ('en_progreso', 'completada', 'abandonada')),
    fue_minimizada    BOOLEAN     NOT NULL DEFAULT false,
    iniciada_en       TIMESTAMPTZ NOT NULL DEFAULT now(),
    completada_en     TIMESTAMPTZ          DEFAULT NULL,
    xp_ganado         INTEGER     NOT NULL DEFAULT 0 CHECK (xp_ganado >= 0),
    preguntas_totales SMALLINT    NOT NULL DEFAULT 0,
    CHECK (completada_en IS NULL OR completada_en >= iniciada_en)
);

-- ============================================================
-- tabla: respuestas_sesion
-- Each answer submitted during a session.
-- tiempo_respuesta_ms capped at 5000ms (5 seconds).
-- ============================================================
CREATE TABLE IF NOT EXISTS public.respuestas_sesion (
    id                  UUID        PRIMARY KEY DEFAULT gen_random_uuid(),
    sesion_id           UUID        NOT NULL REFERENCES public.sesiones(id) ON DELETE CASCADE,
    pregunta_id         UUID        NOT NULL REFERENCES public.preguntas(id),
    opcion_id           UUID        NOT NULL REFERENCES public.opciones_pregunta(id),
    fue_correcta        BOOLEAN     NOT NULL,
    tiempo_respuesta_ms INTEGER     NOT NULL
                                    CHECK (tiempo_respuesta_ms > 0 AND tiempo_respuesta_ms <= 5000),
    xp_obtenido         INTEGER     NOT NULL DEFAULT 0 CHECK (xp_obtenido >= 0),
    es_reintento        BOOLEAN     NOT NULL DEFAULT false,
    respondida_en       TIMESTAMPTZ NOT NULL DEFAULT now()
);

-- ============================================================
-- tabla: clasificaciones
-- Records the placement result for a user+role pair.
-- UNIQUE(usuario_id, rol) enforces one classification per role.
-- ============================================================
CREATE TABLE IF NOT EXISTS public.clasificaciones (
    id                  UUID        PRIMARY KEY DEFAULT gen_random_uuid(),
    usuario_id          UUID        NOT NULL REFERENCES public.usuarios(id) ON DELETE CASCADE,
    rol                 TEXT        NOT NULL CHECK (rol IN ('conductor', 'peaton')),
    sesion_id           UUID        NOT NULL REFERENCES public.sesiones(id),
    reputacion_inicial  NUMERIC(5,2) NOT NULL
                                    CHECK (reputacion_inicial >= 0 AND reputacion_inicial <= 100),
    completada_en       TIMESTAMPTZ NOT NULL DEFAULT now(),
    UNIQUE (usuario_id, rol)
);

-- ============================================================
-- tabla: progreso_lecciones
-- Tracks lesson completion, stars, and best XP per user+role.
-- UNIQUE(usuario_id, leccion_id, rol) prevents duplicate progress rows.
-- ============================================================
CREATE TABLE IF NOT EXISTS public.progreso_lecciones (
    id            UUID        PRIMARY KEY DEFAULT gen_random_uuid(),
    usuario_id    UUID        NOT NULL REFERENCES public.usuarios(id) ON DELETE CASCADE,
    leccion_id    UUID        NOT NULL REFERENCES public.lecciones(id) ON DELETE CASCADE,
    rol           TEXT        NOT NULL CHECK (rol IN ('conductor', 'peaton')),
    completada    BOOLEAN     NOT NULL DEFAULT false,
    estrellas     SMALLINT    NOT NULL DEFAULT 0 CHECK (estrellas >= 0 AND estrellas <= 3),
    mejor_xp      INTEGER     NOT NULL DEFAULT 0 CHECK (mejor_xp >= 0),
    completada_en TIMESTAMPTZ          DEFAULT NULL,
    actualizado_en TIMESTAMPTZ NOT NULL DEFAULT now(),
    UNIQUE (usuario_id, leccion_id, rol)
);
