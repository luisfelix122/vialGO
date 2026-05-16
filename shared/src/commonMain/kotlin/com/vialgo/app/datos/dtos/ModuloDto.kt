package com.vialgo.app.datos.dtos

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class ModuloDto(
    @SerialName("id") val id: String,
    @SerialName("nombre") val nombre: String,
    @SerialName("descripcion") val descripcion: String,
    @SerialName("orden") val orden: Int,
    @SerialName("rol") val rol: String,
    @SerialName("esta_activo") val estaActivo: Boolean,
)
