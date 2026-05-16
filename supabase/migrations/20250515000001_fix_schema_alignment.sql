-- Migration: Fix schema alignment
-- Adds missing enunciado column to preguntas
-- Adds permissive anon RLS policies on all content tables

-- ============================================================
-- 1. Add enunciado column to preguntas
-- ============================================================
ALTER TABLE preguntas
  ADD COLUMN IF NOT EXISTS enunciado TEXT NOT NULL DEFAULT '';

-- ============================================================
-- 2. Permissive anon RLS policies — modulos
-- ============================================================
DROP POLICY IF EXISTS "anon_select_modulos" ON modulos;
DROP POLICY IF EXISTS "anon_insert_modulos" ON modulos;
DROP POLICY IF EXISTS "anon_update_modulos" ON modulos;
DROP POLICY IF EXISTS "anon_delete_modulos" ON modulos;

CREATE POLICY "anon_select_modulos" ON modulos
  FOR SELECT TO anon USING (true);

CREATE POLICY "anon_insert_modulos" ON modulos
  FOR INSERT TO anon WITH CHECK (true);

CREATE POLICY "anon_update_modulos" ON modulos
  FOR UPDATE TO anon USING (true) WITH CHECK (true);

CREATE POLICY "anon_delete_modulos" ON modulos
  FOR DELETE TO anon USING (true);

-- ============================================================
-- 3. Permissive anon RLS policies — lecciones
-- ============================================================
DROP POLICY IF EXISTS "anon_select_lecciones" ON lecciones;
DROP POLICY IF EXISTS "anon_insert_lecciones" ON lecciones;
DROP POLICY IF EXISTS "anon_update_lecciones" ON lecciones;
DROP POLICY IF EXISTS "anon_delete_lecciones" ON lecciones;

CREATE POLICY "anon_select_lecciones" ON lecciones
  FOR SELECT TO anon USING (true);

CREATE POLICY "anon_insert_lecciones" ON lecciones
  FOR INSERT TO anon WITH CHECK (true);

CREATE POLICY "anon_update_lecciones" ON lecciones
  FOR UPDATE TO anon USING (true) WITH CHECK (true);

CREATE POLICY "anon_delete_lecciones" ON lecciones
  FOR DELETE TO anon USING (true);

-- ============================================================
-- 4. Permissive anon RLS policies — categorias_pregunta
-- ============================================================
DROP POLICY IF EXISTS "anon_select_categorias_pregunta" ON categorias_pregunta;
DROP POLICY IF EXISTS "anon_insert_categorias_pregunta" ON categorias_pregunta;
DROP POLICY IF EXISTS "anon_update_categorias_pregunta" ON categorias_pregunta;
DROP POLICY IF EXISTS "anon_delete_categorias_pregunta" ON categorias_pregunta;

CREATE POLICY "anon_select_categorias_pregunta" ON categorias_pregunta
  FOR SELECT TO anon USING (true);

CREATE POLICY "anon_insert_categorias_pregunta" ON categorias_pregunta
  FOR INSERT TO anon WITH CHECK (true);

CREATE POLICY "anon_update_categorias_pregunta" ON categorias_pregunta
  FOR UPDATE TO anon USING (true) WITH CHECK (true);

CREATE POLICY "anon_delete_categorias_pregunta" ON categorias_pregunta
  FOR DELETE TO anon USING (true);

-- ============================================================
-- 5. Permissive anon RLS policies — preguntas
-- ============================================================
DROP POLICY IF EXISTS "anon_select_preguntas" ON preguntas;
DROP POLICY IF EXISTS "anon_insert_preguntas" ON preguntas;
DROP POLICY IF EXISTS "anon_update_preguntas" ON preguntas;
DROP POLICY IF EXISTS "anon_delete_preguntas" ON preguntas;

CREATE POLICY "anon_select_preguntas" ON preguntas
  FOR SELECT TO anon USING (true);

CREATE POLICY "anon_insert_preguntas" ON preguntas
  FOR INSERT TO anon WITH CHECK (true);

CREATE POLICY "anon_update_preguntas" ON preguntas
  FOR UPDATE TO anon USING (true) WITH CHECK (true);

CREATE POLICY "anon_delete_preguntas" ON preguntas
  FOR DELETE TO anon USING (true);

-- ============================================================
-- 6. Permissive anon RLS policies — opciones_pregunta
-- ============================================================
DROP POLICY IF EXISTS "anon_select_opciones_pregunta" ON opciones_pregunta;
DROP POLICY IF EXISTS "anon_insert_opciones_pregunta" ON opciones_pregunta;
DROP POLICY IF EXISTS "anon_update_opciones_pregunta" ON opciones_pregunta;
DROP POLICY IF EXISTS "anon_delete_opciones_pregunta" ON opciones_pregunta;

CREATE POLICY "anon_select_opciones_pregunta" ON opciones_pregunta
  FOR SELECT TO anon USING (true);

CREATE POLICY "anon_insert_opciones_pregunta" ON opciones_pregunta
  FOR INSERT TO anon WITH CHECK (true);

CREATE POLICY "anon_update_opciones_pregunta" ON opciones_pregunta
  FOR UPDATE TO anon USING (true) WITH CHECK (true);

CREATE POLICY "anon_delete_opciones_pregunta" ON opciones_pregunta
  FOR DELETE TO anon USING (true);
