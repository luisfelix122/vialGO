package com.vialgo.app.datos.dtos

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class RecuperacionRequestDto(
    @SerialName("dni") val dni: String,
    @SerialName("respuesta_seguridad") val respuestaSeguridad: String,
    @SerialName("nueva_password") val nuevaPassword: String,
)
