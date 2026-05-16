package com.vialgo.app.dominio.entidades

data class OpcionPregunta(
    val id: String,
    val preguntaId: String,
    val texto: String,
    val imagenUrl: String? = null,
    val esCorrecta: Boolean,
    val orden: Int,
)
