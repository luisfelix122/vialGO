package com.vialgo.app.datos.dtos

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class ProgresoLeccionDto(
    @SerialName("id") val id: String,
    @SerialName("usuario_id") val usuarioId: String,
    @SerialName("leccion_id") val leccionId: String,
    @SerialName("rol") val rol: String,
    @SerialName("completada") val completada: Boolean,
    @SerialName("estrellas") val estrellas: Int = 0,
    @SerialName("mejor_xp") val mejorXp: Int = 0,
    @SerialName("completada_en") val completadaEn: String? = null,
    @SerialName("actualizado_en") val actualizadoEn: String,
)
