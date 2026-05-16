package com.vialgo.app.datos.dtos

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

/**
 * DTO que mapea una fila de la tabla `usuarios` de Supabase.
 * Contiene únicamente las columnas presentes en la tabla — no incluye
 * campos de gamificación que provienen de otras tablas.
 */
@Serializable
data class UsuarioTablaDto(
    @SerialName("id") val id: String,
    @SerialName("dni") val dni: String,
    @SerialName("nombre") val nombre: String,
    @SerialName("rol_activo") val rolActivo: String,
    @SerialName("compromiso_minutos") val compromisoMinutos: Int,
    @SerialName("tutorial_completado") val tutorialCompletado: Boolean = false,
    @SerialName("debe_cambiar_pregunta") val debeCambiarPregunta: Boolean = false,
    @SerialName("fecha_registro") val fechaRegistro: String = "",
    @SerialName("actualizado_en") val actualizadoEn: String = "",
)
