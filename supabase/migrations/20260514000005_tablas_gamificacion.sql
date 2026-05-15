-- Migration 005: Gamification and analytics tables
-- historial_rachas, configuracion_juego, estadisticas_usuario, registro_decaimiento,
-- analiticas_preguntas
-- Depends on: 002_tablas_core, 003_tablas_contenido (preguntas)

-- ============================================================
-- tabla: historial_rachas
-- Records every streak after it breaks. Enables streak history display and analytics.
-- ============================================================
CREATE TABLE IF NOT EXISTS public.historial_rachas (
    id                      UUID    PRIMARY KEY DEFAULT gen_random_uuid(),
    usuario_id              UUID    NOT NULL REFERENCES public.usuarios(id) ON DELETE CASCADE,
    rol                     TEXT    NOT NULL CHECK (rol IN ('conductor', 'peaton')),
    fecha_inicio            DATE    NOT NULL,
    fecha_fin               DATE    NOT NULL,
    dias_totales            INTEGER NOT NULL CHECK (dias_totales > 0),
    xp_multiplicador_activo BOOLEAN NOT NULL DEFAULT false
);

-- ============================================================
-- tabla: configuracion_juego
-- Singleton config table (1 row). Game-wide parameters:
-- streak multiplier, lives recharge, reputation threshold, decay parameters.
-- ============================================================
CREATE TABLE IF NOT EXISTS public.configuracion_juego (
    id                           UUID         PRIMARY KEY DEFAULT gen_random_uuid(),
    dias_para_multiplicador      SMALLINT     NOT NULL DEFAULT 14 CHECK (dias_para_multiplicador > 0),
    valor_multiplicador          NUMERIC(3,2) NOT NULL DEFAULT 1.50 CHECK (valor_multiplicador > 1),
    horas_recarga_vidas          SMALLINT     NOT NULL DEFAULT 12 CHECK (horas_recarga_vidas > 0),
    reputacion_minima_beneficios NUMERIC(5,2) NOT NULL DEFAULT 70.0
                                              CHECK (reputacion_minima_beneficios >= 0 AND reputacion_minima_beneficios <= 100),
    decay_porcentaje_diario      NUMERIC(4,2) NOT NULL DEFAULT 2.0
                                              CHECK (decay_porcentaje_diario >= 0 AND decay_porcentaje_diario <= 100),
    decay_dias_gracia            SMALLINT     NOT NULL DEFAULT 3 CHECK (decay_dias_gracia >= 0)
);

-- ============================================================
-- tabla: estadisticas_usuario
-- Cumulative stats per user per role. Updated by trigger on session completion.
-- vidas_salvadas: narrative metric — count of correct safety decisions, not game lives.
-- ============================================================
CREATE TABLE IF NOT EXISTS public.estadisticas_usuario (
    id               UUID        PRIMARY KEY DEFAULT gen_random_uuid(),
    usuario_id       UUID        NOT NULL REFERENCES public.usuarios(id) ON DELETE CASCADE,
    rol              TEXT        NOT NULL CHECK (rol IN ('conductor', 'peaton')),
    total_sesiones   INTEGER     NOT NULL DEFAULT 0 CHECK (total_sesiones >= 0),
    total_preguntas  INTEGER     NOT NULL DEFAULT 0 CHECK (total_preguntas >= 0),
    total_correctas  INTEGER     NOT NULL DEFAULT 0 CHECK (total_correctas >= 0),
    tiempo_total_ms  BIGINT      NOT NULL DEFAULT 0 CHECK (tiempo_total_ms >= 0),
    vidas_salvadas   INTEGER     NOT NULL DEFAULT 0 CHECK (vidas_salvadas >= 0),
    actualizado_en   TIMESTAMPTZ NOT NULL DEFAULT now(),
    UNIQUE (usuario_id, rol)
);

-- ============================================================
-- tabla: registro_decaimiento
-- Audit trail for reputation decay events.
-- ============================================================
CREATE TABLE IF NOT EXISTS public.registro_decaimiento (
    id                UUID        PRIMARY KEY DEFAULT gen_random_uuid(),
    usuario_id        UUID        NOT NULL REFERENCES public.usuarios(id) ON DELETE CASCADE,
    rol               TEXT        NOT NULL CHECK (rol IN ('conductor', 'peaton')),
    reputacion_antes  NUMERIC(5,2) NOT NULL CHECK (reputacion_antes >= 0 AND reputacion_antes <= 100),
    reputacion_despues NUMERIC(5,2) NOT NULL CHECK (reputacion_despues >= 0 AND reputacion_despues <= 100),
    dias_inactivo     INTEGER     NOT NULL CHECK (dias_inactivo > 0),
    aplicado_en       TIMESTAMPTZ NOT NULL DEFAULT now()
);

-- ============================================================
-- tabla: analiticas_preguntas
-- Aggregate per-question statistics updated by trigger on respuestas_sesion INSERT.
-- Feeds admin heat-map analytics. tasa_fallo is a generated stored column.
-- ============================================================
CREATE TABLE IF NOT EXISTS public.analiticas_preguntas (
    pregunta_id     UUID         NOT NULL REFERENCES public.preguntas(id) ON DELETE CASCADE,
    rol             TEXT         NOT NULL CHECK (rol IN ('conductor', 'peaton')),
    total_intentos  INTEGER      NOT NULL DEFAULT 0 CHECK (total_intentos >= 0),
    total_correctas INTEGER      NOT NULL DEFAULT 0 CHECK (total_correctas >= 0),
    tasa_fallo      NUMERIC(5,2) GENERATED ALWAYS AS (
                        (total_intentos - total_correctas)::NUMERIC
                        / NULLIF(total_intentos, 0) * 100
                    ) STORED,
    PRIMARY KEY (pregunta_id, rol)
);
