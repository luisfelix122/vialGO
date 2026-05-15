package com.vialgo.app.dominio.entidades

import kotlinx.datetime.Instant

data class Sesion(
    val id: String,
    val usuarioId: String,
    val leccionId: String?,
    val rol: String,
    val tipo: String,
    val estado: String,
    val fueMinimizada: Boolean,
    val xpGanado: Int,
    val preguntasTotales: Int,
    val iniciadaEn: Instant,
    val completadaEn: Instant?,
)
