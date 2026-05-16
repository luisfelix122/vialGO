package com.vialgo.app.datos.dtos

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class BeneficioDto(
    @SerialName("id") val id: String,
    @SerialName("titulo") val titulo: String,
    @SerialName("descripcion") val descripcion: String,
    @SerialName("imagen_url") val imagenUrl: String? = null,
    @SerialName("rol") val rol: String,
    @SerialName("reputacion_minima") val reputacionMinima: Double,
    @SerialName("esta_activo") val estaActivo: Boolean = true,
    @SerialName("disponible") val disponible: Boolean = false,
    @SerialName("orden") val orden: Int = 0,
)
