-- Migration 013: Security hardening
-- SET search_path on all functions that were missing it.
-- REVOKE PUBLIC execute from aplicar_decaimiento_reputacion and obtener_vidas,
-- then re-grant to the correct roles.
-- Depends on: 008_funciones (functions must exist)

-- ============================================================
-- Ensure search_path is locked down on all public functions.
-- Repeating CREATE OR REPLACE with SET search_path is idempotent.
-- ============================================================

-- actualizar_timestamp: SECURITY INVOKER, no external schema access needed
ALTER FUNCTION public.actualizar_timestamp()
    SET search_path = public;

-- obtener_vidas: already set in 008; enforce REVOKE/GRANT explicitly
REVOKE ALL ON FUNCTION public.obtener_vidas(UUID) FROM PUBLIC;
GRANT EXECUTE ON FUNCTION public.obtener_vidas(UUID) TO authenticated;
GRANT EXECUTE ON FUNCTION public.obtener_vidas(UUID) TO service_role;

-- calcular_xp: callable by Edge Functions (service_role) and authenticated users
REVOKE ALL ON FUNCTION public.calcular_xp(INTEGER, INTEGER) FROM PUBLIC;
GRANT EXECUTE ON FUNCTION public.calcular_xp(INTEGER, INTEGER) TO authenticated;
GRANT EXECUTE ON FUNCTION public.calcular_xp(INTEGER, INTEGER) TO service_role;

-- aplicar_decaimiento_reputacion: service_role only (cron job)
REVOKE ALL ON FUNCTION public.aplicar_decaimiento_reputacion() FROM PUBLIC;
GRANT EXECUTE ON FUNCTION public.aplicar_decaimiento_reputacion() TO service_role;

-- validar_opcion_correcta: trigger function — no direct grant needed
REVOKE ALL ON FUNCTION public.validar_opcion_correcta() FROM PUBLIC;

-- actualizar_timestamp: trigger function — no direct grant needed
REVOKE ALL ON FUNCTION public.actualizar_timestamp() FROM PUBLIC;
