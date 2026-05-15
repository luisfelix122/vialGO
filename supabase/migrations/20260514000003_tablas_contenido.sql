-- Migration 003: Content tables
-- modulos, lecciones, categorias_pregunta, preguntas, opciones_pregunta
-- Depends on: 002_tablas_core

-- ============================================================
-- tabla: modulos
-- Top-level learning units, scoped by role.
-- ============================================================
CREATE TABLE IF NOT EXISTS public.modulos (
    id          UUID      PRIMARY KEY DEFAULT gen_random_uuid(),
    rol         TEXT      NOT NULL CHECK (rol IN ('conductor', 'peaton')),
    nombre      TEXT      NOT NULL,
    descripcion TEXT               DEFAULT NULL,
    orden       SMALLINT  NOT NULL CHECK (orden > 0),
    esta_activo BOOLEAN   NOT NULL DEFAULT true,
    UNIQUE (rol, orden)
);

-- ============================================================
-- tabla: lecciones
-- Sub-units within a module.
-- ============================================================
CREATE TABLE IF NOT EXISTS public.lecciones (
    id          UUID      PRIMARY KEY DEFAULT gen_random_uuid(),
    modulo_id   UUID      NOT NULL REFERENCES public.modulos(id) ON DELETE CASCADE,
    nombre      TEXT      NOT NULL,
    descripcion TEXT               DEFAULT NULL,
    orden       SMALLINT  NOT NULL CHECK (orden > 0),
    esta_activa BOOLEAN   NOT NULL DEFAULT true,
    UNIQUE (modulo_id, orden)
);

-- ============================================================
-- tabla: categorias_pregunta
-- Question categories; can apply to conductor, peaton, or both.
-- ============================================================
CREATE TABLE IF NOT EXISTS public.categorias_pregunta (
    id          UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    nombre      TEXT NOT NULL,
    rol         TEXT NOT NULL CHECK (rol IN ('conductor', 'peaton', 'ambos')),
    descripcion TEXT DEFAULT NULL
);

-- ============================================================
-- tabla: preguntas
-- Each question references a category and optionally a lesson.
-- Media is always required (video or image).
-- ============================================================
CREATE TABLE IF NOT EXISTS public.preguntas (
    id                   UUID      PRIMARY KEY DEFAULT gen_random_uuid(),
    categoria_id         UUID      NOT NULL REFERENCES public.categorias_pregunta(id),
    leccion_id           UUID               DEFAULT NULL REFERENCES public.lecciones(id) ON DELETE SET NULL,
    tipo_medio           TEXT      NOT NULL CHECK (tipo_medio IN ('video', 'imagen')),
    url_medio            TEXT      NOT NULL CHECK (url_medio <> ''),
    duracion_medio_seg   SMALLINT  NOT NULL DEFAULT 3
                                   CHECK (duracion_medio_seg > 0 AND duracion_medio_seg <= 30),
    texto_consecuencia   TEXT      NOT NULL,
    es_clasificacion     BOOLEAN   NOT NULL DEFAULT false,
    esta_activa          BOOLEAN   NOT NULL DEFAULT true,
    creado_en            TIMESTAMPTZ NOT NULL DEFAULT now()
);

-- ============================================================
-- tabla: opciones_pregunta
-- Answer options for a question; exactly one must be correct.
-- Order constrained to 1-4. Uniqueness enforced by trigger (TASK-009).
-- ============================================================
CREATE TABLE IF NOT EXISTS public.opciones_pregunta (
    id           UUID     PRIMARY KEY DEFAULT gen_random_uuid(),
    pregunta_id  UUID     NOT NULL REFERENCES public.preguntas(id) ON DELETE CASCADE,
    texto        TEXT     NOT NULL,
    imagen_url   TEXT              DEFAULT NULL,
    es_correcta  BOOLEAN  NOT NULL DEFAULT false,
    orden        SMALLINT NOT NULL CHECK (orden >= 1 AND orden <= 4),
    UNIQUE (pregunta_id, orden)
);
