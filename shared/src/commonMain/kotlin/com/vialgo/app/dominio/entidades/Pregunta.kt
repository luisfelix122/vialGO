package com.vialgo.app.dominio.entidades

data class Pregunta(
    val id: String,
    val categoriaId: String,
    val leccionId: String?,
    val enunciado: String,
    val tipoMedio: String,
    val urlMedio: String,
    val duracionMedioSeg: Int?,
    val textoConsecuencia: String,
    val esClasificacion: Boolean,
    val estaActiva: Boolean,
    val opciones: List<OpcionPregunta>,
)
