-- Migration 011: Row-Level Security — enable + policies for all 21 tables
-- Depends on: 002 through 006 (all tables must exist)

-- ============================================================
-- Enable RLS on all 21 tables
-- ============================================================

ALTER TABLE public.usuarios                  ENABLE ROW LEVEL SECURITY;
ALTER TABLE public.roles_usuario             ENABLE ROW LEVEL SECURITY;
ALTER TABLE public.historial_rachas          ENABLE ROW LEVEL SECURITY;
ALTER TABLE public.vidas_usuario             ENABLE ROW LEVEL SECURITY;
ALTER TABLE public.modulos                   ENABLE ROW LEVEL SECURITY;
ALTER TABLE public.lecciones                 ENABLE ROW LEVEL SECURITY;
ALTER TABLE public.progreso_lecciones        ENABLE ROW LEVEL SECURITY;
ALTER TABLE public.categorias_pregunta       ENABLE ROW LEVEL SECURITY;
ALTER TABLE public.preguntas                 ENABLE ROW LEVEL SECURITY;
ALTER TABLE public.opciones_pregunta         ENABLE ROW LEVEL SECURITY;
ALTER TABLE public.sesiones                  ENABLE ROW LEVEL SECURITY;
ALTER TABLE public.respuestas_sesion         ENABLE ROW LEVEL SECURITY;
ALTER TABLE public.clasificaciones           ENABLE ROW LEVEL SECURITY;
ALTER TABLE public.beneficios                ENABLE ROW LEVEL SECURITY;
ALTER TABLE public.configuracion_juego       ENABLE ROW LEVEL SECURITY;
ALTER TABLE public.notificaciones_programadas ENABLE ROW LEVEL SECURITY;
ALTER TABLE public.reportes_error            ENABLE ROW LEVEL SECURITY;
ALTER TABLE public.analiticas_preguntas      ENABLE ROW LEVEL SECURITY;
ALTER TABLE public.registro_decaimiento      ENABLE ROW LEVEL SECURITY;
ALTER TABLE public.tokens_dispositivo        ENABLE ROW LEVEL SECURITY;
ALTER TABLE public.estadisticas_usuario      ENABLE ROW LEVEL SECURITY;

-- ============================================================
-- tabla: usuarios
-- SELECT own row; INSERT via service_role only (Edge Function);
-- UPDATE own row while not banned and not soft-deleted.
-- ============================================================

CREATE POLICY pol_usuarios_select_own
    ON public.usuarios FOR SELECT
    TO authenticated
    USING (id = auth.uid());

CREATE POLICY pol_usuarios_insert_service
    ON public.usuarios FOR INSERT
    TO service_role
    WITH CHECK (true);

CREATE POLICY pol_usuarios_update_own
    ON public.usuarios FOR UPDATE
    TO authenticated
    USING (
        id = auth.uid()
        AND esta_baneado = false
        AND eliminado_en IS NULL
    )
    WITH CHECK (
        id = auth.uid()
        AND esta_baneado = false
        AND eliminado_en IS NULL
    );

-- ============================================================
-- tabla: roles_usuario
-- ============================================================

CREATE POLICY pol_roles_usuario_select_own
    ON public.roles_usuario FOR SELECT
    TO authenticated
    USING (usuario_id = auth.uid());

CREATE POLICY pol_roles_usuario_insert_own
    ON public.roles_usuario FOR INSERT
    TO authenticated
    WITH CHECK (usuario_id = auth.uid());

CREATE POLICY pol_roles_usuario_update_own
    ON public.roles_usuario FOR UPDATE
    TO authenticated
    USING (usuario_id = auth.uid())
    WITH CHECK (usuario_id = auth.uid());

-- ============================================================
-- tabla: historial_rachas
-- Users can read their own history; only system (trigger) inserts.
-- ============================================================

CREATE POLICY pol_historial_rachas_select_own
    ON public.historial_rachas FOR SELECT
    TO authenticated
    USING (usuario_id = auth.uid());

CREATE POLICY pol_historial_rachas_insert_service
    ON public.historial_rachas FOR INSERT
    TO service_role
    WITH CHECK (true);

-- ============================================================
-- tabla: vidas_usuario
-- ============================================================

CREATE POLICY pol_vidas_usuario_select_own
    ON public.vidas_usuario FOR SELECT
    TO authenticated
    USING (usuario_id = auth.uid());

CREATE POLICY pol_vidas_usuario_insert_service
    ON public.vidas_usuario FOR INSERT
    TO service_role
    WITH CHECK (true);

CREATE POLICY pol_vidas_usuario_update_own
    ON public.vidas_usuario FOR UPDATE
    TO authenticated
    USING (usuario_id = auth.uid())
    WITH CHECK (usuario_id = auth.uid());

-- ============================================================
-- tabla: modulos
-- All authenticated users can read; admin (service_role) manages.
-- ============================================================

CREATE POLICY pol_modulos_select_all
    ON public.modulos FOR SELECT
    TO authenticated
    USING (esta_activo = true);

