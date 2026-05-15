-- Migration 012: Auth helper functions
-- hash_respuesta_seguridad, verificar_respuesta_seguridad, registrar_intento_auth
-- Uses extensions.crypt / extensions.gen_salt (pgcrypto installed in extensions schema)
-- Depends on: 001_extensiones (pgcrypto), 002_tablas_core (intentos_auth)

-- ============================================================
-- function: hash_respuesta_seguridad(respuesta TEXT)
-- Returns a bcrypt hash (cost 10) of the security answer.
-- Called by /auth/register Edge Function (service_role).
-- ============================================================
CREATE OR REPLACE FUNCTION public.hash_respuesta_seguridad(respuesta TEXT)
RETURNS TEXT
LANGUAGE sql
SECURITY DEFINER
SET search_path = extensions, public
AS $$
    SELECT extensions.crypt(respuesta, extensions.gen_salt('bf', 10));
$$;

REVOKE ALL ON FUNCTION public.hash_respuesta_seguridad(TEXT) FROM PUBLIC;
GRANT EXECUTE ON FUNCTION public.hash_respuesta_seguridad(TEXT) TO service_role;

-- ============================================================
-- function: verificar_respuesta_seguridad(respuesta_ingresada TEXT, hash_guardado TEXT)
-- Returns true if the plaintext answer matches the stored bcrypt hash.
-- Called by /auth/recover Edge Function (service_role).
-- ============================================================
CREATE OR REPLACE FUNCTION public.verificar_respuesta_seguridad(
    respuesta_ingresada TEXT,
    hash_guardado       TEXT
)
RETURNS BOOLEAN
LANGUAGE sql
SECURITY DEFINER
SET search_path = extensions, public
AS $$
    SELECT extensions.crypt(respuesta_ingresada, hash_guardado) = hash_guardado;
$$;

REVOKE ALL ON FUNCTION public.verificar_respuesta_seguridad(TEXT, TEXT) FROM PUBLIC;
GRANT EXECUTE ON FUNCTION public.verificar_respuesta_seguridad(TEXT, TEXT) TO service_role;

-- ============================================================
-- function: registrar_intento_auth(p_dni CHAR(8))
-- Rate-limiting for /auth/recover: 5 attempts per DNI per hour.
-- Upserts into intentos_auth.
-- Returns (intentos INTEGER, bloqueado BOOLEAN).
-- ============================================================
CREATE OR REPLACE FUNCTION public.registrar_intento_auth(p_dni CHAR(8))
RETURNS TABLE (intentos INTEGER, bloqueado BOOLEAN)
LANGUAGE plpgsql
SECURITY DEFINER
SET search_path = public
AS $$
DECLARE
    v_intentos    SMALLINT;
    v_ventana     TIMESTAMPTZ;
    v_limite      CONSTANT SMALLINT := 5;
    v_ventana_hrs CONSTANT INTERVAL := '1 hour';
BEGIN
    SELECT ia.intentos, ia.ventana_inicio
      INTO v_intentos, v_ventana
      FROM public.intentos_auth ia
     WHERE ia.dni = p_dni;

    IF NOT FOUND THEN
        -- First attempt for this DNI
        INSERT INTO public.intentos_auth (dni, intentos, ventana_inicio)
        VALUES (p_dni, 1, now());

        RETURN QUERY SELECT 1::INTEGER, false;
        RETURN;
    END IF;

    IF now() - v_ventana >= v_ventana_hrs THEN
        -- Window expired: reset counter
        UPDATE public.intentos_auth
           SET intentos       = 1,
               ventana_inicio = now()
         WHERE dni = p_dni;

        RETURN QUERY SELECT 1::INTEGER, false;
        RETURN;
    END IF;

    -- Within the active window: increment
    UPDATE public.intentos_auth
       SET intentos = intentos + 1
     WHERE dni = p_dni
    RETURNING intentos INTO v_intentos;

    RETURN QUERY SELECT v_intentos::INTEGER, (v_intentos >= v_limite);
END;
$$;

REVOKE ALL ON FUNCTION public.registrar_intento_auth(CHAR) FROM PUBLIC;
GRANT EXECUTE ON FUNCTION public.registrar_intento_auth(CHAR) TO service_role;
