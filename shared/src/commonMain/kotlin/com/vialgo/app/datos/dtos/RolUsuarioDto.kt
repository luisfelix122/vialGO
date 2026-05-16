package com.vialgo.app.datos.dtos

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class RolUsuarioDto(
    @SerialName("id") val id: String,
    @SerialName("usuario_id") val usuarioId: String,
    @SerialName("rol") val rol: String,
    @SerialName("xp_total") val xpTotal: Int = 0,
    @SerialName("reputacion") val reputacion: Double = 0.0,
    @SerialName("racha_dias") val rachaDias: Int = 0,
    @SerialName("racha_maxima") val rachaMaxima: Int = 0,
    @SerialName("ultima_sesion") val ultimaSesion: String? = null,
    @SerialName("sesion_completada_hoy") val sesionCompletadaHoy: String? = null,
    @SerialName("clasificacion_completada") val clasificacionCompletada: Boolean = false,
    @SerialName("actualizado_en") val actualizadoEn: String = "",
)