CREATE POLICY pol_modulos_insert_service
    ON public.modulos FOR INSERT
    TO service_role
    WITH CHECK (true);

CREATE POLICY pol_modulos_update_service
    ON public.modulos FOR UPDATE
    TO service_role
    USING (true)
    WITH CHECK (true);

-- ============================================================
-- tabla: lecciones
-- ============================================================

CREATE POLICY pol_lecciones_select_all
    ON public.lecciones FOR SELECT
    TO authenticated
    USING (esta_activa = true);

CREATE POLICY pol_lecciones_insert_service
    ON public.lecciones FOR INSERT
    TO service_role
    WITH CHECK (true);

CREATE POLICY pol_lecciones_update_service
    ON public.lecciones FOR UPDATE
    TO service_role
    USING (true)
    WITH CHECK (true);

-- ============================================================
-- tabla: progreso_lecciones
-- ============================================================

CREATE POLICY pol_progreso_lecciones_select_own
    ON public.progreso_lecciones FOR SELECT
    TO authenticated
    USING (usuario_id = auth.uid());

CREATE POLICY pol_progreso_lecciones_insert_own
    ON public.progreso_lecciones FOR INSERT
    TO authenticated
    WITH CHECK (usuario_id = auth.uid());

CREATE POLICY pol_progreso_lecciones_update_own
    ON public.progreso_lecciones FOR UPDATE
    TO authenticated
    USING (usuario_id = auth.uid())
    WITH CHECK (usuario_id = auth.uid());

-- ============================================================
-- tabla: categorias_pregunta
-- ============================================================

CREATE POLICY pol_categorias_pregunta_select_all
    ON public.categorias_pregunta FOR SELECT
    TO authenticated
    USING (true);

CREATE POLICY pol_categorias_pregunta_insert_service
    ON public.categorias_pregunta FOR INSERT
    TO service_role
    WITH CHECK (true);

CREATE POLICY pol_categorias_pregunta_update_service
    ON public.categorias_pregunta FOR UPDATE
    TO service_role
    USING (true)
    WITH CHECK (true);

-- ============================================================
-- tabla: preguntas
-- Authenticated users see only active questions.
-- ============================================================

CREATE POLICY pol_preguntas_select_active
    ON public.preguntas FOR SELECT
    TO authenticated
    USING (esta_activa = true);

CREATE POLICY pol_preguntas_insert_service
    ON public.preguntas FOR INSERT
    TO service_role
    WITH CHECK (true);

CREATE POLICY pol_preguntas_update_service
    ON public.preguntas FOR UPDATE
    TO service_role
    USING (true)
    WITH CHECK (true);

-- ============================================================
-- tabla: opciones_pregunta
-- ============================================================

CREATE POLICY pol_opciones_pregunta_select_all
    ON public.opciones_pregunta FOR SELECT
    TO authenticated
    USING (true);

CREATE POLICY pol_opciones_pregunta_insert_service
    ON public.opciones_pregunta FOR INSERT
    TO service_role
    WITH CHECK (true);

CREATE POLICY pol_opciones_pregunta_update_service
    ON public.opciones_pregunta FOR UPDATE
    TO service_role
    USING (true)
    WITH CHECK (true);

-- ============================================================
-- tabla: sesiones
-- ============================================================

CREATE POLICY pol_sesiones_select_own
    ON public.sesiones FOR SELECT
    TO authenticated
    USING (usuario_id = auth.uid());

CREATE POLICY pol_sesiones_insert_own
    ON public.sesiones FOR INSERT
    TO authenticated
    WITH CHECK (usuario_id = auth.uid());

CREATE POLICY pol_sesiones_update_own
    ON public.sesiones FOR UPDATE
    TO authenticated
    USING (usuario_id = auth.uid())
    WITH CHECK (usuario_id = auth.uid());

-- ============================================================
-- tabla: respuestas_sesion
-- Users can insert and read their own answers; no updates.
-- ============================================================

CREATE POLICY pol_respuestas_sesion_select_own
    ON public.respuestas_sesion FOR SELECT
    TO authenticated
    USING (
        sesion_id IN (
            SELECT id FROM public.sesiones WHERE usuario_id = auth.uid()
        )
    );

CREATE POLICY pol_respuestas_sesion_insert_own
    ON public.respuestas_sesion FOR INSERT
    TO authenticated
    WITH CHECK (
        sesion_id IN (
            SELECT id FROM public.sesiones WHERE usuario_id = auth.uid()
        )
    );

-- ============================================================
-- tabla: clasificaciones
-- ============================================================

CREATE POLICY pol_clasificaciones_select_own
    ON public.clasificaciones FOR SELECT
    TO authenticated
    USING (usuario_id = auth.uid());

CREATE POLICY pol_clasificaciones_insert_own
    ON public.clasificaciones FOR INSERT
    TO authenticated
    WITH CHECK (usuario_id = auth.uid());

-- ============================================================
-- tabla: beneficios
-- All authenticated users can read.
-- ============================================================

