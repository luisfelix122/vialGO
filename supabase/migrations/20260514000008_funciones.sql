-- Migration 008: Database functions
-- actualizar_timestamp, obtener_vidas, calcular_xp,
-- aplicar_decaimiento_reputacion, validar_opcion_correcta
-- Depends on: 005_tablas_gamificacion (for configuracion_juego, registro_decaimiento)
-- NOTE: pgcrypto functions must be called as extensions.crypt / extensions.gen_salt
--       because pgcrypto is installed in the extensions schema.

-- ============================================================
-- function: actualizar_timestamp()
-- Generic trigger function to set actualizado_en = now() on UPDATE.
-- Applied to: usuarios, roles_usuario, vidas_usuario,
--             progreso_lecciones, estadisticas_usuario.
-- ============================================================
CREATE OR REPLACE FUNCTION public.actualizar_timestamp()
RETURNS TRIGGER
LANGUAGE plpgsql
SECURITY INVOKER
SET search_path = public
AS $$
BEGIN
    NEW.actualizado_en = now();
    RETURN NEW;
END;
$$;

-- ============================================================
-- function: obtener_vidas(p_usuario_id UUID)
-- Lazy lives recharge: if vidas_actuales = 0 AND >= 12h since
-- ultima_recarga, atomically reset to 5. Returns current count.
-- ============================================================
CREATE OR REPLACE FUNCTION public.obtener_vidas(p_usuario_id UUID)
RETURNS SMALLINT
LANGUAGE plpgsql
SECURITY DEFINER
SET search_path = public
AS $$
DECLARE
    v_vidas       SMALLINT;
    v_recarga     TIMESTAMPTZ;
    v_horas_config SMALLINT;
BEGIN
    SELECT vidas_actuales, ultima_recarga
      INTO v_vidas, v_recarga
      FROM public.vidas_usuario
     WHERE usuario_id = p_usuario_id
       FOR UPDATE;

    IF NOT FOUND THEN
        RAISE EXCEPTION 'vidas_usuario not found for usuario_id %', p_usuario_id;
    END IF;

    IF v_vidas = 0 THEN
        SELECT horas_recarga_vidas
          INTO v_horas_config
          FROM public.configuracion_juego
         LIMIT 1;

        IF v_horas_config IS NULL THEN
            v_horas_config := 12;
        END IF;

        IF now() - v_recarga >= (v_horas_config || ' hours')::INTERVAL THEN
            UPDATE public.vidas_usuario
               SET vidas_actuales = 5,
                   ultima_recarga = now(),
                   actualizado_en = now()
             WHERE usuario_id = p_usuario_id;

            v_vidas := 5;
        END IF;
    END IF;

    RETURN v_vidas;
END;
$$;

REVOKE ALL ON FUNCTION public.obtener_vidas(UUID) FROM PUBLIC;
GRANT EXECUTE ON FUNCTION public.obtener_vidas(UUID) TO authenticated;
GRANT EXECUTE ON FUNCTION public.obtener_vidas(UUID) TO service_role;

-- ============================================================
-- function: calcular_xp(p_tiempo_ms INTEGER, p_racha_dias INTEGER)
-- Speed-based XP with optional streak multiplier.
-- Base: 10 XP (at 5000ms) to 50 XP (at 1000ms), linear interpolation.
-- Multiplier: applied when p_racha_dias >= dias_para_multiplicador in config.
-- ============================================================
CREATE OR REPLACE FUNCTION public.calcular_xp(
    p_tiempo_ms  INTEGER,
    p_racha_dias INTEGER
)
RETURNS INTEGER
LANGUAGE plpgsql
SECURITY INVOKER
SET search_path = public
AS $$
DECLARE
    v_xp_base           NUMERIC;
    v_dias_multiplicador SMALLINT;
    v_valor_multiplicador NUMERIC(3,2);
    v_xp_final          INTEGER;
