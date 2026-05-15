package com.vialgo.app.datos.dtos

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class RespuestaSesionDto(
    @SerialName("id") val id: String = "",
    @SerialName("sesion_id") val sesionId: String,
    @SerialName("pregunta_id") val preguntaId: String,
    @SerialName("opcion_id") val opcionId: String,
    @SerialName("fue_correcta") val fueCorrecta: Boolean,
    @SerialName("tiempo_respuesta_ms") val tiempoRespuestaMs: Int,
    @SerialName("xp_obtenido") val xpObtenido: Int? = null,
    @SerialName("es_reintento") val esReintento: Boolean = false,
)
