-- Helper functions called by Edge Functions for security answer hashing
-- These use pgcrypto's crypt() which requires the pgcrypto extension (enabled in 001_extensiones)

-- Hash a plaintext answer using bcrypt (cost factor 10)
CREATE OR REPLACE FUNCTION public.hash_respuesta_seguridad(respuesta TEXT)
RETURNS TEXT
LANGUAGE sql
SECURITY DEFINER
SET search_path = public
AS $$
  SELECT crypt(respuesta, gen_salt('bf', 10));
$$;

-- Verify a plaintext answer against a stored bcrypt hash
CREATE OR REPLACE FUNCTION public.verificar_respuesta_seguridad(
  respuesta_ingresada TEXT,
  hash_guardado TEXT
)
RETURNS BOOLEAN
LANGUAGE sql
SECURITY DEFINER
SET search_path = public
AS $$
  SELECT crypt(respuesta_ingresada, hash_guardado) = hash_guardado;
$$;

-- Grant execute to service_role only (Edge Functions run as service_role)
REVOKE ALL ON FUNCTION public.hash_respuesta_seguridad(TEXT) FROM PUBLIC;
REVOKE ALL ON FUNCTION public.verificar_respuesta_seguridad(TEXT, TEXT) FROM PUBLIC;

GRANT EXECUTE ON FUNCTION public.hash_respuesta_seguridad(TEXT) TO service_role;
GRANT EXECUTE ON FUNCTION public.verificar_respuesta_seguridad(TEXT, TEXT) TO service_role;
