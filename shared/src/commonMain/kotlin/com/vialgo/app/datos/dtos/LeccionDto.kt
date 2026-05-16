package com.vialgo.app.datos.dtos

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class LeccionDto(
    @SerialName("id") val id: String,
    @SerialName("modulo_id") val moduloId: String,
    @SerialName("nombre") val nombre: String,
    @SerialName("descripcion") val descripcion: String,
    @SerialName("orden") val orden: Int,
    @SerialName("esta_activa") val estaActiva: Boolean,
)
