-- Migration 009: Triggers
-- 5 timestamp triggers (actualizar_timestamp) + 1 constraint trigger (validar_opcion_correcta)
-- Depends on: 008_funciones

-- ============================================================
-- Timestamp auto-update triggers
-- Fire BEFORE UPDATE on every table that has actualizado_en.
-- ============================================================

CREATE OR REPLACE TRIGGER trg_actualizar_timestamp_usuarios
    BEFORE UPDATE ON public.usuarios
    FOR EACH ROW EXECUTE FUNCTION public.actualizar_timestamp();

CREATE OR REPLACE TRIGGER trg_actualizar_timestamp_roles_usuario
    BEFORE UPDATE ON public.roles_usuario
    FOR EACH ROW EXECUTE FUNCTION public.actualizar_timestamp();

CREATE OR REPLACE TRIGGER trg_actualizar_timestamp_vidas_usuario
    BEFORE UPDATE ON public.vidas_usuario
    FOR EACH ROW EXECUTE FUNCTION public.actualizar_timestamp();

CREATE OR REPLACE TRIGGER trg_actualizar_timestamp_progreso_lecciones
    BEFORE UPDATE ON public.progreso_lecciones
    FOR EACH ROW EXECUTE FUNCTION public.actualizar_timestamp();

CREATE OR REPLACE TRIGGER trg_actualizar_timestamp_estadisticas_usuario
    BEFORE UPDATE ON public.estadisticas_usuario
    FOR EACH ROW EXECUTE FUNCTION public.actualizar_timestamp();

-- ============================================================
-- Constraint trigger: exactly one correct option per question.
-- Fires BEFORE INSERT OR UPDATE on opciones_pregunta.
-- ============================================================

CREATE OR REPLACE TRIGGER trg_validar_opcion_correcta
    BEFORE INSERT OR UPDATE ON public.opciones_pregunta
    FOR EACH ROW EXECUTE FUNCTION public.validar_opcion_correcta();
