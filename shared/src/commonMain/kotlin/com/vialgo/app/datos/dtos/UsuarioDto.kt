package com.vialgo.app.datos.dtos

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class UsuarioDto(
    @SerialName("id") val id: String,
    @SerialName("correo") val correo: String = "",
    @SerialName("nombre") val nombre: String,
    @SerialName("rol") val rol: String = "",
    @SerialName("vidas") val vidas: Int = 0,
    @SerialName("racha_actual") val rachaActual: Int = 0,
    @SerialName("racha_mas_larga") val rachaMasLarga: Int = 0,
    @SerialName("puntos_experiencia") val puntosExperiencia: Int = 0,
    @SerialName("nivel") val nivel: Int = 0,
    @SerialName("creado_en") val creadoEn: String = "",
    @SerialName("actualizado_en") val actualizadoEn: String = "",
    // Auth fields
    @SerialName("dni") val dni: String = "",
    @SerialName("pregunta_seguridad") val preguntaSeguridad: String? = null,
    @SerialName("rol_activo") val rolActivo: String = "",
    @SerialName("compromiso_minutos") val compromisoMinutos: Int = 0,
    @SerialName("tutorial_completado") val tutorialCompletado: Boolean = false,
    @SerialName("debe_cambiar_pregunta") val debeCambiarPregunta: Boolean = false,
)
