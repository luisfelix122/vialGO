-- Migration: RLS Auth Session Fix
-- 1. Remove overly permissive anon INSERT/UPDATE/DELETE on content tables (keep SELECT).
-- 2. Fix compromiso_minutos CHECK constraint to match UI values [5,10,15,20,30].
-- 3. Open clasificaciones SELECT to all authenticated users (leaderboard).
-- Depends on: 20250515000001_fix_schema_alignment (policies to drop must exist)

-- ============================================================
-- 1. Drop anon INSERT / UPDATE / DELETE on content tables
--    Keep anon SELECT so guest mode can browse content.
-- ============================================================

-- modulos
DROP POLICY IF EXISTS "anon_insert_modulos"        ON public.modulos;
DROP POLICY IF EXISTS "anon_update_modulos"        ON public.modulos;
DROP POLICY IF EXISTS "anon_delete_modulos"        ON public.modulos;

-- lecciones
DROP POLICY IF EXISTS "anon_insert_lecciones"      ON public.lecciones;
DROP POLICY IF EXISTS "anon_update_lecciones"      ON public.lecciones;
DROP POLICY IF EXISTS "anon_delete_lecciones"      ON public.lecciones;

-- categorias_pregunta
DROP POLICY IF EXISTS "anon_insert_categorias_pregunta"  ON public.categorias_pregunta;
DROP POLICY IF EXISTS "anon_update_categorias_pregunta"  ON public.categorias_pregunta;
DROP POLICY IF EXISTS "anon_delete_categorias_pregunta"  ON public.categorias_pregunta;

-- preguntas
DROP POLICY IF EXISTS "anon_insert_preguntas"      ON public.preguntas;
DROP POLICY IF EXISTS "anon_update_preguntas"      ON public.preguntas;
DROP POLICY IF EXISTS "anon_delete_preguntas"      ON public.preguntas;

-- opciones_pregunta
DROP POLICY IF EXISTS "anon_insert_opciones_pregunta"    ON public.opciones_pregunta;
DROP POLICY IF EXISTS "anon_update_opciones_pregunta"    ON public.opciones_pregunta;
DROP POLICY IF EXISTS "anon_delete_opciones_pregunta"    ON public.opciones_pregunta;

-- ============================================================
-- 2. Fix compromiso_minutos CHECK constraint on usuarios.
--    Old: IN (2, 3, 5) — mismatched the UI and Edge Function.
--    New: IN (5, 10, 15, 20, 30) — matches UI options and EF validation.
-- ============================================================

ALTER TABLE public.usuarios
    DROP CONSTRAINT IF EXISTS usuarios_compromiso_minutos_check;

ALTER TABLE public.usuarios
    ADD CONSTRAINT usuarios_compromiso_minutos_check
    CHECK (compromiso_minutos IN (5, 10, 15, 20, 30));

-- ============================================================
-- 3. Open clasificaciones SELECT for leaderboard reads.
--    Old: USING (usuario_id = auth.uid()) — only own row visible.
--    New: USING (true) scoped to authenticated — all rows visible.
-- ============================================================

DROP POLICY IF EXISTS pol_clasificaciones_select_own ON public.clasificaciones;

CREATE POLICY pol_clasificaciones_select_authenticated
    ON public.clasificaciones FOR SELECT
    TO authenticated
    USING (true);
