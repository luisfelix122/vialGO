package com.vialgo.app.datos.dtos

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class ClasificacionDto(
    @SerialName("posicion") val posicion: Int,
    @SerialName("usuario_id") val usuarioId: String,
    @SerialName("nombre_usuario") val nombreUsuario: String,
    @SerialName("puntaje") val puntaje: Int,
    @SerialName("nivel") val nivel: Int,
    @SerialName("rol_usuario") val rolUsuario: String,
)
