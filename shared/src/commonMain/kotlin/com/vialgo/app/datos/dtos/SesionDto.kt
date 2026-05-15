package com.vialgo.app.datos.dtos

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class SesionDto(
    @SerialName("id") val id: String = "",
    @SerialName("usuario_id") val usuarioId: String,
    @SerialName("leccion_id") val leccionId: String? = null,
    @SerialName("rol") val rol: String,
    @SerialName("tipo") val tipo: String,
    @SerialName("estado") val estado: String = "en_progreso",
    @SerialName("fue_minimizada") val fueMinimizada: Boolean = false,
    @SerialName("iniciada_en") val iniciadaEn: String = "",
    @SerialName("completada_en") val completadaEn: String? = null,
    @SerialName("xp_ganado") val xpGanado: Int = 0,
    @SerialName("preguntas_totales") val preguntasTotales: Int = 0,
)
