package com.vialgo.app.datos.dtos

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class LeccionDto(
    @SerialName("id") val id: String,
    @SerialName("modulo_id") val moduloId: String,
    @SerialName("titulo") val titulo: String,
    @SerialName("descripcion") val descripcion: String,
    @SerialName("orden") val orden: Int,
    @SerialName("puntaje_maximo") val puntajeMaximo: Int,
    @SerialName("tiempo_limite_segundos") val tiempoLimiteSegundos: Int?,
    @SerialName("url_imagen_portada") val urlImagenPortada: String?,
)
