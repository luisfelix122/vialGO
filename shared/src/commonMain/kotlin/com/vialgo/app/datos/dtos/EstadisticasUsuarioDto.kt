package com.vialgo.app.datos.dtos

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class EstadisticasUsuarioDto(
    @SerialName("id") val id: String,
    @SerialName("usuario_id") val usuarioId: String,
    @SerialName("rol") val rol: String,
    @SerialName("total_sesiones") val totalSesiones: Int = 0,
    @SerialName("total_preguntas") val totalPreguntas: Int = 0,
    @SerialName("total_correctas") val totalCorrectas: Int = 0,
    @SerialName("tiempo_total_ms") val tiempoTotalMs: Long = 0,
    @SerialName("vidas_salvadas") val vidasSalvadas: Int = 0,
    @SerialName("actualizado_en") val actualizadoEn: String = "",
)
