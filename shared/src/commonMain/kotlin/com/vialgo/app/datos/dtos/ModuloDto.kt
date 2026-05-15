package com.vialgo.app.datos.dtos

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class ModuloDto(
    @SerialName("id") val id: String,
    @SerialName("titulo") val titulo: String,
    @SerialName("descripcion") val descripcion: String,
    @SerialName("orden") val orden: Int,
    @SerialName("url_imagen_portada") val urlImagenPortada: String?,
    @SerialName("roles_disponibles") val rolesDisponibles: List<String>,
)
