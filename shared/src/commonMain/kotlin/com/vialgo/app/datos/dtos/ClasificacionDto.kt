package com.vialgo.app.datos.dtos

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class ClasificacionDto(
    @SerialName("id") val id: String,
    @SerialName("usuario_id") val usuarioId: String,
    @SerialName("rol") val rol: String,
    @SerialName("sesion_id") val sesionId: String,
    @SerialName("reputacion_inicial") val reputacionInicial: Double,
    @SerialName("completada_en") val completadaEn: String = "",
)
