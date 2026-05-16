package com.vialgo.app.dominio.entidades

import kotlinx.datetime.Instant

data class Vida(
    val id: String,
    val usuarioId: String,
    val vidasActuales: Int,
    val ultimaRecarga: Instant,
    val actualizadoEn: Instant,
)
