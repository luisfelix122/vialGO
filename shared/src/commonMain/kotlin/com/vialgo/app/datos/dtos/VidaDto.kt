package com.vialgo.app.datos.dtos

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class VidaDto(
    @SerialName("id") val id: String,
    @SerialName("usuario_id") val usuarioId: String,
    @SerialName("vidas_actuales") val vidasActuales: Int,
    @SerialName("ultima_recarga") val ultimaRecarga: String = "",
    @SerialName("actualizado_en") val actualizadoEn: String = "",
)
