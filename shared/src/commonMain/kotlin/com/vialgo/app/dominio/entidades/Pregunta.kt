package com.vialgo.app.dominio.entidades

data class Pregunta(
    val id: String,
    val leccionId: String?,
    val enunciado: String,
    val tipo: TipoPregunta,
    val urlImagen: String?,
    val urlVideo: String?,
    val orden: Int,
    val opciones: List<OpcionPregunta>,
)

enum class TipoPregunta {
    OPCION_MULTIPLE,
    VERDADERO_FALSO,
    VIDEO_OPCION,
}
