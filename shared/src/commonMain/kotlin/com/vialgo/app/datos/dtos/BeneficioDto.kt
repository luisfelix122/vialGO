package com.vialgo.app.datos.dtos

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class BeneficioDto(
    @SerialName("id") val id: String,
    @SerialName("titulo") val titulo: String,
    @SerialName("descripcion") val descripcion: String,
    @SerialName("url_imagen") val urlImagen: String?,
    @SerialName("puntos_requeridos") val puntosRequeridos: Int,
    @SerialName("categoria") val categoria: String,
    @SerialName("disponible") val disponible: Boolean,
)
