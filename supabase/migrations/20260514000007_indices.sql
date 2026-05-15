-- Migration 007: Explicit indexes for query-critical paths
-- 16 idx_ indexes covering ranking, sessions, content, notifications, and analytics.
-- Depends on: 001 through 006

-- Ranking query: leaderboard sorted by reputation per role
CREATE INDEX IF NOT EXISTS idx_roles_usuario_reputacion
    ON public.roles_usuario (rol, reputacion DESC);

-- User session history: fetch sessions for a user, most recent first
CREATE INDEX IF NOT EXISTS idx_sesiones_usuario_rol
    ON public.sesiones (usuario_id, rol, iniciada_en DESC);

-- Session answers lookup: fetch all answers for a session
CREATE INDEX IF NOT EXISTS idx_respuestas_sesion_sesion
    ON public.respuestas_sesion (sesion_id);

-- Fetch active questions for a lesson
CREATE INDEX IF NOT EXISTS idx_preguntas_leccion_activa
    ON public.preguntas (leccion_id, esta_activa)
    WHERE esta_activa = true;

-- Classification test: fetch active classification questions
CREATE INDEX IF NOT EXISTS idx_preguntas_clasificacion
    ON public.preguntas (es_clasificacion, esta_activa)
    WHERE es_clasificacion = true AND esta_activa = true;

-- Admin heat map: questions with highest failure rates per role
CREATE INDEX IF NOT EXISTS idx_analiticas_fallo
    ON public.analiticas_preguntas (rol, tasa_fallo DESC);

-- Lesson progress per user per role (learning map rendering)
CREATE INDEX IF NOT EXISTS idx_progreso_lecciones_usuario
    ON public.progreso_lecciones (usuario_id, rol);

-- Streak history lookup
CREATE INDEX IF NOT EXISTS idx_historial_rachas_usuario
    ON public.historial_rachas (usuario_id, rol);

-- Notification scheduling: find users who haven't completed today's session
CREATE INDEX IF NOT EXISTS idx_roles_usuario_sesion_hoy
    ON public.roles_usuario (sesion_completada_hoy)
    WHERE sesion_completada_hoy IS NULL OR sesion_completada_hoy < CURRENT_DATE;

-- Error reports: admin views unreviewed reports
CREATE INDEX IF NOT EXISTS idx_reportes_error_revisado
    ON public.reportes_error (revisado, creado_en DESC)
    WHERE revisado = false;

-- Active tokens for push notifications
CREATE INDEX IF NOT EXISTS idx_tokens_dispositivo_usuario
    ON public.tokens_dispositivo (usuario_id)
    WHERE esta_activo = true;

-- Decay candidates: users inactive beyond grace period
CREATE INDEX IF NOT EXISTS idx_roles_usuario_decay
    ON public.roles_usuario (ultima_sesion)
    WHERE ultima_sesion IS NOT NULL;

-- Lessons within a module (learning map order)
CREATE INDEX IF NOT EXISTS idx_lecciones_modulo
    ON public.lecciones (modulo_id, orden);

-- Modules per role (learning map)
CREATE INDEX IF NOT EXISTS idx_modulos_rol
    ON public.modulos (rol, orden);

-- Session state: find in-progress sessions (cleanup/recovery)
CREATE INDEX IF NOT EXISTS idx_sesiones_estado
    ON public.sesiones (estado)
    WHERE estado = 'en_progreso';

-- Decay audit: lookup decay history per user+role
CREATE INDEX IF NOT EXISTS idx_registro_decaimiento_usuario
    ON public.registro_decaimiento (usuario_id, rol, aplicado_en DESC);