CREATE POLICY pol_beneficios_select_all
    ON public.beneficios FOR SELECT
    TO authenticated
    USING (esta_activo = true);

CREATE POLICY pol_beneficios_insert_service
    ON public.beneficios FOR INSERT
    TO service_role
    WITH CHECK (true);

CREATE POLICY pol_beneficios_update_service
    ON public.beneficios FOR UPDATE
    TO service_role
    USING (true)
    WITH CHECK (true);

-- ============================================================
-- tabla: configuracion_juego
-- All authenticated users can read (singleton, no mutations from client).
-- ============================================================

CREATE POLICY pol_configuracion_juego_select_all
    ON public.configuracion_juego FOR SELECT
    TO authenticated
    USING (true);

CREATE POLICY pol_configuracion_juego_insert_service
    ON public.configuracion_juego FOR INSERT
    TO service_role
    WITH CHECK (true);

CREATE POLICY pol_configuracion_juego_update_service
    ON public.configuracion_juego FOR UPDATE
    TO service_role
    USING (true)
    WITH CHECK (true);

-- ============================================================
-- tabla: notificaciones_programadas
-- Users own their notification schedules.
-- ============================================================

CREATE POLICY pol_notificaciones_select_own
    ON public.notificaciones_programadas FOR SELECT
    TO authenticated
    USING (usuario_id = auth.uid());

CREATE POLICY pol_notificaciones_insert_own
    ON public.notificaciones_programadas FOR INSERT
    TO authenticated
    WITH CHECK (usuario_id = auth.uid());

CREATE POLICY pol_notificaciones_update_own
    ON public.notificaciones_programadas FOR UPDATE
    TO authenticated
    USING (usuario_id = auth.uid())
    WITH CHECK (usuario_id = auth.uid());

CREATE POLICY pol_notificaciones_delete_own
    ON public.notificaciones_programadas FOR DELETE
    TO authenticated
    USING (usuario_id = auth.uid());

-- ============================================================
-- tabla: reportes_error
-- Authenticated users can insert; no reads from client; admin via service_role.
-- ============================================================

CREATE POLICY pol_reportes_error_insert_authenticated
    ON public.reportes_error FOR INSERT
    TO authenticated
    WITH CHECK (true);

CREATE POLICY pol_reportes_error_update_service
    ON public.reportes_error FOR UPDATE
    TO service_role
    USING (true)
    WITH CHECK (true);

-- ============================================================
-- tabla: analiticas_preguntas
-- Authenticated users can read aggregate analytics (no PII).
-- Only system (trigger / service_role) writes.
-- ============================================================

CREATE POLICY pol_analiticas_select_authenticated
    ON public.analiticas_preguntas FOR SELECT
    TO authenticated
    USING (true);

CREATE POLICY pol_analiticas_insert_service
    ON public.analiticas_preguntas FOR INSERT
    TO service_role
    WITH CHECK (true);

CREATE POLICY pol_analiticas_update_service
    ON public.analiticas_preguntas FOR UPDATE
    TO service_role
    USING (true)
    WITH CHECK (true);

-- ============================================================
-- tabla: registro_decaimiento
-- Users can read their own decay audit; only service_role inserts.
-- ============================================================

CREATE POLICY pol_registro_decaimiento_select_own
    ON public.registro_decaimiento FOR SELECT
    TO authenticated
    USING (usuario_id = auth.uid());

CREATE POLICY pol_registro_decaimiento_insert_service
    ON public.registro_decaimiento FOR INSERT
    TO service_role
    WITH CHECK (true);

-- ============================================================
-- tabla: tokens_dispositivo
-- Users manage their own device tokens.
-- ============================================================

CREATE POLICY pol_tokens_dispositivo_select_own
    ON public.tokens_dispositivo FOR SELECT
    TO authenticated
    USING (usuario_id = auth.uid());

CREATE POLICY pol_tokens_dispositivo_insert_own
    ON public.tokens_dispositivo FOR INSERT
    TO authenticated
    WITH CHECK (usuario_id = auth.uid());

CREATE POLICY pol_tokens_dispositivo_update_own
    ON public.tokens_dispositivo FOR UPDATE
    TO authenticated
    USING (usuario_id = auth.uid())
    WITH CHECK (usuario_id = auth.uid());

CREATE POLICY pol_tokens_dispositivo_delete_own
    ON public.tokens_dispositivo FOR DELETE
    TO authenticated
    USING (usuario_id = auth.uid());

-- ============================================================
-- tabla: estadisticas_usuario
-- Users can read their own stats; only system writes.
-- ============================================================

CREATE POLICY pol_estadisticas_select_own
    ON public.estadisticas_usuario FOR SELECT
    TO authenticated
    USING (usuario_id = auth.uid());

CREATE POLICY pol_estadisticas_insert_service
    ON public.estadisticas_usuario FOR INSERT
    TO service_role
    WITH CHECK (true);

CREATE POLICY pol_estadisticas_update_service
    ON public.estadisticas_usuario FOR UPDATE
    TO service_role
    USING (true)
    WITH CHECK (true);
