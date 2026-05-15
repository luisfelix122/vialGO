package com.vialgo.app.datos.dtos

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class PreguntaDto(
    @SerialName("id") val id: String,
    @SerialName("leccion_id") val leccionId: String? = null,
    @SerialName("enunciado") val enunciado: String,
    @SerialName("tipo") val tipo: String,
    @SerialName("url_imagen") val urlImagen: String? = null,
    @SerialName("url_video") val urlVideo: String? = null,
    @SerialName("orden") val orden: Int,
    @SerialName("tipo_medio") val tipoMedio: String? = null,
    @SerialName("url_medio") val urlMedio: String? = null,
    @SerialName("es_clasificacion") val esClasificacion: Boolean = false,
)

@Serializable
data class OpcionPreguntaDto(
    @SerialName("id") val id: String,
    @SerialName("pregunta_id") val preguntaId: String,
    @SerialName("texto") val texto: String,
    @SerialName("es_correcta") val esCorrecta: Boolean,
    @SerialName("orden") val orden: Int,
)
