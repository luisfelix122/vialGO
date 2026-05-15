-- Migration 015: Seed initial data
-- configuracion_juego: singleton row with default game parameters.
-- Depends on: 005_tablas_gamificacion

-- Insert only if the table is empty (idempotent).
INSERT INTO public.configuracion_juego (
    dias_para_multiplicador,
    valor_multiplicador,
    horas_recarga_vidas,
    reputacion_minima_beneficios,
    decay_porcentaje_diario,
    decay_dias_gracia
)
SELECT
    14,     -- streak days required to activate XP multiplier
    1.50,   -- XP multiplier factor (1.5x)
    12,     -- hours before lives refill from 0 to 5
    70.0,   -- minimum reputation % to unlock benefits
    2.0,    -- daily reputation decay percentage after grace period
    3       -- inactivity grace period in days before decay starts
WHERE NOT EXISTS (
    SELECT 1 FROM public.configuracion_juego
);
