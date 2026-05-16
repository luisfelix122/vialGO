-- Migration: Restore anon CRUD on content tables for admin dashboard
-- Context: Admin dashboard uses 'use client' components (browser-side) with the anon key.
--          Content tables (modulos, lecciones, categorias_pregunta, preguntas, opciones_pregunta)
--          hold educational content only — no user PII. The dashboard is internal/dev-only.
--          Using service_role from browser is not an option (key would be exposed in the bundle).
-- Depends on: 20260516000001_rls_auth_session_fix

-- ============================================================
-- modulos
-- ============================================================

CREATE POLICY anon_insert_modulos
    ON public.modulos FOR INSERT
    TO anon
    WITH CHECK (true);

CREATE POLICY anon_update_modulos
    ON public.modulos FOR UPDATE
    TO anon
    USING (true)
    WITH CHECK (true);

CREATE POLICY anon_delete_modulos
    ON public.modulos FOR DELETE
    TO anon
    USING (true);

-- ============================================================
-- lecciones
-- ============================================================

CREATE POLICY anon_insert_lecciones
    ON public.lecciones FOR INSERT
    TO anon
    WITH CHECK (true);

CREATE POLICY anon_update_lecciones
    ON public.lecciones FOR UPDATE
    TO anon
    USING (true)
    WITH CHECK (true);

CREATE POLICY anon_delete_lecciones
    ON public.lecciones FOR DELETE
    TO anon
    USING (true);

-- ============================================================
-- categorias_pregunta
-- ============================================================

CREATE POLICY anon_insert_categorias_pregunta
    ON public.categorias_pregunta FOR INSERT
    TO anon
    WITH CHECK (true);

CREATE POLICY anon_update_categorias_pregunta
    ON public.categorias_pregunta FOR UPDATE
    TO anon
    USING (true)
    WITH CHECK (true);

CREATE POLICY anon_delete_categorias_pregunta
    ON public.categorias_pregunta FOR DELETE
    TO anon
    USING (true);

-- ============================================================
-- preguntas
-- ============================================================

CREATE POLICY anon_insert_preguntas
    ON public.preguntas FOR INSERT
    TO anon
    WITH CHECK (true);

CREATE POLICY anon_update_preguntas
    ON public.preguntas FOR UPDATE
    TO anon
    USING (true)
    WITH CHECK (true);

CREATE POLICY anon_delete_preguntas
    ON public.preguntas FOR DELETE
    TO anon
    USING (true);

-- ============================================================
-- opciones_pregunta
-- ============================================================

CREATE POLICY anon_insert_opciones_pregunta
    ON public.opciones_pregunta FOR INSERT
    TO anon
    WITH CHECK (true);

CREATE POLICY anon_update_opciones_pregunta
    ON public.opciones_pregunta FOR UPDATE
    TO anon
    USING (true)
    WITH CHECK (true);

CREATE POLICY anon_delete_opciones_pregunta
    ON public.opciones_pregunta FOR DELETE
    TO anon
    USING (true);
