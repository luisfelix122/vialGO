package com.vialgo.app.datos.dtos

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class PreguntaDto(
    @SerialName("id") val id: String,
    @SerialName("categoria_id") val categoriaId: String,
    @SerialName("leccion_id") val leccionId: String? = null,
    @SerialName("enunciado") val enunciado: String,
    @SerialName("tipo_medio") val tipoMedio: String,
    @SerialName("url_medio") val urlMedio: String,
    @SerialName("duracion_medio_seg") val duracionMedioSeg: Int? = null,
    @SerialName("texto_consecuencia") val textoConsecuencia: String,
    @SerialName("es_clasificacion") val esClasificacion: Boolean = false,
    @SerialName("esta_activa") val estaActiva: Boolean = true,
    @SerialName("creado_en") val creadoEn: String? = null,
)

@Serializable
data class OpcionPreguntaDto(
    @SerialName("id") val id: String,
    @SerialName("pregunta_id") val preguntaId: String,
    @SerialName("texto") val texto: String,
    @SerialName("imagen_url") val imagenUrl: String? = null,
    @SerialName("es_correcta") val esCorrecta: Boolean,
    @SerialName("orden") val orden: Int,
)
