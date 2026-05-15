package com.vialgo.app.dominio.entidades

data class RespuestaUsuario(
    val id: String = "",
    val sesionId: String,
    val preguntaId: String,
    val opcionId: String,
    val fueCorrecta: Boolean,
    val tiempoRespuestaMs: Int,
    val xpObtenido: Int = 0,
    val esReintento: Boolean = false,
)
