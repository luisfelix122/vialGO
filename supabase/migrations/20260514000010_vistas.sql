-- Migration 010: Views
-- vista_ranking_global: global leaderboard ranked by reputation per role.
-- security_invoker = true so RLS policies of the calling user apply.
-- Depends on: 002_tablas_core, 004_tablas_sesiones (progreso_lecciones not used here)

CREATE OR REPLACE VIEW public.vista_ranking_global
WITH (security_invoker = true)
AS
SELECT
    ru.usuario_id,
    u.nombre,
    u.dni,
    ru.rol,
    ru.reputacion,
    ru.xp_total,
    ru.racha_dias,
    RANK() OVER (
        PARTITION BY ru.rol
        ORDER BY ru.reputacion DESC, ru.xp_total DESC
    ) AS posicion
FROM public.roles_usuario ru
JOIN public.usuarios u ON u.id = ru.usuario_id
WHERE u.eliminado_en IS NULL
  AND u.esta_baneado = false;

-- Authenticated users can read the ranking; anonymous/guest cannot.
GRANT SELECT ON public.vista_ranking_global TO authenticated;
