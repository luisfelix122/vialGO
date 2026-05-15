package com.vialgo.app.datos.dtos

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class RegistroRequestDto(
    @SerialName("dni") val dni: String,
    @SerialName("password") val password: String,
    @SerialName("nombre") val nombre: String,
    @SerialName("pregunta_seguridad") val preguntaSeguridad: String,
    @SerialName("respuesta_seguridad") val respuestaSeguridad: String,
    @SerialName("rol_activo") val rolActivo: String,
    @SerialName("compromiso_minutos") val compromisoMinutos: Int,
)
