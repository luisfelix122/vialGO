package com.vialgo.app.dominio.entidades

import kotlinx.datetime.Instant

data class EstadisticasUsuario(
    val id: String,
    val usuarioId: String,
    val rol: String,
    val totalSesiones: Int,
    val totalPreguntas: Int,
    val totalCorrectas: Int,
    val tiempoTotalMs: Long,
    val vidasSalvadas: Int,
    val actualizadoEn: Instant,
)