BEGIN
    -- Clamp input to valid range
    p_tiempo_ms := GREATEST(1, LEAST(p_tiempo_ms, 5000));

    -- Linear interpolation: 50 XP at 1000ms, 10 XP at 5000ms
    -- slope = (10 - 50) / (5000 - 1000) = -0.01 per ms
    v_xp_base := 50.0 + (p_tiempo_ms - 1000) * (-40.0 / 4000.0);
    v_xp_base := GREATEST(10, LEAST(50, v_xp_base));

    -- Fetch streak multiplier config
    SELECT dias_para_multiplicador, valor_multiplicador
      INTO v_dias_multiplicador, v_valor_multiplicador
      FROM public.configuracion_juego
     LIMIT 1;

    IF v_dias_multiplicador IS NULL THEN
        v_dias_multiplicador  := 14;
        v_valor_multiplicador := 1.50;
    END IF;

    IF p_racha_dias >= v_dias_multiplicador THEN
        v_xp_base := v_xp_base * v_valor_multiplicador;
    END IF;

    v_xp_final := ROUND(v_xp_base)::INTEGER;
    RETURN v_xp_final;
END;
$$;

-- ============================================================
-- function: aplicar_decaimiento_reputacion()
-- Daily reputation decay: -decay_porcentaje_diario% per day after
-- decay_dias_gracia days of inactivity. Floor at 0.
-- Idempotent for a given day (skips users already decayed today).
-- Called by external cron (Free plan) or pg_cron (Pro+ plan).
-- ============================================================
CREATE OR REPLACE FUNCTION public.aplicar_decaimiento_reputacion()
RETURNS VOID
LANGUAGE plpgsql
SECURITY DEFINER
SET search_path = public
AS $$
DECLARE
    v_porcentaje   NUMERIC(4,2);
    v_dias_gracia  SMALLINT;
    rec            RECORD;
    v_nueva_rep    NUMERIC(5,2);
BEGIN
    SELECT decay_porcentaje_diario, decay_dias_gracia
      INTO v_porcentaje, v_dias_gracia
      FROM public.configuracion_juego
     LIMIT 1;

    IF v_porcentaje IS NULL THEN
        v_porcentaje  := 2.0;
        v_dias_gracia := 3;
    END IF;

    FOR rec IN
        SELECT id, usuario_id, rol, reputacion, ultima_sesion
          FROM public.roles_usuario
         WHERE ultima_sesion < CURRENT_DATE - v_dias_gracia
           AND reputacion > 0
    LOOP
        v_nueva_rep := GREATEST(0, rec.reputacion - (rec.reputacion * v_porcentaje / 100));

        UPDATE public.roles_usuario
           SET reputacion    = v_nueva_rep,
               actualizado_en = now()
         WHERE id = rec.id;

        INSERT INTO public.registro_decaimiento
            (usuario_id, rol, reputacion_antes, reputacion_despues, dias_inactivo)
        VALUES (
            rec.usuario_id,
            rec.rol,
            rec.reputacion,
            v_nueva_rep,
            (CURRENT_DATE - rec.ultima_sesion)::INTEGER
        );
    END LOOP;
END;
$$;

REVOKE ALL ON FUNCTION public.aplicar_decaimiento_reputacion() FROM PUBLIC;
GRANT EXECUTE ON FUNCTION public.aplicar_decaimiento_reputacion() TO service_role;

-- ============================================================
-- function: validar_opcion_correcta()
-- Trigger function: ensures exactly one es_correcta = true per pregunta_id.
-- Raises an exception if a second correct option is inserted/updated.
-- ============================================================
CREATE OR REPLACE FUNCTION public.validar_opcion_correcta()
RETURNS TRIGGER
LANGUAGE plpgsql
SECURITY INVOKER
SET search_path = public
AS $$
DECLARE
    v_count INTEGER;
BEGIN
    IF NEW.es_correcta = true THEN
        SELECT COUNT(*)
          INTO v_count
          FROM public.opciones_pregunta
         WHERE pregunta_id = NEW.pregunta_id
           AND es_correcta = true
           AND id <> NEW.id;

        IF v_count > 0 THEN
            RAISE EXCEPTION
                'pregunta_id % already has a correct option. Only one es_correcta = true is allowed per question.',
                NEW.pregunta_id;
        END IF;
    END IF;

    RETURN NEW;
END;
$$;
